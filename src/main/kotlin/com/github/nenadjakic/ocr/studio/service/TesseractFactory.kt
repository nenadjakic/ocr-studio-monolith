package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.config.OcrProperties
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import org.springframework.stereotype.Component

@Component
class TesseractFactory(
    private val ocrProperties: OcrProperties
) {

    fun create(
        language: String,
        ocrEngineMode: Int,
        pageSegMode: Int,
        params: Map<String, String>?
    ): ITesseract {
        val tesseract: ITesseract = Tesseract()
        tesseract.setDatapath(ocrProperties.tesseract.dataPath)
        tesseract.setLanguage(language)
        tesseract.setOcrEngineMode(ocrEngineMode)
        tesseract.setPageSegMode(pageSegMode)

        return tesseract
    }
}