package com.hana.baseproject.api.order.repository

import com.hana.baseproject.api.order.domain.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {
}