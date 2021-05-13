package com.wutsi.earning.endpoint

import com.wutsi.earning.`delegate`.ComputeDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotNull
import kotlin.Int

@RestController
public class ComputeController(
    private val `delegate`: ComputeDelegate
) {
    @GetMapping("/v1/earnings/compute")
    @PreAuthorize(value = "hasAuthority('earning-manage')")
    public fun invoke(
        @RequestParam(name = "year", required = true) @NotNull year: Int,
        @RequestParam(name = "month", required = true) @NotNull month: Int
    ) {
        delegate.invoke(year, month)
    }
}
