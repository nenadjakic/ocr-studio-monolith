package com.github.nenadjakic.ocr.studio.executor

data class ProgressInfo(
    var taskDone: Int,
    var totalTasks: Int,
    var description: String
) {
    constructor() : this(0, 0, "In progress...")
}
