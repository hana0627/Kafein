package com.hana.baseproject.api.product.domain

import com.hana.baseproject.api.product.domain.constant.ProductLevel
import jakarta.persistence.*
import java.time.Clock
import java.time.LocalDateTime


@Entity
@Table(name = "product_type", indexes = arrayOf(Index(name = "idx_product_category", columnList = "categoryCode")))
data class ProductCategoryEntity(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val productLevel: ProductLevel,

    @Column(length = 100, nullable = false, unique = true)
    val categoryCode: String,

    @Column(length = 100, nullable = false)
    val categoryName: String,

    @Column(length = 100, nullable = true)
    val upCategoryCode: String,


    val deleted: Boolean = false,

    @Column(nullable = true)
    val deletedDate: LocalDateTime? = null,

    @OneToMany(mappedBy = "productCategory")
    val productEntity: List<ProductEntity>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    ) {

    companion object {
        fun fixture(
            productLevel: ProductLevel = ProductLevel.MEGA,
            categoryCode: String = "AA01",
            categoryName: String = "커피",
            upCategoryCode: String = "",
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
            productEntity: List<ProductEntity> = emptyList(),
            id: Long? = null,
        ): ProductCategoryEntity {
            return ProductCategoryEntity(
                productLevel = productLevel,
                categoryCode = categoryCode,
                categoryName = categoryName,
                upCategoryCode = upCategoryCode,
                deleted = deleted,
                deletedDate = deletedDate,
                productEntity = productEntity,
                id = id,
            )
        }
    }

    fun updateCategoryName(categoryName: String): ProductCategoryEntity = copy(
        categoryName = categoryName,
    )

//    fun delete(clock: Clock): ProductCategoryEntity = copy(
//        deleted = true,
//        deletedDate = LocalDateTime.now(clock)
//    )

}
