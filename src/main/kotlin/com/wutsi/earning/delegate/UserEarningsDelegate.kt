package com.wutsi.earning.`delegate`

import com.wutsi.earning.dao.EarningRepository
import com.wutsi.earning.dto.Earning
import com.wutsi.earning.dto.SearchEarningResponse
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.Int
import kotlin.Long

@Service
public class UserEarningsDelegate(
    private val dao: EarningRepository
) {
    public fun invoke(userId: Long, year: Int): SearchEarningResponse {
        val earnings = dao.findByUserIdAndYear(userId, year)
        return SearchEarningResponse(
            earnings = earnings.map {
                Earning(
                    id = it.id ?: -1,
                    userId = it.userId,
                    partnerId = it.partnerId,
                    contractId = it.contractId,
                    currency = it.currency,
                    amount = it.amount,
                    date = LocalDate.of(it.year, it.month, 1)
                )
            }
        )
    }
}
