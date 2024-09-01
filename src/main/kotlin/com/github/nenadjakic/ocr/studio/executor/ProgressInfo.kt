package com.github.nenadjakic.ocr.studio.executor

import com.github.nenadjakic.ocr.studio.entity.OcrProgress

data class ProgressInfo(
    var taskDone: Int,
    var totalTasks: Int,
    var description: String,
    var progressInfoStatus: ProgressInfoStatus
) {
    enum class ProgressInfoStatus {
        CREATED,
        IN_PROGRESS,
        FINISHED,
        FAILED,
        CANCELED,
        INTERRUPTED
    }

    constructor() : this(0, 0, "Created...", ProgressInfoStatus.CREATED)
}
