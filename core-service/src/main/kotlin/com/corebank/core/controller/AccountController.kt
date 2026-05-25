package com.corebank.core.controller

import com.corebank.commons.domain.Account
import com.corebank.core.service.AccountApplicationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val service: AccountApplicationService
) {
    @GetMapping("/{customerId}")
    suspend fun getAccounts(@PathVariable customerId: String): ResponseEntity<List<Account>> {
        val accounts = service.getByCustomerId(customerId)
        return ResponseEntity.ok(accounts)
    }
}
