package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import org.apache.tika.config.TikaConfig
import org.apache.tika.detect.Detector
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


@Service
class TaskFileSystemService(
    private val ocrProperties: OcrProperties
) {
    private val inputDirectoryName: String = "input"
    private val outputDirectoryName: String = "output"

    companion object {
        private val tikaConfig = TikaConfig()
        private val detector: Detector = tikaConfig.detector

        fun getContentType(file: File): MediaType = detector.detect(cloneInputStream(file.inputStream()), Metadata())

        fun getContentType(multiPartFile: MultipartFile): String? {
            var contentType = multiPartFile.contentType

            if ("application/octet-stream".equals(contentType, true)) {
                contentType = detector.detect(cloneInputStream(multiPartFile.inputStream), Metadata()).toString()
            }
            return contentType
        }

        private fun cloneInputStream (inputStream: InputStream): InputStream {
            val byteArrayOutputStream = ByteArrayOutputStream()
            inputStream.transferTo(byteArrayOutputStream)

            return ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        }
    }

    @Throws(IOException::class)
    fun createTaskDirectories(id: UUID) {
        val path = Path.of(ocrProperties.taskPath, id.toString())
        Files.createDirectories(path)
        Files.createDirectory(path.resolve("input"))
        Files.createDirectory(path.resolve("output"))
    }

    @Throws(IOException::class)
    fun uploadFile(
        multiPartFile: MultipartFile,
        taskId: UUID,
        fileName: String,
        input: Boolean = true) {

        val targetFile = Path.of(
            ocrProperties.taskPath,
            taskId.toString(),
            (if (input) inputDirectoryName else outputDirectoryName),
            fileName
        ).toFile()
        multiPartFile.transferTo(targetFile.absoluteFile)
    }

    fun cleanUp(id: UUID) {
        deleteDirectoryRecursively(Path.of(ocrProperties.taskPath))
    }

    fun getInputFiles(id:UUID) {}

    fun getOutputFiles(id:UUID) {}

    @Throws(IOException::class)
    private fun deleteDirectoryRecursively(path: Path) {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }
}