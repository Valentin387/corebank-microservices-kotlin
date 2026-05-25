package com.corebank.auth.service

import com.corebank.commons.dto.LoginRequestDTO
import com.corebank.commons.security.JwtUtil
import org.springframework.stereotype.Service

@Service
class AuthApplicationService(private val jwtUtil: JwtUtil) {

    fun authenticate(request: LoginRequestDTO): String? {
        // Mock authentication, in a real system this would check a DB
        return if (request.username == "admin" && request.password == "password") {
            jwtUtil.generateToken(request.username, mapOf("role" to "ADMIN"))
        } else if (request.username == "user" && request.password == "password") {
            jwtUtil.generateToken(request.username, mapOf("role" to "USER"))
        } else {
            null
        }
    }
}
