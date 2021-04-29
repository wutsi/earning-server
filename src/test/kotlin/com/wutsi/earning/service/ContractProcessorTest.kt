package com.wutsi.earning.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.contract.ContractApi
import com.wutsi.contract.dto.Contract
import com.wutsi.contract.dto.SearchContractResponse
import com.wutsi.earning.dao.EarningRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ContractProcessor.sql"])
internal class ContractProcessorTest {
    @Autowired
    private lateinit var processor: ContractProcessor

    @Autowired
    private lateinit var dao: EarningRepository

    @MockBean
    private lateinit var api: ContractApi

    @Test
    fun process() {
        val c1 = createContract(100, 1, 50000, "XAF")
        val c2 = createContract(200, 2, 25000, "XAF")
        doReturn(SearchContractResponse(listOf(c1, c2))).whenever(api).active(any())

        processor.process(2020, 10)

        val earnings = dao.findByYearAndMonth(2020, 10).sortedBy { it.userId }

        assertEquals(2, earnings.size)

        assertEquals(c1.userId, earnings[0].userId)
        assertEquals(c1.id, earnings[0].contractId)
        assertEquals(c1.amount, earnings[0].amount)
        assertEquals(c1.currency, earnings[0].currency)
        assertNull(earnings[0].partnerId)

        assertEquals(c2.userId, earnings[1].userId)
        assertEquals(c2.id, earnings[1].contractId)
        assertEquals(c2.amount, earnings[1].amount)
        assertEquals(c2.currency, earnings[1].currency)
        assertNull(earnings[1].partnerId)
    }

    private fun createContract(id: Long, userId: Long, amount: Long, currency: String = "XAF") = Contract(
        id = id,
        userId = userId,
        amount = amount,
        currency = currency
    )
}
