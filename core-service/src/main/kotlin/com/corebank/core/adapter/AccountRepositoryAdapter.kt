package com.corebank.core.adapter

import com.corebank.commons.domain.Account
import com.corebank.core.port.AccountRepositoryPort
import com.corebank.core.repository.AccountRepository
import org.springframework.stereotype.Component

@Component
class AccountRepositoryAdapter(
    private val repository: AccountRepository
) : AccountRepositoryPort {
    override suspend fun findByCustomerId(customerId: String): List<Account> {
        return repository.findByCustomerId(customerId)
    }
}
