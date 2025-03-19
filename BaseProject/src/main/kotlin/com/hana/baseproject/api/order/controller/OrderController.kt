package com.hana.baseproject.api.order.controller

import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class OrderController {

    @GetMapping("/v2/{orderCode}/company")
    fun showOrder(@PathVariable("orderCode") orderCode: String): APIResponse<String> {
        // 주문정보를 표기한다.
        return APIResponse.success("");
    }

    @PostMapping("/v1/create/order")
    fun createOrder(): APIResponse<String> {
        // 주문을 생성한다.
        return APIResponse.success("");
    }
    @PatchMapping("/v2/confirm/order")
    fun confirmOrder(): APIResponse<String> {
        // 주문을 확정한다.
        return APIResponse.success("");
    }
    @PatchMapping("/v2/finish/order")
    fun finishOrder(): APIResponse<String> {
        // 주문을 완료한다.
        return APIResponse.success("");
    }
    @PatchMapping("/v2/request_cancel/order")
    fun cancelRequestOrder(): APIResponse<String> {
        // 주문취소 요청을 보낸다.
        return APIResponse.success("");
    }

    @PatchMapping("/v2/cancel/order")
    fun cancelOrder(): APIResponse<String> {
        // 주문을 취소한다.
        return APIResponse.success("");
    }
}
