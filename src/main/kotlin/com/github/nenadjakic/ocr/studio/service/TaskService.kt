package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.Document
import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.Task
import com.github.nenadjakic.ocr.studio.exception.OcrException
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskFileSystemService: TaskFileSystemService,
    private val ocrProperties: OcrProperties
) : CrudService<Task, UUID> {

    override fun findById(id: UUID): Task = taskRepository.findById(id).orElse(null)

    override fun findPage(pageNumber: Int, pageSize: Int): Page<Task> = taskRepository.findAll(PageRequest.of(pageNumber, pageSize))

    override fun insert(entity: Task): Task {
        TODO("Not yet implemented")
    }

    override fun update(entity: Task): Task {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Task) = taskRepository.delete(entity)

    override fun deleteById(id: UUID) = taskRepository.deleteById(id)

    fun create(task: Task): Task {
        task.id = UUID.randomUUID()
        createTaskDirectories(task.id!!)

        return taskRepository.save(task)
    }

    fun upload(id: UUID, multipartFiles: Collection<MultipartFile>) {
        val task = taskRepository.findById(id).orElseThrow { OcrException("Cannot find task with id: $id.") }

        for (multiPartFile in multipartFiles) {
            val document = Document()
            document.originalFileName = multiPartFile.originalFilename!!
            document.randomizedFileName = UUID.randomUUID().toString()
            document.type = multiPartFile.contentType

            taskFileSystemService.uploadFile(multiPartFile, id, document.randomizedFileName)
            task.addInDocument(document)
        }

        taskRepository.save(task)
    }

    fun update(id: UUID, properties: Map<Object, Object>) {
        val optTask = taskRepository.findById(id)

        if (optTask.isPresent) {
            val task = optTask.get()

        }
    }

    fun update(id: UUID, language: String) {

    }

    private fun createTaskDirectories(id: UUID) {
        val path = Path.of(ocrProperties.taskPath, id.toString())
        Files.createDirectories(path)
        Files.createDirectory(path.resolve("input"))
        Files.createDirectory(path.resolve("output"))
    }

    fun update(id: UUID, ocrConfig: OcrConfig) {
        taskRepository.updateOcrConfigById(id, ocrConfig)
    }
}