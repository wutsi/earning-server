package com.wutsi.earning.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.security.SecurityApi
import com.wutsi.security.apikey.ApiKeyContext
import com.wutsi.security.dto.ApiKey
import com.wutsi.security.dto.GetApiKeyResponse
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.UUID

open class ControllerTestBase {
    @MockBean
    private lateinit var securityApi: SecurityApi

    @MockBean
    private lateinit var context: ApiKeyContext

    private val apiKeyId = UUID.randomUUID().toString()

    protected fun givenApiKey(scope: String? = null): ApiKey {
        doReturn(apiKeyId).whenever(context).id()

        val apiKey = ApiKey(
            id = "api-key",
            name = "test",
            scopes = if (scope == null) emptyList() else listOf(scope)
        )
        doReturn(GetApiKeyResponse(apiKey)).whenever(securityApi).get(any())
        return apiKey
    }

    protected fun <T> exchange(url: String, method: HttpMethod, type: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.put("Authorization", listOf(apiKeyId))
        val request = HttpEntity("", headers)
        return RestTemplate().exchange(url, method, request, type)
    }
}
