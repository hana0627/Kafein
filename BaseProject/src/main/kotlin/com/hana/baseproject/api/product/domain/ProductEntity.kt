package com.hana.baseproject.api.product.domain

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.order.domain.OrderEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime


@Entity
@Table(name = "product", indexes = arrayOf(Index(name = "idx_product", columnList = "productCode")))
class ProductEntity(

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
    val productCategory: ProductCategoryEntity,

    @OneToMany(mappedBy = "product")
    val orders: List<OrderEntity>,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    val companyEntity: CompanyEntity,

    val deleted: Boolean = false,

    @Column(nullable = true)
    val deletedDate: LocalDateTime? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    ) {

    companion object {
        fun fixture(
            productCode: String = "2025041600000001",
            productName: String = "아메리카노",
            price: Int = 3000,
            stock: Int = 9999,
            productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(),
            orders: List<OrderEntity> = emptyList(),
            companyEntity: CompanyEntity = CompanyEntity.fixture(),
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
            id: Long? = null,
        ): ProductEntity {
            return ProductEntity(
                productCode = productCode,
                productName = productName,
                price = price,
                stock = stock,
                productCategory = productCategory,
                orders = orders,
                companyEntity = companyEntity,
                deleted = deleted,
                deletedDate = deletedDate,
                id = id,
            )
        }
    }
}
