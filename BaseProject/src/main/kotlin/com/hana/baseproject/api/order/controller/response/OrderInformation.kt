package com.hana.baseproject.api.order.controller.response

import com.hana.baseproject.api.order.domain.constant.OrderStatus

data class OrderInformation (
    val orderCode: String,
    val unitPrice: Int,
    val totalPrice: Int,
    val orderQuantity: String,
    val orderState: OrderStatus,
    val username: String,
    val name: String,
    val productName: String,
    val price: Int,
    val categoryCode: String,
    val categoryName: String,
){

}
