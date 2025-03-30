package com.hana.baseproject.api.product.controller.request

import com.hana.baseproject.api.product.domain.constant.ProductLevel

data class ProductCategoryCreate(
    val productLevel: ProductLevel,
    val upCategoryCode: String,
    val categoryName: String,
) {

    companion object {
        fun fixture(
            productLevel: ProductLevel = ProductLevel.MEGA,
            upCategoryCode: String = "",
            categoryName: String = "커피",
        ): ProductCategoryCreate {
            return ProductCategoryCreate(
                productLevel = productLevel,
                upCategoryCode = upCategoryCode,
                categoryName = categoryName,
            )
        }
    }
}
