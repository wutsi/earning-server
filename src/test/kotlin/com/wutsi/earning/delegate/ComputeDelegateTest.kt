package com.wutsi.earning.delegate

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.earning.service.ContractProcessor
import com.wutsi.earning.service.PartnerProcessor
import org.junit.jupiter.api.Test

internal class ComputeDelegateTest {
    private lateinit var contract: ContractProcessor
    private lateinit var partner: PartnerProcessor
    private lateinit var delegate: ComputeDelegate

    @Test
    operator fun invoke() {
        contract = mock()
        partner = mock()
        delegate = ComputeDelegate(contract, partner)

        delegate.invoke(2020, 10)

        verify(contract).process(2020, 10)
        verify(partner).process(2020, 10)
    }
}
