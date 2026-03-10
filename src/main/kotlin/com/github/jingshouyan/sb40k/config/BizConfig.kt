package com.github.jingshouyan.sb40k.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("biz")
class BizConfig {

    var tokenExpireSeconds: Long = 0

    var passwordMaxTry: Int = 5

    var passwordTryPeriodInSeconds: Long = 5_000L

    var passwordLockExpireSeconds: Long = 30_000L

    var passwordExpireDays: Long = 180


}