package com.github.nenadjakic.ocr.studio.dto

import jakarta.validation.constraints.NotEmpty

class TaskDraftRequest {
    @NotEmpty
    lateinit var name: String
}
