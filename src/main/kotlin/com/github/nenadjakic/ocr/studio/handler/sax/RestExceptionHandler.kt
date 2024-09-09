package com.github.nenadjakic.ocr.studio.handler.sax

import com.github.nenadjakic.ocr.studio.exception.IllegalStateOcrException
import com.github.nenadjakic.ocr.studio.exception.MissingDocumentOcrException
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.stream.Collectors

@ControllerAdvice
@ResponseBody
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    data class ErrorInfo(
        var status: HttpStatus,
        var errors: List<String?>,
        var path: String,
        val timestamp: LocalDateTime = LocalDateTime.now()
    )

    @ExceptionHandler(MissingDocumentOcrException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(ex: MissingDocumentOcrException, request: WebRequest?): ResponseEntity<ErrorInfo> {
        logger.error("Error occurred.", ex)
        return getErrorInfoResponseEntity(HttpStatus.NOT_FOUND, ex, request as ServletWebRequest)
    }

    @ExceptionHandler(IllegalStateOcrException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(ex: IllegalStateOcrException, request: WebRequest?): ResponseEntity<ErrorInfo> {
        logger.error("Error occurred.", ex)
        return getErrorInfoResponseEntity(HttpStatus.BAD_REQUEST, ex, request as ServletWebRequest)
    }

    private fun getErrorInfoResponseEntity(
        resultHttpStatus: HttpStatus,
        ex: Exception,
        request: ServletWebRequest
    ): ResponseEntity<ErrorInfo> {
        val path = request.request.requestURI

        logger.error("Exception occurred. in request: $path", ex)
        val body: ErrorInfo = if (ex is ConstraintViolationException && ex.constraintViolations.isNotEmpty()) {
            getErrorResponse(resultHttpStatus,
                ex.constraintViolations.stream()
                    .map { obj: ConstraintViolation<*> -> obj.message }
                    .collect(Collectors.toList()), path)
        } else {
            getErrorResponse(resultHttpStatus, ex.message, path)
        }

        return ResponseEntity(body, resultHttpStatus)
    }

    private fun getErrorResponse(resultHttpStatus: HttpStatus, message: String?, path: String): ErrorInfo = getErrorResponse(resultHttpStatus, mutableListOf(message), path)

    private fun getErrorResponse(resultHttpStatus: HttpStatus, messages: List<String?>, path: String): ErrorInfo = ErrorInfo(resultHttpStatus, messages, path)
}