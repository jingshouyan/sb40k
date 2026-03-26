package com.github.jingshouyan.sb40k.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@ConfigurationProperties("biz")
class BizConfig {

    var tokenExpireSeconds: Long = 30 * 24 * 3600

    var passwordMaxTry: Int = 5

    var passwordTryPeriodInSeconds: Long = 5 * 60

    var passwordLockExpireSeconds: Long = 30 * 60

    var passwordExpireDays: Long = 180

    var tokenSecret: String = "sb40k_abc1113"

    var maskSettings = ConcurrentHashMap<String, Int>()


}