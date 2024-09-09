package com.github.nenadjakic.ocr.studio.entity

class Document(
    val originalFileName: String,
    val randomizedFileName: String
) {
    var type: String? = null
    var outDocument: OutDocument? = null
}