package com.github.nenadjakic.ocr.studio.dto

import java.time.ZonedDateTime

data class SchedulerConfigRequest(var startDateTime: ZonedDateTime? = null)