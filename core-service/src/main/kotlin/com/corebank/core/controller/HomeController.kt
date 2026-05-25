package com.corebank.core.controller

import com.corebank.commons.domain.Account
import com.corebank.commons.domain.Balance
import com.corebank.commons.domain.Card
import com.corebank.core.service.AccountApplicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class HomeAggregateDTO(
    val accounts: List<Account>,
    val balances: List<Balance>,
    val cards: List<Card>
)

@RestController
@RequestMapping("/api/home")
class HomeController(
    private val accountService: AccountApplicationService
) {
    @GetMapping("/{customerId}")
    suspend fun getHomeData(@PathVariable customerId: String): HomeAggregateDTO {
        val accounts = accountService.getByCustomerId(customerId)
        val balances = listOf(Balance(customerId, BigDecimal.ZERO, BigDecimal.ZERO))
        val cards = emptyList<Card>()
        return HomeAggregateDTO(accounts, balances, cards)
    }
}
