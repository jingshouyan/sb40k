package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.VerificationCode
import com.github.jingshouyan.sb40k.mapper.VerificationCodeMapper
import com.github.jingshouyan.sb40k.service.VerificationCodeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class VerificationCodeServiceImpl(
    private val verificationCodeMapper: VerificationCodeMapper,
    private val cfg: BizConfig,
    private val mailSender: JavaMailSender,
) : VerificationCodeService {

    @Value($$"${spring.mail.username:}")
    private var fromEmail: String? = null

    private val log = LoggerFactory.getLogger(VerificationCodeServiceImpl::class.java)

    override fun trigger(
        account: String,
        idType: Int,
        businessType: String,
        lang: String?,
        params: Map<String, String>?
    ): String {
        val now = System.currentTimeMillis()
        val expireMs = cfg.verificationCodeExpireMinutes * 60 * 1000
        val resendIntervalMs = cfg.verificationCodeResendIntervalSeconds * 1000

        val existing = verificationCodeMapper.selectOne(
            QueryWrapper<VerificationCode>()
                .eq("c_account", account)
                .eq("c_id_type", idType)
                .eq("c_business_type", businessType)
                .isNull("c_verified_at")
                .gt("c_expire_at", now)
                .orderByDesc("c_sent_at")
        )

        return if (existing != null) {
            if (now - existing.sentAt < resendIntervalMs) {
                // 1 分钟内，直接返回已有 id
                existing.id!!
            } else {
                // 超过 1 分钟，重新发送，刷新过期时间
                existing.lang = lang
                existing.params = params
                existing.sentAt = now
                existing.expireAt = now + expireMs
                verificationCodeMapper.updateById(existing)
                sendCode(existing)
                existing.id!!
            }
        } else {
            // 无未过期记录，新建
            val code = generateCode()
            val entity = VerificationCode(
                account = account,
                idType = idType,
                code = code,
                businessType = businessType,
                lang = lang,
                params = params,
                sentAt = now,
                expireAt = now + expireMs,
            )
            verificationCodeMapper.insert(entity)
            sendCode(entity)
            entity.id!!
        }
    }

    override fun verify(id: String, code: String): Boolean {
        val now = System.currentTimeMillis()
        val vc = verificationCodeMapper.selectById(id) ?: return false
        if (vc.code != code) return false
        if (vc.expireAt <= now) return false
        if (vc.verifiedAt != null) return false

        vc.verifiedAt = now
        verificationCodeMapper.updateById(vc)
        return true
    }

    private fun generateCode(): String {
        return (100000..999999).random().toString()
    }

    private fun sendCode(vc: VerificationCode) {
        val serial = vc.id?.takeLast(4) ?: "????"
        val suffix = " (编号:$serial)"
        when (vc.idType) {
            C.ID_TYPE_EMAIL -> sendEmail(vc)
            C.ID_TYPE_PHONE -> log.info("[FAKE SMS] code=${vc.code} to phone=${vc.account}$suffix")
            else -> log.warn("Unknown idType=${vc.idType}, cannot send code=${vc.code} to ${vc.account}")
        }
    }

    private fun sendEmail(vc: VerificationCode) {
        val serial = vc.id?.takeLast(4) ?: "????"
        val subject = when {
            vc.lang?.startsWith("zh") == true -> "验证码"
            else -> "Verification Code"
        }
        val html = buildEmailHtml(vc.code, vc.lang, serial)
        try {
            val msg = mailSender.createMimeMessage()
            MimeMessageHelper(msg, true).apply {
                setTo(vc.account)
                setSubject(subject)
                setText(html, true) // true = HTML
                fromEmail?.let { setFrom(it) }
            }
            mailSender.send(msg)
            log.info("[EMAIL SENT] id = {} success", vc.id)
        } catch (e: Exception) {
            log.error("[EMAIL SENT] id = {} fail", vc.id, e)
        }
    }

    companion object {
        private val EMAIL_TEMPLATE: String by lazy {
            ClassPathResource("templates/verification-code-email.html").inputStream
                .readAllBytes()
                .toString(StandardCharsets.UTF_8)
        }
    }

    private fun buildEmailHtml(code: String, lang: String?, serial: String): String {
        val isZh = lang?.startsWith("zh") == true
        val title = if (isZh) "验证码" else "Verification Code"
        val codeLabel = if (isZh) "您的验证码" else "Your Code"
        val expireLabel = if (isZh) "有效期" else "Valid for"
        val minUnit = if (isZh) "分钟" else "minutes"
        val idLabel = if (isZh) "编号" else "ID"
        val ignoreMsg = if (isZh) "如果您没有请求此验证码，请忽略此邮件。" else "If you did not request this code, please ignore this email."
        val footer = if (isZh) "此邮件由系统自动发送，请勿回复。" else "This is an automated message, please do not reply."

        return EMAIL_TEMPLATE
            .replace("{title}", title)
            .replace("{codeLabel}", codeLabel)
            .replace("{code}", code)
            .replace("{expireLabel}", expireLabel)
            .replace("{expireMinutes}", cfg.verificationCodeExpireMinutes.toString())
            .replace("{minUnit}", minUnit)
            .replace("{idLabel}", idLabel)
            .replace("{serial}", serial)
            .replace("{ignoreMsg}", ignoreMsg)
            .replace("{footer}", footer)
    }
}
