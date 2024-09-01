package com.github.nenadjakic.ocr.studio.controller

import com.github.nenadjakic.ocr.studio.service.OcrService
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

    @PostMapping
    fun schedule(id: UUID) {
        ocrService.schedule(id)
    }

    @PutMapping
    fun interrupt(id: UUID) {

    }

    @GetMapping
    fun status(id: UUID) {

    }
}