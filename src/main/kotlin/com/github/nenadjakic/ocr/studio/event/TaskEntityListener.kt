package com.github.nenadjakic.ocr.studio.event

import com.github.nenadjakic.ocr.studio.entity.Task
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class TaskEntityListener : AbstractMongoEventListener<Task>() {
    override fun onBeforeConvert(event: BeforeConvertEvent<Task>) {
        if (event.source.id == null) {
            event.source.id = UUID.randomUUID()
        }
    }
}