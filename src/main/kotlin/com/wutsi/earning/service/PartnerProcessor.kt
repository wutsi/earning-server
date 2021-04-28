package com.wutsi.earning.service

import com.wutsi.earning.SiteAttribute
import com.wutsi.earning.SiteAttribute.WPP_MONTHLY_AMOUNT
import com.wutsi.earning.SiteAttribute.WPP_THRESHOLD
import com.wutsi.earning.dao.EarningRepository
import com.wutsi.earning.entity.EarningEntity
import com.wutsi.partner.PartnerApi
import com.wutsi.partner.dto.Partner
import com.wutsi.site.SiteApi
import com.wutsi.site.dto.Site
import com.wutsi.stats.StatsApi
import com.wutsi.stats.dto.KpiType
import com.wutsi.user.UserApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PartnerProcessor(
    private val partnerApi: PartnerApi,
    private val statsApi: StatsApi,
    private val userApi: UserApi,
    private val siteApi: SiteApi,
    private val dao: EarningRepository
) : Processor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PartnerProcessor::class.java)
    }

    @Transactional
    override fun process(year: Int, month: Int) {
        LOGGER.info("Computing Partner Earnings")

        val entities = doProcess(year, month)
        clear(year, month, entities)
    }

    private fun clear(year: Int, month: Int, saved: List<EarningEntity>) {
        val earnings = dao.findEarningsByYearAndMonthAndPartnerIdNotNull(year, month)
        val ids = saved.map { it.id }

        earnings.forEach {
            if (!ids.contains(it.id))
                dao.delete(it)
        }
    }

    private fun doProcess(year: Int, month: Int): List<EarningEntity> {
        val limit = 100
        var offset = 0
        val sites = mutableMapOf<Long, Site>()
        val earnings = mutableListOf<EarningEntity>()
        while (true) {
            val partners = partnerApi.active(limit, offset).partners
            if (partners.isEmpty())
                break

            partners.forEach {
                val entity = doProcess(year, month, it, sites)
                if (entity != null)
                    earnings.add(entity)
            }

            offset += partners.size
        }
        return earnings
    }

    private fun doProcess(year: Int, month: Int, partner: Partner, sites: MutableMap<Long, Site>): EarningEntity? {
        val user = userApi.get(partner.userId).user
        val site = findSite(user.siteId, sites)
        val wppAmount = siteAttributeAsLong(WPP_MONTHLY_AMOUNT, site)
        if (wppAmount <= 0) {
            LOGGER.warn("siteId=${site.id} - WPP-Amount not available")
            return null
        }

        // Get WPP Ratio
        val kpis = statsApi.userMonthlyKpis(
            userId = user.id,
            type = KpiType.WPP_READ_RATIO.name,
            year = year,
            month = month,
            limit = 1,
            offset = 0
        ).kpis
        if (kpis.isEmpty()) {
            return null
        }
        val ratio = kpis[0].value.toDouble() / 100

        // Compute the earning
        val threshold = siteAttributeAsLong(WPP_THRESHOLD, site)
        val earning = wppAmount.toDouble() * ratio
        if (earning < threshold) {
            LOGGER.warn("userId=${user.id} earning=$earning threshold=$threshold - Earning below threshold")
            return null
        }

        // Save
        LOGGER.warn("userId=${user.id} earning=$earning - Saving")
        return save(year, month, earning.toLong(), partner, site)
    }

    private fun save(year: Int, month: Int, amount: Long, partner: Partner, site: Site): EarningEntity {
        val opt = dao.findByUserIdAndYearAndMonth(partner.userId, year, month)
        if (opt.isEmpty) {
            val entity = create(year, month, amount, partner, site)
            dao.save(entity)
            return entity
        } else {
            val entity = update(opt.get(), amount, partner, site)
            dao.save(entity)
            return entity
        }
    }

    private fun create(year: Int, month: Int, amount: Long, partner: Partner, site: Site) = EarningEntity(
        year = year,
        month = month,
        currency = site.currency,
        amount = amount,
        userId = partner.userId,
        contractId = null,
        partnerId = partner.id
    )

    private fun update(entity: EarningEntity, amount: Long, partner: Partner, site: Site): EarningEntity {
        entity.amount = amount
        entity.currency = site.currency
        entity.contractId = null
        entity.partnerId = partner.id
        return entity
    }

    private fun findSite(siteId: Long, sites: MutableMap<Long, Site>): Site {
        var site = sites[siteId]
        if (site != null)
            return site

        site = siteApi.get(siteId).site
        sites[siteId] = site
        return site
    }

    private fun siteAttributeAsLong(attribute: SiteAttribute, site: Site): Long {
        val attr = site.attributes.find { it.urn == attribute.urn }
        try {
            return attr?.let { it.value.toLong() } ?: 0
        } catch (ex: Exception) {
            return 0
        }
    }
}
