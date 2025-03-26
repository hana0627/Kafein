package com.hana.baseproject.api.product.domain

import com.hana.baseproject.api.product.domain.constant.ProductCategory
import jakarta.persistence.*


@Entity
@Table(name = "product_type")
class ProductTypeEntity (

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val productCategory: ProductCategory,

    @Column(length = 100 ,nullable = false)
    val categoryCode: String,

    @Column(length = 100 ,nullable = false)
    val categoryName: String,

    @Column(length = 100 ,nullable = true)
    val upCategoryCode: String,

    @OneToMany(mappedBy = "productTypeEntity")
    val productEntity: List<ProductEntity>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

){


}