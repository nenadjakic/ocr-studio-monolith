package com.github.nenadjakic.ocr.studio.controller

import com.github.nenadjakic.ocr.studio.entity.OcrProgress
import com.github.nenadjakic.ocr.studio.service.OcrService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/ocr")
class OcrController(
    private val ocrService: OcrService
)  {

    @Operation(
        operationId = "scheduleOcrJob",
        summary = "Schedule ocr job.",
        description = """Schedule ocr job. If a task has a defined start time, the OCR job will be initiated 
            |at the earliest possible moment after that time and when an executor becomes available. 
            |If the start time is not defined, the job will be initiated as soon as possible, depending on the availability of an executor."""
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task scheduled successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PostMapping
    fun schedule(id: UUID): ResponseEntity<Void> {
        ocrService.schedule(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        operationId = "interuptOcrJob",
        summary = "Interrupt ocr job for given id.",
        description = "Interrupt ocr job for given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task interrupted successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PutMapping
    fun interrupt(id: UUID): ResponseEntity<Void> {
        ocrService.interrupt(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        operationId = "getOcrJobProgress",
        summary = "Get ocr progress for given id.",
        description = "Get ocr progress for given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ocr progress successfully retrieved."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @GetMapping
    fun progress(id: UUID): ResponseEntity<OcrProgress> = ResponseEntity.ok(ocrService.getProgress(id))
}