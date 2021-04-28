package com.wutsi.earning.cron

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.earning.delegate.ComputeDelegate
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class EarningJobsTest {
    private lateinit var delegate: ComputeDelegate
    private lateinit var job: EarningJobs

    @Test
    fun monthly() {
        delegate = mock()
        job = EarningJobs(delegate)

        job.monthly()

        val date = LocalDate.now().minusMonths(1)
        verify(delegate).invoke(date.year, date.monthValue)
    }
}
