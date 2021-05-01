package com.wutsi.earning.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.partner.PartnerApi
import com.wutsi.partner.PartnerApiBuilder
import com.wutsi.security.apikey.ApiKeyRequestInterceptor
import com.wutsi.tracing.TracingRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
public class PartnerConfiguration(
    @Autowired private val env: Environment,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val tracingRequestInterceptor: TracingRequestInterceptor,
    @Autowired private val apiKeyRequestInterceptor: ApiKeyRequestInterceptor
) {
    @Bean
    fun partnerApi(): PartnerApi =
        PartnerApiBuilder()
            .build(
                env = partnerEnvironment(),
                mapper = mapper,
                interceptors = listOf(tracingRequestInterceptor, apiKeyRequestInterceptor)
            )

    fun partnerEnvironment(): com.wutsi.partner.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            com.wutsi.partner.Environment.PRODUCTION
        else
            com.wutsi.partner.Environment.SANDBOX
}
