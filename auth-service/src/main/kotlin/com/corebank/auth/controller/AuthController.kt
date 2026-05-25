package com.corebank.auth.controller

import com.corebank.auth.service.AuthApplicationService
import com.corebank.commons.dto.LoginRequestDTO
import com.corebank.commons.model.ResponseDTO
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthApplicationService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestDTO): ResponseEntity<ResponseDTO<String>> {
        val token = authService.authenticate(request)
        return if (token != null) {
            ResponseEntity.ok(ResponseDTO.success(token))
        } else {
            ResponseEntity.status(401).body(ResponseDTO.error(401, "Invalid credentials"))
        }
    }
}
