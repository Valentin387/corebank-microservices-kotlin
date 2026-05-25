package com.corebank.commons.security

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

object HeaderConstants {
    const val BEARER_PREFIX = "Bearer "
}

class ReactiveJwtFilter(private val jwtUtil: JwtUtil) : WebFilter {
    private val log = org.slf4j.LoggerFactory.getLogger(ReactiveJwtFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.uri.path

        if (path.contains("/api/auth/login") || path.startsWith("/actuator")) {
            return chain.filter(exchange)
        }

        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader != null && authHeader.startsWith(HeaderConstants.BEARER_PREFIX)) {
            val token = authHeader.substring(HeaderConstants.BEARER_PREFIX.length)

            if (jwtUtil.validateToken(token)) {
                log.debug("JWT token validated successfully for path: $path")
                val username = jwtUtil.extractUsername(token)
                val authToken = UsernamePasswordAuthenticationToken(username, null, emptyList())
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
            } else {
                log.warn("JWT token validation failed for path: $path")
            }
        } else {
            log.warn("Authorization header missing or invalid for path: $path")
        }

        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}
