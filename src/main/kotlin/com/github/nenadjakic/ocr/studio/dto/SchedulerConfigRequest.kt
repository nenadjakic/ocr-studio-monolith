package com.github.nenadjakic.ocr.studio.dto

import java.time.LocalDateTime

data class SchedulerConfigRequest(var startDateTime: LocalDateTime? = null)