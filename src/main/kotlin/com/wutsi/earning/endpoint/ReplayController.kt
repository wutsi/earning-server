package com.wutsi.earning.endpoint

import com.wutsi.earning.`delegate`.ReplayDelegate
import org.springframework.format.`annotation`.DateTimeFormat
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import java.time.LocalDate

@RestController
public class ReplayController(
    private val `delegate`: ReplayDelegate
) {
    @GetMapping("/v1/earnings/replay")
    public fun invoke(
        @RequestParam(name = "start-date", required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
        @RequestParam(
            name = "end-date",
            required = false
        ) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate? = null
    ) {
        delegate.invoke(startDate, endDate)
    }
}
