package com.github.nenadjakic.ocr.studio.controller

import com.github.nenadjakic.ocr.studio.dto.FileFormatResponse
import com.github.nenadjakic.ocr.studio.dto.OcrEngineModeResponse
import com.github.nenadjakic.ocr.studio.dto.PageSegmentationModeResponse
import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Config controller", description = "API endpoints for config.")
@RestController
@RequestMapping("/config")
class OcrConfigController {

    @Operation(
        operationId = "findOcrEngineModes",
        summary = "Get all ocr engine modes.",
        description = "Returns a collection with ocr engine modes.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved page of ocr engine modes.")
        ]
    )
    @GetMapping("/engine-mode")
    fun findOcrEngineModes(): ResponseEntity<Collection<OcrEngineModeResponse>> =
        ResponseEntity.ok(OcrConfig.OcrEngineMode.entries.map { OcrEngineModeResponse(it.name, it.descritpion) })

    @Operation(
        operationId = "findPageSegmentationModes",
        summary = "Get all page segmentation modes.",
        description = "Returns a collection with page segmentation modes.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved page segmentation modes.")
        ]
    )
    @GetMapping("/page-segmentation-mode")
    fun findOcrPageSegmentationModes(): ResponseEntity<Collection<PageSegmentationModeResponse>> =
        ResponseEntity.ok(OcrConfig.PageSegmentationMode.entries.map { PageSegmentationModeResponse(it.name, it.descritpion) })

    @Operation(
        operationId = "findFileFormats",
        summary = "Get all file formats.",
        description = "Returns a collection with file formats.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved of file formats.")
        ]
    )
    @GetMapping("/file-format")
    fun findOcrFileFormats(): ResponseEntity<Collection<FileFormatResponse>> =
        ResponseEntity.ok(OcrConfig.FileFormat.entries.map { FileFormatResponse(it.name, it.getExtension(), it.toRenderedFormat().name) })
}