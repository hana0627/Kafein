package com.hana.baseproject.api.product.service.impl

import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.request.ProductCreate
import com.hana.baseproject.api.product.controller.request.ProductUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation
import com.hana.baseproject.api.product.service.ProductService
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl : ProductService {
    override fun getProductCategories(): List<ProductCategoryInformation> {
        TODO("Not yet implemented")
    }

    override fun createProductCategory(productCategoryCreate: ProductCategoryCreate): ProductCategoryInformation {
        TODO("Not yet implemented")
    }

    override fun updateProductCategory(productCategoryUpdate: ProductCategoryUpdate): ProductCategoryInformation {
        TODO("Not yet implemented")
    }

    override fun deleteProductCategory(productCategoryId: Long): ProductCategoryInformation {
        TODO("Not yet implemented")
    }

    override fun getProduct(productCode: String): ProductInformation {
        TODO("Not yet implemented")
    }

    override fun getProductsByCompanyCode(companyCode: String): List<ProductInformation> {
        TODO("Not yet implemented")
    }

    override fun createProduct(productCreate: ProductCreate): ProductInformation {
        TODO("Not yet implemented")
    }

    override fun updateProduct(productUpdate: ProductUpdate): ProductInformation {
        TODO("Not yet implemented")
    }

    override fun deleteProduct(productCode: String): ProductInformation {
        TODO("Not yet implemented")
    }
}
