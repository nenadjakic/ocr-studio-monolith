package com.github.nenadjakic.ocr.studio.controller

import com.github.nenadjakic.ocr.studio.dto.StatusCount
import com.github.nenadjakic.ocr.studio.service.AnalyticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Analytics controller", description = "API endpoints for analytics.")
@RestController
@RequestMapping("/analytics")
class AnalyticsController(private val analyticsService: AnalyticsService) {

    @Operation(
        operationId = "getCountByStatus",
        summary = "Get count by status.",
        description = "Returns count by status information.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved count by status.")
        ]
    )
    @GetMapping(value = ["/count-by-status"], produces = [org.springframework.http.MediaType.APPLICATION_JSON_VALUE])
    fun getCountByStatus(): ResponseEntity<List<StatusCount>> = ResponseEntity.ok(analyticsService.getCountByStatus())

    @Operation(
        operationId = "getAverageInDocuments",
        summary = "Get average count across inDocuments.",
        description = "Returns average count across inDocuments.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved average count across inDocuments.")
        ]
    )
    @GetMapping(value = ["/average-in-documents"], produces = [org.springframework.http.MediaType.APPLICATION_JSON_VALUE])
    fun getAverageInDocuments(): ResponseEntity<Long> = ResponseEntity.ok(analyticsService.getAverageInDocuments())
}