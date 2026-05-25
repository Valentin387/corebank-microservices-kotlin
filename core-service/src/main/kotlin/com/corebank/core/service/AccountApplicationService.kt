package com.corebank.core.service

import com.corebank.commons.domain.Account
import com.corebank.core.port.AccountRepositoryPort
import org.springframework.stereotype.Service

@Service
class AccountApplicationService(
    private val repositoryPort: AccountRepositoryPort
) {
    suspend fun getByCustomerId(customerId: String): List<Account> {
        return repositoryPort.findByCustomerId(customerId)
    }
}
