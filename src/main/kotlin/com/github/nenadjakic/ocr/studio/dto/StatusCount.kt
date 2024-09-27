package com.github.nenadjakic.ocr.studio.dto

import com.github.nenadjakic.ocr.studio.entity.Status

data class StatusCount(
    val status: Status,
    val count: Long
)
