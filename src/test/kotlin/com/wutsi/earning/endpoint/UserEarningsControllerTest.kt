package com.wutsi.earning.endpoint

import com.wutsi.earning.dto.SearchEarningResponse
import com.wutsi.earning.dto.SecurityScope
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UserEarningsController.sql"])
public class UserEarningsControllerTest : ControllerTestBase() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    fun `invoke`() {
        givenApiKey(SecurityScope.EARNING.scope)

        val url = "http://localhost:$port/v1/earnings/users/1?year=2020"
        val response = exchange(url, HttpMethod.GET, SearchEarningResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(3, response.body.earnings.size)

        assertEquals(LocalDate.of(2020, 10, 1), response.body.earnings[0].date)
        assertEquals(100, response.body.earnings[0].amount)
        assertEquals("XAF", response.body.earnings[0].currency)
        assertEquals(1000L, response.body.earnings[0].partnerId)
        assertNull(response.body.earnings[0].contractId)

        assertEquals(LocalDate.of(2020, 11, 1), response.body.earnings[1].date)
        assertEquals(101, response.body.earnings[1].amount)
        assertEquals("XAF", response.body.earnings[1].currency)
        assertEquals(1000L, response.body.earnings[1].partnerId)
        assertNull(response.body.earnings[1].contractId)

        assertEquals(LocalDate.of(2020, 12, 1), response.body.earnings[2].date)
        assertEquals(102, response.body.earnings[2].amount)
        assertEquals("XAF", response.body.earnings[2].currency)
        assertNull(response.body.earnings[2].partnerId)
        assertEquals(100, response.body.earnings[2].contractId)
    }

    @Test
    fun `invoke with no permission`() {
        givenApiKey()

        try {
            val url = "http://localhost:$port/v1/earnings/users/1?year=2020"
            exchange(url, HttpMethod.GET, SearchEarningResponse::class.java)
            fail()
        } catch (ex: HttpClientErrorException) {
            assertEquals(HttpStatus.FORBIDDEN, ex.statusCode)
        } catch (ex: Exception) {
            fail()
        }
    }
}
