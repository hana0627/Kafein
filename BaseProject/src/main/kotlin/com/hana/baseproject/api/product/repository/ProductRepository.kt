package com.hana.baseproject.api.product.repository

import com.hana.baseproject.api.product.domain.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ProductRepository : JpaRepository<ProductEntity, Long>{
    fun findByProductCode(productCode: String): ProductEntity?

    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long
}