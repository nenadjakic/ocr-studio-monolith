package com.github.nenadjakic.ocr.studio.entity

class Document {
    lateinit var originalFileName: String
    lateinit var randomizedFileName: String
    var type: String? = null
    var outDocument: OutDocument? = null
}