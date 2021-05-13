package com.wutsi.earning.endpoint

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthControllerTest : ControllerTestBase() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun healthCheck() {
        RestTemplate().getForEntity("http://localhost:$port/actuator/health", Any::class.java)
    }
}
