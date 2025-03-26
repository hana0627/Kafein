package com.hana.baseproject.api.company.domain

import com.hana.baseproject.api.company.controller.request.CompanyCreate
import com.hana.baseproject.api.product.domain.ProductEntity
import com.hana.baseproject.api.user.domain.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "company", indexes = arrayOf(Index(name = "idx_company", columnList = "companyCode")))
data class CompanyEntity (
    @Column(length = 255, nullable = true, unique = true)
    val companyCode: String,

    @Column(length = 255, nullable = true)
    var companyName: String,
    @OneToMany(mappedBy = "company")
    val userEntity: List<UserEntity>,

    @OneToMany(mappedBy = "companyEntity")
    val productEntity: List<ProductEntity>,

    var deleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,


    ){
    constructor(companyCode: String, companyName: String) : this(
        companyCode = companyCode,
        companyName = companyName,
        userEntity = emptyList(),
        productEntity = emptyList(),
        deleted = false,
        deletedDate = null,
        null
    )

    companion object {
        fun fixture(
            companyCode: String = "A0000001",
            companyName: String = "하나다방",
            userEntity: List<UserEntity> = mutableListOf(),
            productEntity: List<ProductEntity> = mutableListOf(),
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
            id: Long? = null
        ) : CompanyEntity {
            return CompanyEntity(
                companyCode = companyCode,
                companyName = companyName,
                userEntity = userEntity,
                productEntity = productEntity,
                deleted = deleted,
                deletedDate = deletedDate,
                id = id
            )
        }
    }

    fun updateCompanyName(companyName: String): CompanyEntity {
        this.companyName = companyName
        return this
    }

    fun delete(): CompanyEntity {
        this.deleted = true
        this.deletedDate = LocalDateTime.now()
        return this
    }

}
