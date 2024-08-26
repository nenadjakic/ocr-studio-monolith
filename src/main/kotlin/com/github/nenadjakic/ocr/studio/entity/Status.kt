package com.github.nenadjakic.ocr.studio.entity

enum class Status(
    val ocrInProgress: Boolean,
    val description: String
) {
    CREATED(false, "Task is created. Exists in system and can be modified."),
    TRIGGERED(true,
        "Task is added to task executor. If start time is not given," +
                " task will start immediately (when first executor thread is free)."),
    STARTED(true, "OCR in progress."),
    FAILED(false, "OCR finished with error."),
    INTERRUPTED(false, "OCR interrupted by user."),
    CANCELED(false, "OCR task canceled. Task was in CREATED status and user decide to cancel it."),
    FINISHED(false, "OCR task finished successfully.");

    companion object {
        fun getInProgressStatuses(): Collection<Status> = entries.filter { it.ocrInProgress }
    }
}