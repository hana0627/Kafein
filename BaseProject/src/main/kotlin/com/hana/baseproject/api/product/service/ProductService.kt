package com.hana.baseproject.api.product.service

import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.request.ProductCreate
import com.hana.baseproject.api.product.controller.request.ProductUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation

interface ProductService {
    fun getProductCategories(): List<ProductCategoryInformation>
    fun createProductCategory(productCategoryCreate: ProductCategoryCreate): ProductCategoryInformation
    fun updateProductCategory(productCategoryUpdate: ProductCategoryUpdate): ProductCategoryInformation
    fun deleteProductCategory(productCategoryId: Long): ProductCategoryInformation
    fun getProduct(productCode: String): ProductInformation
    fun getProductsByCompanyCode(companyCode: String): List<ProductInformation>
    fun createProduct(productCreate: ProductCreate): ProductInformation
    fun updateProduct(productUpdate: ProductUpdate): ProductInformation
    fun deleteProduct(productCode: String): ProductInformation
}
