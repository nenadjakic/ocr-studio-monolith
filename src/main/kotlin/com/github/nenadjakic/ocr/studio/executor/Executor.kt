package com.github.nenadjakic.ocr.studio.executor

import java.time.ZonedDateTime
import java.util.UUID

interface Executor : Runnable {
    val id: UUID
    val startDateTime: ZonedDateTime?
    val progressInfo: ProgressInfo
}