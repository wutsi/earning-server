package com.wutsi.earning.service

interface Processor {
    fun process(year: Int, month: Int)
}
