package com.github.jingshouyan.sb40k.service

interface VerificationCodeService {

    /**
     * 触发验证码
     * @param account email 或 手机号
     * @param idType C.ID_TYPE_EMAIL / C.ID_TYPE_PHONE
     * @param businessType 业务类型
     * @param lang 语言
     * @param params 发送时附加参数
     * @return 验证码记录 id
     */
    fun trigger(
        account: String,
        idType: Int,
        businessType: String,
        lang: String? = null,
        params: Map<String, String>? = null
    ): String

    /**
     * 核验验证码
     * @param id 验证码记录 id
     * @param code 用户输入的验证码
     * @param account email 或 手机号
     * @param idType C.ID_TYPE_EMAIL / C.ID_TYPE_PHONE
     * @param businessType 业务类型
     * @return true=验证通过
     */
    fun verify(id: String, code: String, account: String, idType: Int, businessType: String): Boolean
}
