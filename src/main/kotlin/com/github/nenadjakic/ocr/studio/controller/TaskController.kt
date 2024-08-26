package com.github.nenadjakic.ocr.studio.controller

import com.github.nenadjakic.ocr.studio.dto.NewTask
import com.github.nenadjakic.ocr.studio.dto.OcrConfigDto
import com.github.nenadjakic.ocr.studio.dto.SchedulerConfigDto
import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.SchedulerConfig
import com.github.nenadjakic.ocr.studio.entity.Task
import com.github.nenadjakic.ocr.studio.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@Tag(name = "Task controller", description = "API endpoints for managing task entities.")
@RestController
@RequestMapping("/task")
@Validated
open class TaskController(
    private val modelMapper: ModelMapper,
    private val taskService: TaskService
) : ReadController<Task, UUID> {

    @Operation(
        operationId = "findPageWithTasks",
        summary = "Get tasks by page.",
        description = "Returns a page of tasks based on page number and page size.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved page of tasks.")
        ]
    )
    override fun findPage(pageNumber: Int, pageSize: Int?): ResponseEntity<Page<Task>> = ResponseEntity.ok(taskService.findPage(pageNumber, pageSize ?: 20))

    @Operation(
        operationId = "findTaskById",
        summary = "Get task by id.",
        description = "Returns an task with the specified id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved task."),
            ApiResponse(responseCode = "404", description = "Entity not found.")
        ]
    )
    override fun findById(id: UUID): ResponseEntity<Task> = ResponseEntity.ofNullable(taskService.findById(id))

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody model: NewTask): ResponseEntity<Void> {
        var task = modelMapper.map(model, Task::class.java)
        task = taskService.create(task)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(task.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/config/{id}")
    fun update(@PathVariable id: UUID, @RequestBody ocrConfigDto: OcrConfigDto) {
        val ocrConfig = modelMapper.map(ocrConfigDto, OcrConfig::class.java)

        taskService.update(id, ocrConfig)
    }
    @PutMapping("/scheduler/{id}")
    fun update(@PathVariable id: UUID, @RequestBody schedulerConfigDto: SchedulerConfigDto) {
        val schedulerConfig = modelMapper.map(schedulerConfigDto, SchedulerConfig::class.java)
    }

    @PutMapping("upload/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadFile(
        @PathVariable id: UUID,
        @RequestPart("files") multipartFiles: Collection<MultipartFile>
    ) {
        taskService.upload(id, multipartFiles)
    }



    fun updateLanguage(id: UUID, language: String) {
    }
}