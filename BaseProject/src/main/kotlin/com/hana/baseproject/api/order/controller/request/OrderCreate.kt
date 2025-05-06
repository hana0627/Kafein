package com.hana.baseproject.api.order.controller.request

data class OrderCreate(
    val productCode: String,
    val username: String,
) {
}