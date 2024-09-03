package com.github.nenadjakic.ocr.studio.executor

import com.github.nenadjakic.ocr.studio.exception.ConfigurationException
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ScheduledFuture

@Service
class ParallelizationManagerImpl(
    private val taskScheduler: TaskScheduler
): ParallelizationManager {
    private val runnables: MutableMap<UUID, Executor> = mutableMapOf()
    private val futures: MutableMap<UUID, ScheduledFuture<*>> = mutableMapOf()

    override fun schedule(executor: Executor) {
        if (executor.startDateTime?.isBefore(ZonedDateTime.now()) == true) {
            throw ConfigurationException("Start time is wrong. Cannot schedule task.")
        }

        val future: ScheduledFuture<out Any> = if (executor.startDateTime != null) {
            taskScheduler.schedule({ executor.run() }, executor.startDateTime!!.toInstant())
        } else {
            taskScheduler.schedule({ executor.run() }, Instant.now().plusSeconds(30L))
        }

        runnables[executor.id] = executor
        futures[executor.id] = future
    }

    override fun interrupt(id: UUID): Boolean? = futures[id]?.cancel(true)

    override fun interruptAll(): Map<UUID, Boolean?> {
        val resultMap = mutableMapOf<UUID, Boolean?>()

        for (futureEntry in futures.entries) {
            resultMap.put(futureEntry.key, futureEntry.value.cancel(true))
        }

        return resultMap
    }

    override fun getProgress(id: UUID): ProgressInfo? {
        val runnable = runnables[id]
        return runnable?.progressInfo
    }
}