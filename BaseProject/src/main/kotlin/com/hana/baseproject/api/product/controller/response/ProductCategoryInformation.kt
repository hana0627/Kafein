package com.hana.baseproject.api.product.controller.response

import com.hana.baseproject.api.product.domain.constant.ProductLevel
import java.time.LocalDateTime

data class ProductCategoryInformation(
    val productLevel: ProductLevel,
    val categoryCode: String,
    val upCategoryCode: String,
    val categoryName: String,
    val deleted: Boolean,
    val deletedDate: LocalDateTime?
) {


    companion object {
        fun fixture(
            productLevel: ProductLevel = ProductLevel.MEGA,
            categoryCode: String = "AA01",
            upCategoryCode: String = "",
            categoryName: String = "커피",
            deleted: Boolean = false,
            deletedDate: LocalDateTime? = null,
        ): ProductCategoryInformation {
            return ProductCategoryInformation(
                productLevel = productLevel,
                categoryCode = categoryCode,
                upCategoryCode = upCategoryCode,
                categoryName = categoryName,
                deleted = deleted,
                deletedDate = deletedDate,
            )
        }
    }
}
