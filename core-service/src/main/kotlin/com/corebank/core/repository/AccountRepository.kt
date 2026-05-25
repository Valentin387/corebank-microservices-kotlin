package com.corebank.core.repository

import com.corebank.commons.domain.Account
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AccountRepository : CoroutineCrudRepository<Account, Long> {
    suspend fun findByCustomerId(customerId: String): List<Account>
}
