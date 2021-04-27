package com.wutsi.earning.`delegate`

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
public class ReplayDelegate(
    private val compute: ComputeDelegate
) {
    public fun invoke(startDate: LocalDate, endDate: LocalDate?) {
        var cur = startDate
        val today = LocalDate.now()
        val end = if (endDate == null || endDate.isAfter(today)) today else endDate
        while (cur.isBefore(end) || cur == end) {
            compute.invoke(cur.year, cur.monthValue)
            cur = cur.plusMonths(1)
        }
    }
}
