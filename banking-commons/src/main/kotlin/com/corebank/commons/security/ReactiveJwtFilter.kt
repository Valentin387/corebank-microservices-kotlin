package com.corebank.commons.security

import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(ReactiveJwtFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.uri.path

        log.debug("ReactiveJwtFilter ▸ path={}", path)

        if (path.contains("/api/auth/login") || path.startsWith("/actuator")) {
            log.debug("ReactiveJwtFilter ▸ SKIP (public path)")
            return chain.filter(exchange)
        }

        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        log.debug("ReactiveJwtFilter ▸ Authorization header present={}", authHeader != null)

        if (authHeader != null && authHeader.startsWith(HeaderConstants.BEARER_PREFIX)) {
            val token = authHeader.substring(HeaderConstants.BEARER_PREFIX.length)
            log.debug("ReactiveJwtFilter ▸ token length={}", token.length)

            val valid = jwtUtil.validateToken(token)
            log.debug("ReactiveJwtFilter ▸ token valid={}", valid)

            if (valid) {
                val username = jwtUtil.extractUsername(token)
                log.debug("ReactiveJwtFilter ▸ authenticated user={}", username)
                val authToken = UsernamePasswordAuthenticationToken(username, null, emptyList())
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
            } else {
                log.warn("ReactiveJwtFilter ▸ REJECTED — invalid token")
            }
        } else {
            log.warn("ReactiveJwtFilter ▸ REJECTED — missing or malformed Authorization header")
        }

        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}

