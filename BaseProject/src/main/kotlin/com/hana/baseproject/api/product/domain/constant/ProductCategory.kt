package com.hana.baseproject.api.product.domain.constant

enum class ProductCategory (
    val description: String
) {
    MEGA("대분류"),
    MAJOR("중분류"),
    SUB("소분류"),
}
