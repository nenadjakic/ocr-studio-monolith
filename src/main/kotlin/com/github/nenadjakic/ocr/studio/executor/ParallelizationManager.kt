package com.github.nenadjakic.ocr.studio.executor

import java.util.UUID

interface ParallelizationManager {

    fun schedule(executor: Executor)

    fun interrupt(id: UUID): Boolean?

    fun interruptAll(): Map<UUID, Boolean?>

    fun getProgress(id: UUID): ProgressInfo?

    fun clearFinished()

    fun clearInterrupted()

    fun clear() {
        clearInterrupted()
        clearFinished()
    }
}