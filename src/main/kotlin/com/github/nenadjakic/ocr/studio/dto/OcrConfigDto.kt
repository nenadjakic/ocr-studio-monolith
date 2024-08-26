package com.github.nenadjakic.ocr.studio.dto

import com.github.nenadjakic.ocr.studio.entity.OcrConfig

class OcrConfigDto {
    lateinit var ocrEngineMode: OcrConfig.OcrEngineMode
    lateinit var pageSegmentationMode: OcrConfig.PageSegmentationMode
    lateinit var language: String
}