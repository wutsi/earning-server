package com.wutsi.earning.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.earning.delegate.ComputeDelegate
import com.wutsi.earning.dto.SearchEarningResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComputeControllerTest : ControllerTestBase() {
    @LocalServerPort
    public val port: Int = 0

    @MockBean
    private lateinit var delegate: ComputeDelegate

    @Test
    public fun `invoke`() {
        givenApiKey("earning.admin")

        val url = "http://localhost:$port/v1/earnings/compute?year=2020&month=1"
        val response = exchange(url, HttpMethod.GET, SearchEarningResponse::class.java)

        assertEquals(OK, response.statusCode)

        verify(delegate).invoke(2020, 1)
    }

    @Test
    fun `invoke with no permission`() {
        givenApiKey()

        try {
            val url = "http://localhost:$port/v1/earnings/compute?year=2020&month=1"
            exchange(url, HttpMethod.GET, Any::class.java)
            fail()
        } catch (ex: HttpClientErrorException) {
            assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
        } catch (ex: Exception) {
            fail()
        }
    }

    @Test
    fun `invoke with bad permission`() {
        givenApiKey("earning")

        try {
            val url = "http://localhost:$port/v1/earnings/compute?year=2020&month=1"
            exchange(url, HttpMethod.GET, Any::class.java)
            fail()
        } catch (ex: HttpClientErrorException) {
            assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
        } catch (ex: Exception) {
            fail()
        }
    }

    @Test
    fun `invoke as anonymous`() {
        try {
            val url = "http://localhost:$port/v1/earnings/compute?year=2020&month=1"
            exchange(url, HttpMethod.GET, Any::class.java)
            fail()
        } catch (ex: HttpClientErrorException) {
            assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
        } catch (ex: Exception) {
            fail()
        }
    }
}
