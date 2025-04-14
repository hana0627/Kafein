package com.hana.baseproject.api.product.repository

import com.hana.baseproject.api.product.domain.ProductCategoryEntity
import com.hana.baseproject.api.product.domain.constant.ProductLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ProductCategoryRepository : JpaRepository<ProductCategoryEntity, Long>{
    fun findByCategoryCode(categoryCode: String): ProductCategoryEntity?

    @Query("SELECT MAX(p.categoryCode) FROM ProductCategoryEntity p WHERE p.productLevel = :productLevel")
    fun getMaxCategoryCodeByProductLevel(productLevel: ProductLevel): String?

    @Query("SELECT MAX(p.categoryCode) FROM ProductCategoryEntity p WHERE p.upCategoryCode = :upCategoryCode")
    fun getMaxCategoryCodeByUpCategoryCode(upCategoryCode: String): String?

    @Modifying
    @Query("UPDATE ProductCategoryEntity p SET p.deleted = true , p.deletedDate = :currentTime WHERE p.categoryCode LIKE :prefixWildcatd")
    fun deleteByCategoryCodeStartingWith(prefixWildcard: String, currentTime: LocalDateTime): Int

}