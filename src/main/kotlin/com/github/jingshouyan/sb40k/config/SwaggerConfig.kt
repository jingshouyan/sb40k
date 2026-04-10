package com.github.jingshouyan.sb40k.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
                    .addParameters(
                        "remoteIP",
                        Parameter().`in`("header").name("X-Forwarded-For").description("客户端IP地址")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
    }

    @Bean
    fun globalHeaderCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, _ ->
            operation.addParametersItem(
                Parameter().`$ref`("#/components/parameters/remoteIP")
            )
            operation
        }
    }
}