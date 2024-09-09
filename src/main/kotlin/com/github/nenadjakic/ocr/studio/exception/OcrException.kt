package com.github.nenadjakic.ocr.studio.exception

open class OcrException(message: String) : Exception(message)

class IllegalStateOcrException(message: String): OcrException(message)

class MissingDocumentOcrException(message: String) : OcrException(message)
