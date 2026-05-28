package com.github.jingshouyan.sb40k.service

interface VerificationCodeService {

    /**
     * 触发验证码
     * @param target email 或 手机号
     * @param businessType 业务类型
     * @return 验证码记录 id
     */
    fun trigger(target: String, businessType: String): String
}
