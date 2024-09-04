package com.github.nenadjakic.ocr.studio.extension

import com.github.nenadjakic.ocr.studio.entity.OcrProgress
import com.github.nenadjakic.ocr.studio.entity.Status
import com.github.nenadjakic.ocr.studio.executor.ProgressInfo

fun ProgressInfo.toOcrProgress(): OcrProgress {
    val status =
        when (this.progressInfoStatus) {
            ProgressInfo.ProgressInfoStatus.CREATED -> Status.TRIGGERED
            ProgressInfo.ProgressInfoStatus.IN_PROGRESS -> Status.STARTED
            ProgressInfo.ProgressInfoStatus.FINISHED -> Status.FINISHED
            ProgressInfo.ProgressInfoStatus.FAILED -> Status.FAILED
            ProgressInfo.ProgressInfoStatus.CANCELED -> Status.CANCELED
            ProgressInfo.ProgressInfoStatus.INTERRUPTED -> Status.INTERRUPTED
        }
    val progress = "${this.taskDone} / ${this.totalTasks}"
    val description = this.description

    return OcrProgress(status, progress, description)
}