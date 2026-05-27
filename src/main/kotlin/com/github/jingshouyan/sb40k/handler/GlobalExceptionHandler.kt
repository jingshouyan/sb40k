package com.github.jingshouyan.sb40k.handler

import com.github.jingshouyan.sb40k.base.BizException
import com.github.jingshouyan.sb40k.base.R
import com.github.jingshouyan.sb40k.base.RC
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import tools.jackson.databind.exc.MismatchedInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // ✅ 业务异常
    @ExceptionHandler(BizException::class)
    fun handleBiz(e: BizException): R<Any?> {
        return R.error(e.code, e.data)
    }

    // ✅ 参数校验异常（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValid(e: MethodArgumentNotValidException): R<Any?> {
        val data = e.bindingResult.fieldErrors.map { "${it.field}:${it.defaultMessage}" }
        return R.error(RC.PARAM_INVALID, data)
    }

    // ✅ form 参数校验
    @ExceptionHandler(BindException::class)
    fun handleBind(e: BindException): R<Any?> {
        val data = e.bindingResult.fieldErrors.map { "${it.field}:${it.defaultMessage}" }
        return R.error(RC.PARAM_UNBIND, data)
    }

    // ✅ 参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleType(e: MethodArgumentTypeMismatchException): R<Any?> {
        return R.error(RC.PARAM_ERROR, "${e.name} should be of type ${e.requiredType?.simpleName}")
    }

    // ✅ JSON 解析错误
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun HandleJsonParse(e: HttpMessageNotReadableException): R<Any?> {
        val data = when (val cause = e.cause) {

            is MismatchedInputException -> {
                val field = cause.path.joinToString(".") { it.propertyName ?: "" }
                "field[$field] type mismatch, expected ${cause.targetType?.simpleName ?: "unknown"}"
            }

            else -> "request body parse error: ${cause?.message}"
        }
        return R.error(RC.PARAM_ERROR, data)
    }

    // ✅ 兜底异常（必须有）
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handle(e: Exception): R<Any?> {
        log.warn("Unhandled exception: ${e.message}", e)

        return R.error(RC.SERVER_ERROR, "${e.cause?.message}")
    }
}