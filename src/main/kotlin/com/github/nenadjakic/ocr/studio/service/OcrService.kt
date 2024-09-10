package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.MessageConst
import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.OcrProgress
import com.github.nenadjakic.ocr.studio.entity.Status
import com.github.nenadjakic.ocr.studio.exception.MissingDocumentOcrException
import com.github.nenadjakic.ocr.studio.exception.OcrException
import com.github.nenadjakic.ocr.studio.executor.OcrExecutor
import com.github.nenadjakic.ocr.studio.executor.ParallelizationManager
import com.github.nenadjakic.ocr.studio.extension.toOcrProgress
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class OcrService(
    private val ocrProperties: OcrProperties,
    private val parallelizationManager: ParallelizationManager,
    private val tesseractFactory: TesseractFactory,
    private val taskRepository: TaskRepository
) {
    private val logger = LoggerFactory.getLogger(OcrService::class.java)

    fun schedule(id: UUID) {
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }

        if (Status.getInProgressStatuses().contains(task.ocrProgress.status)) {
            throw OcrException("Task with id: $id is in progress and cannot be scheduled.")
        }
        val tesseract = tesseractFactory.create(
            task.ocrConfig.language,
            task.ocrConfig.ocrEngineMode.tesseractValue,
            task.ocrConfig.pageSegmentationMode.tesseractValue, null)

        val executor = OcrExecutor(
            id,
            task.schedulerConfig.startDateTime,
            ocrProperties,
            tesseract,
            taskRepository
        )

        parallelizationManager.schedule(executor)
        task.ocrProgress.status = Status.TRIGGERED
        taskRepository.save(task)
    }

    fun interrupt(id: UUID) {
        val interrupted = parallelizationManager.interrupt(id)

        if (interrupted != null) {
            taskRepository.findById(id).getOrNull()?.let {
                it.ocrProgress.status = Status.INTERRUPTED
                taskRepository.save(it)
            }
        }
    }

    fun interruptAll(id: UUID) {
        val interruptResult = parallelizationManager.interruptAll()
        for (interruptResultEntry in interruptResult.entries) {
            if (interruptResultEntry.value != null) {
                taskRepository.findById(id).getOrNull()?.let {
                    it.ocrProgress.status = Status.INTERRUPTED
                    taskRepository.save(it)
                }
            }
        }
    }

    fun getProgress(id: UUID): OcrProgress {
        val progressInfo = parallelizationManager.getProgress(id)

        if (progressInfo == null) {
            // get progress from datastore
            val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }
            return task.ocrProgress
        } else {
            return progressInfo.toOcrProgress()
        }
    }

    fun clearFinished() {
        parallelizationManager.clearFinished()
    }

    fun clearInterrupted() {
        parallelizationManager.clearInterrupted()
    }

    @Scheduled(cron = "0 0 23 * * ?")
    fun clear() {
        parallelizationManager.clear()
    }
}