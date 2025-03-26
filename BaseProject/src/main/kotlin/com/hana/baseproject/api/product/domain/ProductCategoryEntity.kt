package com.hana.baseproject.api.product.domain

import com.hana.baseproject.api.product.domain.constant.ProductLevel
import jakarta.persistence.*


@Entity
@Table(name = "product_type")
class ProductCategoryEntity(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val productLevel: ProductLevel,

    @Column(length = 100, nullable = false)
    val categoryCode: String,

    @Column(length = 100, nullable = false)
    val categoryName: String,

    @Column(length = 100, nullable = true)
    val upCategoryCode: String,

    @OneToMany(mappedBy = "productCategory")
    val productEntity: List<ProductEntity>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    ) {

    companion object {
        fun fixture(
            productLevel: ProductLevel = ProductLevel.MEGA,
            categoryCode: String = "AA01",
            categoryName: String = "커피",
            upCategoryCode: String = "",
            productEntity: List<ProductEntity> = emptyList(),
            id: Long? = null,
        ): ProductCategoryEntity {
            return ProductCategoryEntity(
                productLevel = productLevel,
                categoryCode = categoryCode,
                categoryName = categoryName,
                upCategoryCode = upCategoryCode,
                productEntity = productEntity,
                id = id,
            )
        }
    }

}
