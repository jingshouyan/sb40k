package com.github.jingshouyan.sb40k.filter

import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.util.JsonMasking
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class LogFilter(
    private val cfg: BizConfig,
    private val jsonMasking: JsonMasking = JsonMasking(cfg.maskSettings)
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(LogFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val uri = request.requestURI

        // 只拦截 /api/*
        return !uri.startsWith("/api/")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val reqWrapper = ContentCachingRequestWrapper(request, 10 * 1024)
        val respWrapper = ContentCachingResponseWrapper(response)

        val start = System.currentTimeMillis()

        try {
            filterChain.doFilter(reqWrapper, respWrapper)
        } finally {
            val cost = System.currentTimeMillis() - start
            logRequest(reqWrapper)
            logResponse(respWrapper, cost)

            // ⚠️ 必须调用，否则 response 不会返回给客户端
            respWrapper.copyBodyToResponse()
        }
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        val contentType = request.contentType ?: ""

        val body = when {
            isMultipart(contentType) -> extractFileNames(request)
            else -> String(request.contentAsByteArray, StandardCharsets.UTF_8)
        }
        val ticket = SecurityContextHolder.getContext().authentication?.principal?.toString() ?: "anonymous"
        if (log.isInfoEnabled) {
            log.info(
                ">>> REQUEST {} {} Ticket: {} Body: {}",
                request.method,
                request.requestURI,
                ticket,
                jsonMasking.masking2(body)
            )
        }
    }

    private fun logResponse(response: ContentCachingResponseWrapper, cost: Long) {
        val contentType = response.contentType ?: ""

        val body = when {
            isDownload(contentType) -> "[file download]"
            else -> String(response.contentAsByteArray, StandardCharsets.UTF_8)
        }
        if (log.isInfoEnabled) {
            log.info("<<< RESPONSE status={} cost={}ms Body: {}", response.status, cost, jsonMasking.masking2(body))
        }

    }

    // ==================== 工具方法 ====================

    private fun isMultipart(contentType: String): Boolean {
        return contentType.contains("multipart/form-data", true)
    }

    private fun isDownload(contentType: String): Boolean {
        return contentType.contains("application/octet-stream", true) ||
                contentType.contains("application/pdf", true) ||
                contentType.contains("image", true)
    }

    private fun extractFileNames(request: HttpServletRequest): String {
        return try {
            val parts = request.parts
            val fileNames = parts
                .filter { it.submittedFileName != null }
                .map { it.submittedFileName }

            "files=$fileNames"
        } catch (e: Exception) {
            "[multipart parse error]"
        }
    }
}