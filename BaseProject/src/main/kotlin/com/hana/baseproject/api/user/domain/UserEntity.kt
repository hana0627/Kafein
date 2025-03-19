package com.hana.baseproject.api.user.domain

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.order.domain.OrderEntity
import com.hana.baseproject.api.user.domain.constant.Gender
import jakarta.persistence.*

@Entity
@Table(name = "user_account", indexes = arrayOf(Index(name = "idx_user_account", columnList = "username")))
class UserEntity (

    @Column(length = 100, updatable = false, nullable = false)
    val username: String,

    @Column(length = 100, nullable = false)
    val name: String,

    @Column(length = 255, nullable = false)
    val password: String,

    @Column(length = 30, nullable = false)
    val phoneNumber: String,

    @Column(length = 1000, nullable = true)
    val description: String,

    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Column(nullable = false)
    val point: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,


    @OneToMany(mappedBy = "user")
    val orders: List<OrderEntity>,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    val companyEntity: CompanyEntity,
){


}