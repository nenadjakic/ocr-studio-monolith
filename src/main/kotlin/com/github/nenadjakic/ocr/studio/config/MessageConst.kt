package com.github.nenadjakic.ocr.studio.config

import com.github.nenadjakic.ocr.studio.entity.Status

enum class MessageConst(val description: String) {
    ILLEGAL_STATUS("Cannot remove file for task, because status is different than ${Status.CREATED}."),
    MISSING_DOCUMENT("Cannot find task with specified id.");
}