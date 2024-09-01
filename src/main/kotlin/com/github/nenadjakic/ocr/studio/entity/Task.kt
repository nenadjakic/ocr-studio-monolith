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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (name != other.name) return false
        if (ocrConfig != other.ocrConfig) return false
        if (schedulerConfig != other.schedulerConfig) return false
        if (ocrProgress != other.ocrProgress) return false
        if (inDocuments != other.inDocuments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + ocrConfig.hashCode()
        result = 31 * result + schedulerConfig.hashCode()
        result = 31 * result + ocrProgress.hashCode()
        result = 31 * result + inDocuments.hashCode()
        return result
    }
}