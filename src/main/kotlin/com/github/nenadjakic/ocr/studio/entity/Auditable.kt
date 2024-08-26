package com.github.nenadjakic.ocr.studio.entity

import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

open class Auditable<U> {

    @CreatedBy
    @Field(name = "_created_by")
    var  createdBy: U? = null

    @CreatedDate
    @Field(name = "_created_date")
    lateinit var  createdDate: LocalDateTime

    @LastModifiedBy
    @Field(name = "_last_modified_by")
    var  lastModifiedBy: U?  = null

    @LastModifiedDate
    @Field(name = "_last_modified_date")
    lateinit var  lastModifiedDate: LocalDateTime

    @Version
    @Field(name = "_version")
    var version: Int? = null
}