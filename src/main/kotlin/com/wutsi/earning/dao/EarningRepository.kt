package com.wutsi.earning.dao

import com.wutsi.earning.entity.EarningEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EarningRepository : CrudRepository<EarningEntity, Long> {
    fun findByUserIdAndYear(userId: Long, year: Int): List<EarningEntity>
    fun findByUserIdAndYearAndMonth(userId: Long, year: Int, month: Int): Optional<EarningEntity>
    fun findByYearAndMonth(year: Int, month: Int): List<EarningEntity>
    fun findEarningsByYearAndMonthAndPartnerIdNotNull(year: Int, month: Int): List<EarningEntity>
    fun findEarningsByYearAndMonthAndContractIdNotNull(year: Int, month: Int): List<EarningEntity>
}
