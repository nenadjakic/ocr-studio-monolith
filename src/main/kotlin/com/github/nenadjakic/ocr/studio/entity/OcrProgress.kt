package com.github.nenadjakic.ocr.studio.entity

class OcrProgress(
    var status: Status = Status.CREATED,
    var progress: String =  "N/A",
    var description: String? = null
)