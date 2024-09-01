package com.github.nenadjakic.ocr.studio.extension

import org.springframework.web.multipart.MultipartFile
import java.io.File


fun MultipartFile.toFile(targetFile: File) {
    this.transferTo(targetFile)
}