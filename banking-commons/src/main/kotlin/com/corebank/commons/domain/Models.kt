package com.corebank.commons.domain

import org.springframework.data.annotation.Id
import java.math.BigDecimal

data class Account(
    @Id val id: Long? = null,
    val customerId: String,
    val accountNumber: String,
    val accountType: String,
    val balance: BigDecimal
)

data class Balance(
    val customerId: String,
    val totalBalance: BigDecimal,
    val availableBalance: BigDecimal
)

data class Card(
    @Id val id: Long? = null,
    val customerId: String,
    val cardNumber: String,
    val cardType: String,
    val creditLimit: BigDecimal,
    val availableBalance: BigDecimal
)
