package com.github.nenadjakic.ocr.studio.service

interface CrudService<T, ID> : WriteService<T, ID>, ReadService<T, ID>