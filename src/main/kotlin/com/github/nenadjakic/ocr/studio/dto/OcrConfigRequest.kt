package com.github.nenadjakic.ocr.studio.dto

import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.OcrConfig.FileFormat

class OcrConfigRequest {
    lateinit var ocrEngineMode: OcrConfig.OcrEngineMode
    lateinit var pageSegmentationMode: OcrConfig.PageSegmentationMode
    lateinit var language: String
    var tessVariables: Map<String, String>? = null
    var preProcessing: Boolean = false
    lateinit var fileFormat: FileFormat
    var mergeDocuments: Boolean = false
}