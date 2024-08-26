package com.github.nenadjakic.ocr.studio.repository

import com.github.nenadjakic.ocr.studio.entity.OcrConfig
import com.github.nenadjakic.ocr.studio.entity.Task
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import java.util.UUID

interface TaskRepository : MongoRepository<Task, UUID> {

    @Query(value = "{ 'id': ?0 }")
    @Update("{ 'ocrConfig': ?1 }")
    fun updateOcrConfigById(id: UUID, ocrConfig: OcrConfig): Int
}