package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.Document
import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.SchedulerConfig
import com.github.nenadjakic.ocr.studio.entity.Task
import com.github.nenadjakic.ocr.studio.exception.OcrException
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskFileSystemService: TaskFileSystemService,
    private val ocrProperties: OcrProperties
) {

    fun findAll(): List<Task> = taskRepository.findAll(Sort.by(Sort.Order.asc("id")))

    fun findById(id: UUID): Task? = taskRepository.findById(id).orElse(null)

    fun findPage(pageNumber: Int, pageSize: Int): Page<Task> = taskRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("id"))))

    private fun insert(entity: Task): Task {
        entity.id = UUID.randomUUID()

        taskFileSystemService.createTaskDirectories(entity.id!!)

        return taskRepository.insert(entity)
    }

    fun insert(entity: Task, files: Collection<MultipartFile>? = emptyList()): Task {
        val createdEntity = insert(entity)
        if (files != null && !files!!.isEmpty()) {
            upload(createdEntity.id!!, files)
        }
        return createdEntity;
    }

    fun update(entity: Task): Task = taskRepository.save(entity)

    fun delete(entity: Task) = taskRepository.delete(entity)

    fun deleteById(id: UUID) = taskRepository.deleteById(id)

    fun upload(id: UUID, multipartFiles: Collection<MultipartFile>): List<Document> {
        val createdDocuments = mutableListOf<Document>()
        val task = taskRepository.findById(id).orElseThrow { OcrException("Cannot find task with id: $id.") }

        for (multiPartFile in multipartFiles) {
            val document = Document()
            document.originalFileName = multiPartFile.originalFilename!!
            document.randomizedFileName = UUID.randomUUID().toString()
            document.type = TaskFileSystemService.getContentType(multiPartFile)

            taskFileSystemService.uploadFile(multiPartFile, id, document.randomizedFileName)
            task.addInDocument(document)
            createdDocuments.add(document)
        }

        taskRepository.save(task)
        return createdDocuments
    }

    fun removeFiles(id: UUID, originalFileName: String) {}

    fun update(id: UUID, properties: Map<Object, Object>) {
        val optTask = taskRepository.findById(id)

        if (optTask.isPresent) {
            val task = optTask.get()

        }
    }

    fun update(id: UUID, language: String): Int = taskRepository.updateLanguageById(id, language)

    fun update(id: UUID, ocrConfig: OcrConfig): Int = taskRepository.updateOcrConfigById(id, ocrConfig)

    fun update(id: UUID, schedulerConfig: SchedulerConfig): Int = taskRepository.updateSchedulerConfigById(id, schedulerConfig)
}