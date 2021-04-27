package com.wutsi.earning.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.earning.SiteAttribute
import com.wutsi.earning.dao.EarningRepository
import com.wutsi.partner.PartnerApi
import com.wutsi.partner.dto.Partner
import com.wutsi.partner.dto.SearchPartnerResponse
import com.wutsi.site.SiteApi
import com.wutsi.site.dto.Attribute
import com.wutsi.site.dto.GetSiteResponse
import com.wutsi.site.dto.Site
import com.wutsi.stats.StatsApi
import com.wutsi.stats.dto.SearchUserKpiResponse
import com.wutsi.stats.dto.UserKpi
import com.wutsi.user.UserApi
import com.wutsi.user.dto.GetUserResponse
import com.wutsi.user.dto.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PartnerProcessorTest {
    @Autowired
    private lateinit var processor: PartnerProcessor

    @Autowired
    private lateinit var dao: EarningRepository

    @MockBean
    private lateinit var statsApi: StatsApi

    @MockBean
    private lateinit var userApi: UserApi

    @MockBean
    private lateinit var siteApi: SiteApi

    @MockBean
    private lateinit var partnerApi: PartnerApi

    @Test
    @Sql(value = ["/db/clean.sql", "/db/PartnerProcessor.sql"])
    fun `save earnings`() {
        val site = createSite(50000, 1000)
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        doReturn(GetUserResponse(createUser(1))).whenever(userApi).get(1L)

        doReturn(SearchUserKpiResponse(listOf(createKpi(1L, 80)))).whenever(statsApi).userMonthlyKpis(
            any(), any(), any(), any(), any(), any()
        )

        doReturn(SearchPartnerResponse(listOf(createPartner(100L, 1L))))
            .doReturn(SearchPartnerResponse(listOf()))
            .whenever(partnerApi).active(any(), any())

        processor.process(2020, 10)

        val earnings = dao.findByYearAndMonth(2020, 10).sortedBy { it.userId }

        assertEquals(1, earnings.size)

        assertEquals(1, earnings[0].userId)
        assertEquals(100, earnings[0].partnerId)
        assertEquals(40000, earnings[0].amount)
        assertEquals(site.currency, earnings[0].currency)
        assertNull(earnings[0].contractId)
    }

    @Test
    @Sql(value = ["/db/clean.sql", "/db/PartnerProcessor.sql"])
    fun `do not save earnings below threshold`() {
        val site = createSite(50000, 1000)
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        doReturn(GetUserResponse(createUser(1))).whenever(userApi).get(1L)

        doReturn(SearchUserKpiResponse(listOf(createKpi(1L, 1)))).whenever(statsApi).userMonthlyKpis(
            any(), any(), any(), any(), any(), any()
        )

        doReturn(SearchPartnerResponse(listOf(createPartner(100L, 1L))))
            .doReturn(SearchPartnerResponse(listOf()))
            .whenever(partnerApi).active(any(), any())

        processor.process(2020, 10)

        val earnings = dao.findByYearAndMonth(2020, 10).sortedBy { it.userId }

        assertEquals(0, earnings.size)
    }

    @Test
    @Sql(value = ["/db/clean.sql", "/db/PartnerProcessor.sql"])
    fun `do not save earnings when no wpp value`() {
        val site = createSite(0, 1000)
        doReturn(GetSiteResponse(site)).whenever(siteApi).get(1L)

        doReturn(GetUserResponse(createUser(1))).whenever(userApi).get(1L)

        doReturn(SearchUserKpiResponse(listOf(createKpi(1L, 80)))).whenever(statsApi).userMonthlyKpis(
            any(), any(), any(), any(), any(), any()
        )

        doReturn(SearchPartnerResponse(listOf(createPartner(100L, 1L))))
            .doReturn(SearchPartnerResponse(listOf()))
            .whenever(partnerApi).active(any(), any())

        processor.process(2020, 10)

        val earnings = dao.findByYearAndMonth(2020, 10).sortedBy { it.userId }

        assertEquals(0, earnings.size)
    }

    private fun createSite(wpp: Long = 50000, threshold: Long = 2000) = Site(
        id = 1,
        currency = "XAF",
        attributes = listOf(
            Attribute(urn = SiteAttribute.WPP_THRESHOLD.urn, value = threshold.toString()),
            Attribute(urn = SiteAttribute.WPP_MONTHLY_AMOUNT.urn, value = wpp.toString())
        )
    )

    private fun createUser(id: Long) = User(
        id = id,
        siteId = 1L
    )

    private fun createPartner(id: Long, userId: Long) = Partner(
        id = id,
        userId = userId
    )

    private fun createKpi(userId: Long, value: Long) = UserKpi(
        userId = userId,
        value = value
    )
}
