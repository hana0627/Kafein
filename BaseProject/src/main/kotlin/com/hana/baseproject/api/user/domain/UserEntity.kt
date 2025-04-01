package com.hana.baseproject.api.user.domain

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.order.domain.OrderEntity
import com.hana.baseproject.api.user.domain.constant.Gender
import com.hana.baseproject.api.user.domain.constant.UserType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_account", indexes = arrayOf(Index(name = "idx_user_account", columnList = "username")))
class UserEntity(

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

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    val userType: UserType,

    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Column(nullable = false)
    val point: Int,

    var deleted: Boolean = false,

    @Column(nullable = true)
    var deletedDate: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,


    @OneToMany(mappedBy = "user")
    val orders: List<OrderEntity>,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    val company: CompanyEntity?,
) {


    companion object {
        fun fixture(
            username: String = "hanana",
            name: String = "박하나",
            password: String = "123456",
            description: String = "하나다방 사장님",
            phoneNumber: String = "010-1234-5678",
            userType: UserType = UserType.CUSTOMER,
            gender: Gender = Gender.F,
            point: Int = 0,
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
            id: Long? = null,
            orders: List<OrderEntity> = listOf(),
            company: CompanyEntity? = null,
        ): UserEntity {
            return UserEntity(
                username = username,
                name = name,
                password = password,
                phoneNumber = phoneNumber,
                description = description,
                userType= userType,
                gender = gender,
                point = point,
                deleted = deleted,
                deletedDate = deletedDate,
                id = id,
                orders = orders,
                company = CompanyEntity.fixture(
                    companyCode = "A0000001",
                    companyName = "하나다방",
                ),
            )
        }
    }

}
