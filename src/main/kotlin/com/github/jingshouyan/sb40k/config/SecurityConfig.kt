package com.github.jingshouyan.sb40k.config

import com.github.jingshouyan.sb40k.filter.AuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(val authFilter: AuthFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        return http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/**/signin",
                    "/**/signup",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                authFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }
}