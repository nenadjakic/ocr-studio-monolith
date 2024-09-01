package com.github.nenadjakic.ocr.studio.executor

import java.util.UUID

interface ParallelizationManager {

    fun schedule(executor: Executor)

    fun interrupt(id: UUID): Boolean?

    fun interruptAll()

    fun getProgress(id: UUID): ProgressInfo?
}