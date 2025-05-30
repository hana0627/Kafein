package com.hana.baseproject.api.order.domain

import com.hana.baseproject.api.order.domain.constant.OrderStatus
import com.hana.baseproject.api.product.domain.ProductEntity
import com.hana.baseproject.api.user.domain.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders", indexes = arrayOf(Index(name = "idx_user_account", columnList = "order_code")))
class OrderEntity (

    @Column(length = 100, updatable = false, nullable = false)
    val orderCode: String,

    val unitPrice: Int,

    val totalPrice: Int,

    val orderQuantity: Int,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    val orderState: OrderStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: ProductEntity,

    var deleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,

    @Id
    var id: Long?
){
}