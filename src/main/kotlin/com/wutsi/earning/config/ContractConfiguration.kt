package com.wutsi.earning.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.contract.ContractApi
import com.wutsi.contract.ContractApiBuilder
import com.wutsi.platform.security.apikey.ApiKeyRequestInterceptor
import com.wutsi.tracing.TracingRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
public class ContractConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val tracingRequestInterceptor: TracingRequestInterceptor,
    @Autowired private val apiKeyRequestInterceptor: ApiKeyRequestInterceptor
) {
    @Bean
    fun contractApi(): ContractApi =
        ContractApiBuilder()
            .build(
                env = contractEnvironment(),
                mapper = mapper,
                interceptors = listOf(tracingRequestInterceptor, apiKeyRequestInterceptor)
            )

    fun contractEnvironment(): com.wutsi.contract.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.contract.Environment.PRODUCTION
        else
            com.wutsi.contract.Environment.SANDBOX
}
