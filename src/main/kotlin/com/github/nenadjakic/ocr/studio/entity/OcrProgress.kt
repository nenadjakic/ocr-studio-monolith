package com.github.nenadjakic.ocr.studio.entity

class OcrProgress() {
    constructor(status: Status, progress: String, description: String?) : this() {
        this.status = status
        this.progress = progress
        this.description = description
    }

    var status: Status = Status.CREATED
    var progress: String = "N/A"
    var description: String? = null
}