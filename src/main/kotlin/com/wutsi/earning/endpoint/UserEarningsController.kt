package com.wutsi.earning.endpoint

import com.wutsi.earning.`delegate`.UserEarningsDelegate
import com.wutsi.earning.dto.SearchEarningResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.Long

@RestController
public class UserEarningsController(
    private val `delegate`: UserEarningsDelegate
) {
    @GetMapping("/v1/earnings/users/{user-id}")
    @PreAuthorize(value = "hasAuthority('earning-read')")
    public fun invoke(
        @PathVariable(name = "user-id") @NotNull userId: Long,
        @RequestParam(
            name = "year",
            required = true
        ) @NotNull year: Int
    ): SearchEarningResponse = delegate.invoke(userId, year)
}
