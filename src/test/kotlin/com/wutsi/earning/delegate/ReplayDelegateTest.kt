package com.wutsi.earning.delegate

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ReplayDelegateTest {
    private lateinit var compute: ComputeDelegate
    private lateinit var delegate: ReplayDelegate

    @Test
    fun invoke() {
        compute = mock()
        delegate = ReplayDelegate(compute)

        val start = LocalDate.of(2020, 10, 1)
        val end = LocalDate.of(2020, 12, 2)
        delegate.invoke(start, end)

        verify(compute).invoke(2020, 10)
        verify(compute).invoke(2020, 11)
        verify(compute).invoke(2020, 12)
    }

    @Test
    fun invokeNoEndDate() {
        compute = mock()
        delegate = ReplayDelegate(compute)

        val today = LocalDate.now()
        val start = today.minusMonths(1)
        delegate.invoke(start, null)

        verify(compute).invoke(start.year, start.monthValue)
        verify(compute).invoke(today.year, today.monthValue)
    }

    @Test
    fun invokeEndDateAfterToday() {
        compute = mock()
        delegate = ReplayDelegate(compute)

        val today = LocalDate.now()
        val start = today.minusMonths(1)
        val end = today.plusMonths(1)
        delegate.invoke(start, end)

        verify(compute).invoke(start.year, start.monthValue)
        verify(compute).invoke(today.year, today.monthValue)
        verify(compute, never()).invoke(end.year, end.monthValue)
    }
}
