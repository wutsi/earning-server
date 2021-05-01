package com.wutsi.earning.config

import com.wutsi.security.apikey.ApiKeyAuthenticationProvider
import com.wutsi.security.apikey.ApiKeyProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.servlet.Filter

@Configuration
public class SecurityConfiguration(
    @Autowired
    private val apiKeyProvider: ApiKeyProvider,
    @Value(value = "\${security.api-key.header}")
    private val apiKeyHeader: String
) : WebSecurityConfigurerAdapter() {
    public override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            )
            .and()
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(
                authenticationFilter(),
                org.springframework.security.web.authentication.AnonymousAuthenticationFilter::class.java
            )
    }

    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(apiKeyAuthenticationProvider())
    }

    private fun apiKeyAuthenticationProvider(): ApiKeyAuthenticationProvider =
        ApiKeyAuthenticationProvider()

    public fun authenticationFilter(): Filter {
        val filter = com.wutsi.security.apikey.ApiKeyAuthenticationFilter(
            headerName = apiKeyHeader,
            apiProvider = apiKeyProvider,
            pattern = "/**"
        )
        filter.setAuthenticationManager(authenticationManagerBean())
        return filter
    }
}
