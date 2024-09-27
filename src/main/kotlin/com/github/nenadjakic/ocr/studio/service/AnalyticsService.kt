package com.github.nenadjakic.ocr.studio.service

import com.github.nenadjakic.ocr.studio.dto.StatusCount
import com.github.nenadjakic.ocr.studio.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class AnalyticsService(
    private val taskRepository: TaskRepository
) {
    fun getCountByStatus(): List<StatusCount> = taskRepository.countTasksByStatus()

    fun getAverageInDocuments(): Long = taskRepository.averageInDocuments()
}