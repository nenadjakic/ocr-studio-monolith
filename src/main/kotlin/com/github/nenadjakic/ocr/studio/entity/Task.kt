package com.github.nenadjakic.ocr.studio.entity

import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "ocr_collection")
class Task : Auditable<UUID>() {

    @Id
    @Field(name = "_id")
    var id: UUID? = null

    lateinit var name: String
    var ocrConfig: OcrConfig = OcrConfig()
    var schedulerConfig: SchedulerConfig = SchedulerConfig()
    var ocrProgress: OcrProgress = OcrProgress()
    var inDocuments: MutableCollection<com.github.nenadjakic.ocr.studio.entity.Document> = mutableListOf()
        set(value) {
            inDocuments.clear()
            inDocuments.addAll(value)
        }

    fun addInDocument(document: com.github.nenadjakic.ocr.studio.entity.Document): Boolean = inDocuments.add(document)
}