package com.corebank.auth.config

import com.corebank.commons.security.JwtUtil
import com.corebank.commons.security.ReactiveJwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(private val jwtUtil: JwtUtil) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/auth/login", "/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(ReactiveJwtFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
