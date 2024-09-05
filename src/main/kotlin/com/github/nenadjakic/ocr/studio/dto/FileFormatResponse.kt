package com.github.nenadjakic.ocr.studio.dto

data class FileFormatResponse(
    val value: String,
    val extension: String,
    val tesseractFormat: String
)
