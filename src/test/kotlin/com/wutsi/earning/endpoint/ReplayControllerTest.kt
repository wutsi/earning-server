package com.wutsi.earning.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.earning.delegate.ReplayDelegate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReplayControllerTest : ControllerTestBase() {
    @LocalServerPort
    public val port: Int = 0

    @MockBean
    lateinit var delegate: ReplayDelegate

    @Test
    public fun `replay`() {
        login("earning-manage")

        val url = "http://localhost:$port/v1/earnings/replay?start-date=2020-10-01&end-date=2020-11-01"
        val response = exchange(url, HttpMethod.GET, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(delegate).invoke(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 11, 1))
    }

    @Test
    public fun `return 403 when scope is invalid`() {
        login("earning")

        val url = "http://localhost:$port/v1/earnings/replay?start-date=2020-10-01&end-date=2020-11-01"
        val ex = assertThrows<HttpClientErrorException> {
            exchange(url, HttpMethod.GET, Any::class.java)
        }
        assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
    }
}
