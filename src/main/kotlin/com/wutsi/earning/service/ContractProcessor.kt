package com.wutsi.earning.service

import com.wutsi.contract.ContractApi
import com.wutsi.contract.dto.Contract
import com.wutsi.earning.dao.EarningRepository
import com.wutsi.earning.entity.EarningEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
class ContractProcessor(
    private val api: ContractApi,
    private val dao: EarningRepository
) : Processor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ContractProcessor::class.java)
    }

    @Transactional
    override fun process(year: Int, month: Int) {
        LOGGER.info("Computing Contract Earnings")

        val entities = doProcess(year, month)
        clear(year, month, entities)
    }

    private fun clear(year: Int, month: Int, saved: List<EarningEntity>) {
        val earnings = dao.findEarningsByYearAndMonthAndContractIdNotNull(year, month)
        val ids = saved.map { it.id }

        earnings.forEach {
            if (!ids.contains(it.id))
                dao.delete(it)
        }
    }

    private fun doProcess(year: Int, month: Int): List<EarningEntity> {
        val contracts = api.active(LocalDate.of(year, month, 1)).contracts
        val earnings = mutableListOf<EarningEntity>()
        contracts.forEach {
            val earning = save(year, month, it)
            earnings.add(earning)
        }

        LOGGER.info("${contracts.size} earnings saved")
        return earnings
    }

    private fun save(year: Int, month: Int, contract: Contract): EarningEntity {
        val opt = dao.findByUserIdAndYearAndMonth(contract.userId, year, month)
        if (opt.isEmpty) {
            val entity = create(year, month, contract)
            dao.save(entity)
            return entity
        } else {
            val entity = update(opt.get(), contract)
            dao.save(entity)
            return entity
        }
    }

    private fun create(year: Int, month: Int, contract: Contract) = EarningEntity(
        year = year,
        month = month,
        currency = contract.currency,
        amount = contract.amount,
        userId = contract.userId,
        contractId = contract.id,
        partnerId = null
    )

    private fun update(entity: EarningEntity, contract: Contract): EarningEntity {
        entity.amount = contract.amount
        entity.currency = contract.currency
        entity.contractId = contract.id
        entity.partnerId = null
        return entity
    }
}
