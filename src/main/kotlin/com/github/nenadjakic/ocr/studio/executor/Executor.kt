package com.github.nenadjakic.ocr.studio.executor

import java.time.LocalDateTime
import java.util.UUID

interface Executor : Runnable {
    val id: UUID
    val startDateTime: LocalDateTime?
    val progressInfo: ProgressInfo
}