package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.github.jingshouyan.sb40k.base.C
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.VerificationCode
import com.github.jingshouyan.sb40k.mapper.VerificationCodeMapper
import com.github.jingshouyan.sb40k.service.VerificationCodeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

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
        val text = when {
            vc.lang?.startsWith("zh") == true -> "您的验证码是：${vc.code}，有效期 ${cfg.verificationCodeExpireMinutes} 分钟。（编号：$serial）"
            else -> "Your verification code is: ${vc.code}, valid for ${cfg.verificationCodeExpireMinutes} minutes. (ID: $serial)"
        }
        try {
            val msg = SimpleMailMessage().apply {
                setTo(vc.account)
                setSubject(subject)
                setText(text)
                from = fromEmail
            }
            mailSender.send(msg)
            log.info("[EMAIL SENT] id = {} success", vc.id)
        } catch (e: Exception) {
            log.error("[EMAIL SENT] id = {} fail", vc.id, e)
        }
    }
}
