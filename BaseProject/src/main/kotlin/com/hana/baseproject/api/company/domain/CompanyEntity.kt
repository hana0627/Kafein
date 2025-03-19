package com.hana.baseproject.api.company.domain

import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.product.domain.ProductEntity
import com.hana.baseproject.api.user.domain.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "company", indexes = arrayOf(Index(name = "idx_company", columnList = "companyCode")))
class CompanyEntity (
    @Column(length = 255, nullable = true)
    val companyCode: String,

    @Column(length = 255, nullable = true)
    val companyName: String,
    @OneToMany(mappedBy = "companyEntity")
    val userEntity: List<UserEntity>,

    @OneToMany(mappedBy = "companyEntity")
    val productEntity: List<ProductEntity>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,


){


    companion object {
        fun fixture(
            companyCode: String = "A0000001",
            companyName: String = "하나다방",
            userEntity: List<UserEntity> = mutableListOf(),
            productEntity: List<ProductEntity> = mutableListOf(),
            id: Long? = null
        ) : CompanyEntity {
            return CompanyEntity(
                companyCode = companyCode,
                companyName = companyName,
                userEntity = userEntity,
                productEntity = productEntity,
                id = id
            )
        }
    }
}
