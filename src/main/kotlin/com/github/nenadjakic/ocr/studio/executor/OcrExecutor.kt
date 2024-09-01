package com.github.nenadjakic.ocr.studio.executor

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.OutDocument
import com.github.nenadjakic.ocr.studio.entity.Status
import com.github.nenadjakic.ocr.studio.extension.toOcrProgress
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.ITesseract.RenderedFormat
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.ZonedDateTime
import java.util.*

class OcrExecutor(
    override val id: UUID,
    override val startDateTime: ZonedDateTime?,
    private val ocrProperties: OcrProperties,
    private val tesseract: ITesseract,
    private val taskRepository: TaskRepository,
    override val progressInfo: ProgressInfo = ProgressInfo()
) : Executor {
    private val logger = LoggerFactory.getLogger(OcrExecutor::class.java)

    override fun run() {
        val task = taskRepository.findById(id).orElseThrow()
        logger.info("Started OCR for task: {}. Number of documents: {}", task.id, task.inDocuments.size)
        progressInfo.progressInfoStatus = ProgressInfo.ProgressInfoStatus.IN_PROGRESS
        progressInfo.totalTasks = task.inDocuments.size
        progressInfo.description = "In progress..."

        task.ocrProgress = progressInfo.toOcrProgress()
        taskRepository.save(task)
        for (document in task.inDocuments) {
            val inFile = Path.of(ocrProperties.taskPath, task.id.toString(), "input", document.randomizedFileName).toFile()
            if (inFile.exists()) {
                val outFile =
                    Path.of(ocrProperties.taskPath, task.id.toString(), "output", UUID.randomUUID().toString()).toFile()

                document.outDocument = OutDocument()
                document.outDocument!!.outputFileName = outFile.name

                tesseract.createDocuments(inFile.absolutePath, outFile.absolutePath, mutableListOf(RenderedFormat.PDF))
            } else {
                logger.warn("File {} for task {} does not exist on filesystem.", inFile.name, id)
            }

            progressInfo.taskDone++
        }
        task.ocrProgress = progressInfo.toOcrProgress()
        taskRepository.save(task)
    }
}