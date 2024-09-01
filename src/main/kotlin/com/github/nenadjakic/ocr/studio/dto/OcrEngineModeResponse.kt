package com.github.nenadjakic.ocr.studio.dto

data class OcrEngineModeResponse(
    override val value: String,
    override val description: String
) : ValueDescriptionResponse
