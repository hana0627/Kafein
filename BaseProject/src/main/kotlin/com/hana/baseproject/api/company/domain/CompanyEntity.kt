package com.hana.baseproject.api.company.domain

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
}