package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import com.github.nenadjakic.ocr.studio.entity.Task
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.*

@ExtendWith(
    MockitoExtension::class)
class TaskServiceTest {

    private lateinit var taskService: TaskService
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskFileSystemService: TaskFileSystemService
    private lateinit var ocrProperties: OcrProperties

    @BeforeEach
    fun setUp() {
        taskRepository = mock(TaskRepository::class.java)
        taskFileSystemService = mock(TaskFileSystemService::class.java)
        ocrProperties = mock(OcrProperties::class.java)

        taskService = TaskService(taskRepository, taskFileSystemService, ocrProperties)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @DisplayName("findAll should return list of tasks")
    fun findAll() {
        `when`(taskRepository.findAll(Sort.by(Sort.Order.asc("id")))).thenReturn(listOf(Task(), Task()))
        val result = taskService.findAll()

        assertEquals(2, result.size)
        verify(taskRepository).findAll(Sort.by(Sort.Order.asc("id")))
    }

    @Test
    @DisplayName("findById should return task")
    fun findById_should_return_task() {
        val id = UUID.randomUUID()
        val task = Task()
        task.id = id

        `when`(taskRepository.findById(id)).thenReturn(Optional.of(task))
        val result = taskService.findById(id)

        assertNotNull(result)
        assertEquals(task, result)
    }

    @Test
    @DisplayName("findById should return null")
    fun findById_should_return_null() {
        val id = UUID.randomUUID()

        `when`(taskRepository.findById(id)).thenReturn(Optional.empty())
        val result = taskService.findById(id)

        assertNull(result)
    }


    @Test
    @DisplayName("findPage should return paged result")
    fun findPage() {
        val pageNumber = 0
        val pageSize = 10
        val tasks = listOf(Task())
        val page: Page<Task> = PageImpl(tasks)
        `when`(taskRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("id"))))).thenReturn(page)

        val result = taskService.findPage(pageNumber, pageSize)

        assertEquals(1, result.content.size)
        verify(taskRepository).findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("id"))))
    }

    @Test
    @DisplayName("insert should create a new task")
    fun insert_in_document_is_null() {
        val task = Task()
        val savedTask = Task()
        savedTask.id = UUID.randomUUID()
        `when`(taskRepository.insert(any(Task::class.java))).thenReturn(savedTask)

        val result = taskService.insert(task)

        assertNotNull(result.id)
        verify(taskRepository).insert(task)
    }

    @Test
    @DisplayName("update should save the task")
    fun update() {
        val task = Task()
        task.id = UUID.randomUUID()
        `when`(taskRepository.save(task)).thenReturn(task)

        val result = taskService.update(task)

        assertEquals(task, result)
        verify(taskRepository).save(task)
    }

    @Test
    @DisplayName("delete should delete the task")
    fun delete() {
        val task = Task()

        taskService.delete(task)

        verify(taskRepository).delete(task)
    }

    @Disabled
    @Test
    @DisplayName("deleteById should delete the task by id")
    fun deleteById() {
        val taskId = UUID.randomUUID()

        `when`(taskRepository.findById(taskId)).thenReturn(Optional.of(Task()))

        taskService.deleteById(taskId)

        verify(taskRepository).deleteById(taskId)
    }

    @Test
    @DisplayName("upload should save documents and update the task")
    fun upload() {
        val taskId = UUID.randomUUID()
        val task = Task()
        task.id = taskId
        `when`(taskRepository.findById(taskId)).thenReturn(Optional.of(task))

        val multipartFile = mock(MultipartFile::class.java)
        `when`(multipartFile.originalFilename).thenReturn("test.pdf")

        val documents = taskService.upload(taskId, listOf(multipartFile))

        assertEquals(1, documents.size)
        verify(taskRepository).save(task)
        verify(taskFileSystemService).uploadFile(multipartFile, taskId, documents[0].randomizedFileName)

    }
}