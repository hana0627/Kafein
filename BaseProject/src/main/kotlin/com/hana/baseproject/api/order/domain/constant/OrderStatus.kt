package com.hana.baseproject.api.order.domain.constant

enum class OrderStatus (
    val description: String
) {
    ORDER_REQUEST("주문요청"),
    ORDER("주문완료"),
    READY("준비중"),
    FINISH("완료")
}