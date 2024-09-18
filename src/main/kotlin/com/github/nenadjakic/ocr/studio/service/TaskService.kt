package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.MessageConst
import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.*
import com.github.nenadjakic.ocr.studio.exception.IllegalStateOcrException
import com.github.nenadjakic.ocr.studio.exception.MissingDocumentOcrException
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

    private fun insert(task: Task): Task {
        task.id = UUID.randomUUID()

        taskFileSystemService.createTaskDirectories(task.id!!)

        return taskRepository.insert(task)
    }

    fun insert(task: Task, files: Collection<MultipartFile>? = emptyList()): Task {
        val createdEntity = insert(task)
        if (!files.isNullOrEmpty()) {
            upload(createdEntity.id!!, files)
        }
        return createdEntity
    }

    fun update(entity: Task): Task = taskRepository.save(entity)

    fun delete(task: Task) {
        if (task.ocrProgress.status != Status.CREATED) {
            throw IllegalStateOcrException(MessageConst.ILLEGAL_STATUS.description)
        }

        removeAllFiles(task)
        taskRepository.delete(task)
    }

    fun deleteById(id: UUID) {
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }
        delete(task)
    }

    fun upload(id: UUID, multipartFiles: Collection<MultipartFile>): List<Document> {
        val createdDocuments = mutableListOf<Document>()
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }

        for (multiPartFile in multipartFiles) {
            val document = Document(multiPartFile.originalFilename!!, UUID.randomUUID().toString()).apply {
                type = TaskFileSystemService.getContentType(multiPartFile)
            }

            taskFileSystemService.uploadFile(multiPartFile, id, document.randomizedFileName)
            task.addInDocument(document)
            createdDocuments.add(document)
        }

        taskRepository.save(task)
        return createdDocuments
    }

    fun removeFile(id: UUID, originalFileName: String) {
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }

        if (task.ocrProgress.status != Status.CREATED) {
            throw IllegalStateOcrException(MessageConst.ILLEGAL_STATUS.description)
        }

        task.inDocuments.find { it.originalFileName == originalFileName }?.let {
            taskFileSystemService.deleteFile(TaskFileSystemService.getInputFile(ocrProperties.taskPath, id, it.randomizedFileName).toPath())
            task.inDocuments.remove(it)
        }
        taskRepository.save(task)
    }

    fun removeAllFiles(task: Task) {
        if (task.ocrProgress.status != Status.CREATED) {
            throw IllegalStateOcrException(MessageConst.ILLEGAL_STATUS.description)
        }
        task.inDocuments.forEach { taskFileSystemService.deleteFile(TaskFileSystemService.getInputFile(ocrProperties.taskPath, task.id!!, it.randomizedFileName).toPath()) }
        task.inDocuments.clear()
        taskRepository.save(task)
    }

    fun removeAllFiles(id: UUID) {
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentOcrException(MessageConst.MISSING_DOCUMENT.description) }

        removeAllFiles(task)
    }

    fun update(id: UUID, properties: Map<Object, Object>) {
        TODO()
    }

    fun update(id: UUID, language: String): Int = taskRepository.updateLanguageById(id, language)

    fun update(id: UUID, ocrConfig: OcrConfig): Int = taskRepository.updateOcrConfigById(id, ocrConfig)

    fun update(id: UUID, schedulerConfig: SchedulerConfig): Int = taskRepository.updateSchedulerConfigById(id, schedulerConfig)
}