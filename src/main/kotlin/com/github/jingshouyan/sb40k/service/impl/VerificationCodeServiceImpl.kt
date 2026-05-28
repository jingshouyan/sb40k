package com.github.jingshouyan.sb40k.service.impl

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.github.jingshouyan.sb40k.config.BizConfig
import com.github.jingshouyan.sb40k.entity.VerificationCode
import com.github.jingshouyan.sb40k.mapper.VerificationCodeMapper
import com.github.jingshouyan.sb40k.service.VerificationCodeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class VerificationCodeServiceImpl(
    private val verificationCodeMapper: VerificationCodeMapper,
    private val cfg: BizConfig,
) : VerificationCodeService {

    private val log = LoggerFactory.getLogger(VerificationCodeServiceImpl::class.java)

    override fun trigger(target: String, businessType: String): String {
        val now = System.currentTimeMillis()
        val expireMs = cfg.verificationCodeExpireMinutes * 60 * 1000
        val resendIntervalMs = cfg.verificationCodeResendIntervalSeconds * 1000

        val existing = verificationCodeMapper.selectOne(
            LambdaQueryWrapper<VerificationCode>()
                .eq(VerificationCode::target, target)
                .eq(VerificationCode::businessType, businessType)
                .gt(VerificationCode::expireAt, now)
                .orderByDesc(VerificationCode::sentAt)
                .last("LIMIT 1")
        )

        return if (existing != null) {
            if (now - existing.sentAt < resendIntervalMs) {
                // 1 分钟内，直接返回已有 id
                existing.id!!
            } else {
                // 超过 1 分钟，重新生成验证码，刷新过期时间
                val newCode = generateCode()
                existing.code = newCode
                existing.sentAt = now
                existing.expireAt = now + expireMs
                verificationCodeMapper.updateById(existing)
                sendCode(target, newCode)
                existing.id!!
            }
        } else {
            // 无未过期记录，新建
            val code = generateCode()
            val entity = VerificationCode(
                target = target,
                code = code,
                businessType = businessType,
                sentAt = now,
                expireAt = now + expireMs,
            )
            verificationCodeMapper.insert(entity)
            sendCode(target, code)
            entity.id!!
        }
    }

    private fun generateCode(): String {
        return (100000..999999).random().toString()
    }

    private fun sendCode(target: String, code: String) {
        log.info("[FAKE SEND] code=$code to target=$target")
    }
}
