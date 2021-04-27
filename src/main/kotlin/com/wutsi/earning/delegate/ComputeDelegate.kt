package com.wutsi.earning.`delegate`

import com.wutsi.earning.service.ContractProcessor
import com.wutsi.earning.service.PartnerProcessor
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import kotlin.Int

@Service
public class ComputeDelegate(
    private val contract: ContractProcessor,
    private val partner: PartnerProcessor
) {
    @Async
    public fun invoke(year: Int, month: Int) {
        contract.process(year, month)
        partner.process(year, month)
    }
}
