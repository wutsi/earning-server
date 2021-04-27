package com.wutsi.earning.endpoint

import com.wutsi.earning.`delegate`.ComputeDelegate
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Int

@RestController
public class ComputeController(
    private val `delegate`: ComputeDelegate
) {
    @GetMapping("/v1/earnings/compute")
    public fun invoke(
        @RequestParam(name = "year", required = false) year: Int,
        @RequestParam(name = "month", required = false) month: Int
    ) {
        delegate.invoke(year, month)
    }
}
