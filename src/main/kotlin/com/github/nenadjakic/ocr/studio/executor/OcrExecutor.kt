package com.github.nenadjakic.ocr.studio.executor

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.OutDocument
import com.github.nenadjakic.ocr.studio.entity.Task
import com.github.nenadjakic.ocr.studio.extension.toOcrProgress
import com.github.nenadjakic.ocr.studio.handler.sax.HocrSaxHandler
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import com.github.nenadjakic.ocr.studio.service.TaskFileSystemService
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.util.ImageHelper
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Path
import java.time.ZonedDateTime
import java.util.*
import javax.imageio.ImageIO
import javax.xml.parsers.SAXParserFactory

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
        progressInfo.description = "Starting ocr process..."

        task.ocrProgress = progressInfo.toOcrProgress()
        taskRepository.save(task)
        try {
            progressInfo.description = "Starting ocr of documents..."
            for (document in task.inDocuments.sortedBy { it.originalFileName }) {
                val inFile =
                    Path.of(ocrProperties.taskPath, task.id.toString(), "input", document.randomizedFileName).toFile()
                if (inFile.exists()) {
                    val outFile =
                        Path.of(ocrProperties.taskPath, task.id.toString(), "output", UUID.randomUUID().toString())
                            .toFile()

                    document.outDocument = OutDocument()
                    document.outDocument!!.outputFileName = outFile.name

                    val filesToOcr = preProcessDocument(task.ocrConfig.preProcessing, inFile)
                    if (filesToOcr.size > 1) {
                        progressInfo.description = "Starting ocr of documents..."
                        logger.info("Starting ocr of multi paged document.")
                        PDDocument().use { pdDocument ->
                            filesToOcr.entries.sortedBy { it.key }.forEach { filesToOcrEntry ->
                                val index = filesToOcrEntry.key
                                val fileToOcr = filesToOcrEntry.value

                                val tempOutFile = File.createTempFile("___", ".pdf")
                                logger.debug("OCR of pdf page: $index.")
                                tesseract.createDocuments(
                                    fileToOcr.absolutePath,
                                    tempOutFile.absolutePath.removeSuffix(".pdf"),
                                    mutableListOf(task.ocrConfig.fileFormat.toRenderedFormat())
                                )

                                val outDocument = Loader.loadPDF(tempOutFile)
                                outDocument.pages.forEach { page ->
                                    pdDocument.addPage(page)
                                }

                            }
                            pdDocument.save(outFile)
                        }
                    } else {
                        logger.info("Starting ocr of one paged document.")
                        filesToOcr.entries.sortedBy { it.key }.forEach {
                            val index = it.key
                            val fileToOcr = it.value

                            tesseract.createDocuments(
                                fileToOcr.absolutePath,
                                outFile.absolutePath,
                                mutableListOf(task.ocrConfig.fileFormat.toRenderedFormat())
                            )
                        }
                    }
                    progressInfo.taskDone++
                }
            }
            if (task.ocrConfig.mergeDocuments) {
                progressInfo.description = "Starting merging of documents..."
                val mergedFile =
                    Path.of(ocrProperties.taskPath, task.id.toString(), "output", "merged_" + UUID.randomUUID() + "." + task.ocrConfig.fileFormat.getExtension())
                        .toFile()

                when (task.ocrConfig.fileFormat) {
                    OcrConfig.FileFormat.TEXT -> {
                        mergeTextDocuments(mergedFile, task)
                    }
                    OcrConfig.FileFormat.PDF -> {
                        mergePdfDocuments(mergedFile, task)
                    }
                    OcrConfig.FileFormat.HOCR -> {
                        mergeHocrDocuments(mergedFile, task)
                    }
                }
            }
            progressInfo.progressInfoStatus = ProgressInfo.ProgressInfoStatus.FINISHED
            task.ocrProgress = progressInfo.toOcrProgress()
            taskRepository.save(task)
        } catch (ex: Exception) {
            logger.error("OCR for task id $id failed.", ex)
            progressInfo.progressInfoStatus = ProgressInfo.ProgressInfoStatus.FAILED
        }
    }

    private fun mergeTextDocuments(mergedFile: File, task: Task) {
        BufferedWriter(FileWriter(mergedFile)).use { writer ->
            for (document in task.inDocuments.sortedBy { it.originalFileName }) {
                val file = Path.of(ocrProperties.taskPath, task.id.toString(), "output", document.outDocument!!.outputFileName + "." + task.ocrConfig.fileFormat.getExtension()).toFile()
                BufferedReader(FileReader(file)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        writer.write(line)
                        writer.newLine()
                    }
                }
                writer.newLine()
            }
        }
    }

    private fun mergeHocrDocuments(mergedFile: File, task: Task) {
        val saxParserFactory = SAXParserFactory.newInstance()
        val saxParser = saxParserFactory.newSAXParser()
        val saxHandler = HocrSaxHandler()

        BufferedWriter(FileWriter(mergedFile)).use { writer ->
            //writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            //writer.newLine()
           // writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")
            writer.write("<html>")
            writer.newLine()
            for ((index, document) in task.inDocuments.sortedBy { it.originalFileName }.withIndex()) {
                val file = Path.of(ocrProperties.taskPath, task.id.toString(), "output", document.outDocument!!.outputFileName + "." + task.ocrConfig.fileFormat.getExtension()).toFile()

                saxParser.parse(file, saxHandler)
                if (index == 0) {
                    writer.write(" <head>")
                    writer.newLine()
                    writer.write(saxHandler.head)
                    writer.write(" </head>")
                    writer.newLine()
                    writer.write("  <body>")
                }
                writer.write(saxHandler.body)
            }
            writer.newLine()
            writer.write("  </body>")
            writer.newLine()
            writer.write("</html>")
        }
    }

    private fun mergePdfDocuments(mergedFile: File, task: Task) {
        PDDocument().use { pdDocument ->
            for (document in task.inDocuments.sortedBy { it.originalFileName }) {
                val file = Path.of(ocrProperties.taskPath, task.id.toString(), "output", document.outDocument!!.outputFileName + "." + task.ocrConfig.fileFormat.getExtension()).toFile()
                val outDocument = Loader.loadPDF(file)
                outDocument.pages.forEach { page ->
                    pdDocument.addPage(page)
                }
            }
            pdDocument.save(mergedFile)
        }
    }

    private data class InputData (
        val fileFormat: OcrConfig.FileFormat,
        val file: File
    )

    @Throws(IOException::class)
    private fun preProcessDocument(preProcess: Boolean, inFile: File): Map<Long, File> {
        val files = mutableMapOf<Long, File>()
        var order = 1L
        val mediaType = TaskFileSystemService.getContentType(inFile)
        if (preProcess) {
            logger.info("Pre processing of input document ${inFile.name}.")
            if (mediaType.type.equals("image")) {
                val originalImage = ImageIO.read(inFile)
                val grayscaleImage = ImageHelper.convertImageToGrayscale(originalImage)
                val tempGrayscaleImage = File.createTempFile("___", "_tmp")
                val result = ImageIO.write(grayscaleImage, mediaType.subtype, tempGrayscaleImage)
                if (result) {
                    files[order++] = tempGrayscaleImage
                }
            } else if (mediaType.toString() == "application/pdf") {
               Loader.loadPDF(inFile).use  {
                    val pdfRenderer = PDFRenderer(it)
                    logger.info("Starting of pdf preprocess. Total pages: ${it.numberOfPages}")
                    for (pageNumber in 0..<it.numberOfPages) {
                        logger.debug("Preprocessing of pdf page $pageNumber")
                        val pdfPage = pdfRenderer.renderImageWithDPI(pageNumber, 300F, ImageType.GRAY)
                        val tempPdfPage = File.createTempFile("___", "_tmp")
                        val result = ImageIO.write(pdfPage, "png", tempPdfPage)
                        if (result) {
                            files[order++] = tempPdfPage
                        } else {
                            order++
                        }
                    }
                }
            } else {
                files[order++] = inFile
            }

        } else {
            files[order++] = inFile
        }
        return files
    }
}