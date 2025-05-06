package com.hana.baseproject.api.order.service.impl

import com.hana.baseproject.api.order.controller.response.OrderInformation
import com.hana.baseproject.api.order.service.OrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderServiceImpl : OrderService{
    override fun getOrder(orderCode: String): List<OrderInformation> {
        // 주문정보를 불러온다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun createOrder(): List<OrderInformation> {
        // 주문을 생성한다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun confirmOrder(): List<OrderInformation> {
        // 주문확정 상태로 변경한다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun finishOrder(): List<OrderInformation> {
        // 주문완료 상태로 변경한다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun cancelRequestOrder(): List<OrderInformation> {
        // 주문취소요청 상태로 변경한다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun cancelOrder(): List<OrderInformation> {
        // 주문취소 상태로 변경한다
        TODO("Not yet implemented")
    }
}
