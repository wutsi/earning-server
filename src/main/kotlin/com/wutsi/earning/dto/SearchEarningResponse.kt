package com.wutsi.earning.dto

import kotlin.collections.List

public data class SearchEarningResponse(
    public val earnings: List<Earning> = emptyList()
)
