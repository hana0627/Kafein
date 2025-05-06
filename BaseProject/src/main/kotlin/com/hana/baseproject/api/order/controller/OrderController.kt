package com.hana.baseproject.api.order.controller

import com.hana.baseproject.api.order.controller.request.OrderCreate
import com.hana.baseproject.api.order.controller.response.OrderInformation
import com.hana.baseproject.api.order.service.OrderService
import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*


// TODO List<OrderInformation> 대신 OrderInformation에 List<> 필드를 가지는 방식으로 변경
@RestController
class OrderController (
    private val orderService: OrderService
){

    @GetMapping("/v2/{orderCode}")
    fun showOrder(@PathVariable("orderCode") orderCode: String): APIResponse<List<OrderInformation>> {
        // 주문정보를 표기한다.
        val result: List<OrderInformation> = orderService.getOrder(orderCode);
        return APIResponse.success(result)
    }

    @PostMapping("/v2/create/order")
    fun createOrder(orderCreate: OrderCreate): APIResponse<List<OrderInformation>> {
        // 주문을 생성한다.
        val result: List<OrderInformation> = orderService.createOrder();
        return APIResponse.success(result)
    }
    @PatchMapping("/v2/{orderCode}/confirm")
    fun confirmOrder(): APIResponse<List<OrderInformation>> {
        // 주문을 확정한다.
        val result: List<OrderInformation> = orderService.confirmOrder()
        return APIResponse.success(result)
    }
    @PatchMapping("/v2/{orderCode}/finish")
    fun finishOrder(): APIResponse<List<OrderInformation>> {
        // 주문을 완료한다.
        val result: List<OrderInformation> = orderService.finishOrder()
        return APIResponse.success(result)
    }
    @PatchMapping("/v2/{orderCode}/cancel-request")
    fun cancelRequestOrder(): APIResponse<List<OrderInformation>> {
        // 주문취소 요청을 보낸다.
        val result: List<OrderInformation> = orderService.cancelRequestOrder()
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/{orderCode}/cancel")
    fun cancelOrder(): APIResponse<List<OrderInformation>> {
        // 주문을 취소한다.
        val result: List<OrderInformation> = orderService.cancelOrder()
        return APIResponse.success(result)
    }
}
