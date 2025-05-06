package com.hana.baseproject.api.order.service

import com.hana.baseproject.api.order.controller.response.OrderInformation

interface OrderService {
    fun getOrder(orderCode: String): List<OrderInformation>
    fun createOrder(): List<OrderInformation>
    fun confirmOrder(): List<OrderInformation>
    fun finishOrder(): List<OrderInformation>
    fun cancelRequestOrder(): List<OrderInformation>
    fun cancelOrder(): List<OrderInformation>
}