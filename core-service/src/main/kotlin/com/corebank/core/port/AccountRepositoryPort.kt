package com.corebank.core.port

import com.corebank.commons.domain.Account

interface AccountRepositoryPort {
    suspend fun findByCustomerId(customerId: String): List<Account>
}
