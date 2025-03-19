package com.hana.baseproject.api.company.domain

import com.hana.baseproject.api.user.domain.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "company", indexes = arrayOf(Index(name = "idx_company", columnList = "companyCode")))
class CompanyEntity (
    @Column(length = 255, nullable = true)
    val companyCode: String,

    @Column(length = 255, nullable = true)
    val companyName: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @OneToMany(mappedBy = "companyEntity")
    val userEntity: List<UserEntity>
){
}