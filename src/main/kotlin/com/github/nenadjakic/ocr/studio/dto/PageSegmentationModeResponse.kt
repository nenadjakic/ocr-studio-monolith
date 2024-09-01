package com.github.nenadjakic.ocr.studio.dto

data class PageSegmentationModeResponse(
    override val value: String,
    override val description: String
) : ValueDescriptionResponse