package com.github.nenadjakic.ocr.studio.service

import org.springframework.data.domain.Page

interface ReadService<T, ID> {
    fun findById(id: ID): T
    fun findPage(pageNumber: Int, pageSize: Int): Page<T>
}