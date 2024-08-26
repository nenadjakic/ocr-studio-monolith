package com.github.nenadjakic.ocr.studio.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ocr")
class OcrProperties {

    lateinit var taskPath: String
}