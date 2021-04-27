package com.wutsi.earning

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.`annotation`.EnableAsync
import org.springframework.scheduling.`annotation`.EnableScheduling
import org.springframework.transaction.`annotation`.EnableTransactionManagement
import kotlin.String

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
