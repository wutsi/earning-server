package com.wutsi.earning.cron

import com.wutsi.earning.delegate.ComputeDelegate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class EarningJobs(private val compute: ComputeDelegate) {
    @Scheduled(cron = "\${wutsi.cron.monthly-compute}")
    fun monthly() {
        val lastMonth = LocalDate.now().minusMonths(1)
        compute.invoke(lastMonth.year, lastMonth.monthValue)
    }
}
