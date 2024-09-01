package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.Status
import com.github.nenadjakic.ocr.studio.exception.OcrException
import com.github.nenadjakic.ocr.studio.executor.OcrExecutor
import com.github.nenadjakic.ocr.studio.executor.ParallelizationManager
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class OcrService(
    private val ocrProperties: OcrProperties,
    private val parallelizationManager: ParallelizationManager,
    private val tesseractFactory: TesseractFactory,
    private val taskRepository: TaskRepository
) {
    private val logger = LoggerFactory.getLogger(OcrService::class.java)

    fun schedule(id: UUID) {
        val task = taskRepository.findById(id).orElseThrow { OcrException("Cannot find task with id: $id") }

        if (Status.getInProgressStatuses().contains(task.ocrProgress.status)) {
            throw OcrException("Task with id: {} is in progress and cannot be scheduled.")
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
    }

    fun interrupt(id: UUID) = parallelizationManager.interrupt(id)

    fun interruptAll(id: UUID) = parallelizationManager.interruptAll()
}