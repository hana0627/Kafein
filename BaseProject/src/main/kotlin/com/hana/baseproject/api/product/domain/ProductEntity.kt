package com.hana.baseproject.api.product.domain

import com.hana.baseproject.api.order.domain.OrderEntity
import com.hana.baseproject.api.product.domain.constant.ProductCategory
import jakarta.persistence.*


@Entity
@Table(name = "product", indexes = arrayOf(Index(name = "idx_product", columnList = "productCode")))
class ProductEntity (

    @Column(length = 30, updatable = false, nullable = false)
    val productCode: String,

    @Column(length = 255, nullable = false)
    val productName: String,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    val stock: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    val productTypeEntity: ProductTypeEntity,

    @OneToMany(mappedBy = "product")
    val orders: List<OrderEntity>,


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    ){


}