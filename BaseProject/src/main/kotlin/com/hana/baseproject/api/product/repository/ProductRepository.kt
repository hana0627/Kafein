package com.hana.baseproject.api.product.repository

import com.hana.baseproject.api.product.domain.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Long>{
}