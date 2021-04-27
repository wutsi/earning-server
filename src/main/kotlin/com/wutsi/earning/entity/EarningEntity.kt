package com.wutsi.earning.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_EARNING")
data class EarningEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long = -1,

    @Column(name = "contract_id")
    var contractId: Long? = null,

    @Column(name = "partner_id")
    var partnerId: Long? = null,

    val month: Int = -1,
    val year: Int = -1,
    var amount: Long = 0,
    var currency: String = ""
)
