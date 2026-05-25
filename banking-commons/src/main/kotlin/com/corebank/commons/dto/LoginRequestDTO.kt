package com.corebank.commons.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequestDTO(
    @field:NotBlank(message = "Username is required")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)
