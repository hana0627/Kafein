package com.hana.baseproject.api.product.controller

import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.request.ProductCreate
import com.hana.baseproject.api.product.controller.request.ProductUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation
import com.hana.baseproject.api.product.service.ProductService
import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class ProductController (
    private val productService: ProductService
){

    // ====== 상품타입 관련 - start ======
    @GetMapping("/v1/productCategories")
    fun showProductCategory(): APIResponse<List<ProductCategoryInformation>> {
        // 모든 상품타입을 조회한다.
        val result: List<ProductCategoryInformation> = productService.getProductCategories()
        return APIResponse.success(result)
    }

    @PostMapping("/v2/productCategory")
    fun createProductCategory(
        @RequestBody productCategoryCreate: ProductCategoryCreate
    ): APIResponse<ProductCategoryInformation> {
        // 상품타입을 생성한다.
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/productCategory")
    fun updateProductCategory(
        @RequestBody productCategoryUpdate : ProductCategoryUpdate,
    ): APIResponse<ProductCategoryInformation> {
        // 상품타입을 수정한다.
        val result: ProductCategoryInformation = productService.updateProductCategory(productCategoryUpdate)
        return APIResponse.success(result)
    }

    @DeleteMapping("/v2/{categoryCode}/productCategory")
    fun deleteProductCategory(@PathVariable("categoryCode") categoryCode: String): APIResponse<Int> {
        // 상품타입을 삭제한다.
        val result: Int = productService.deleteProductCategory(categoryCode)
        return APIResponse.success(result)
    }
    // ====== 상품타입 관련 - end ======


    @GetMapping("/v1/{productCode}/product")
    fun showProduct(@PathVariable("productCode") productCode: String): APIResponse<ProductInformation> {
        // 상품 한건을 조회한다.
        val result: ProductInformation = productService.getProduct(productCode)
        return APIResponse.success(result)
    }

    @GetMapping("/v1/{companyCode}/products")
    fun showProducts(@PathVariable("companyCode") companyCode: String): APIResponse<List<ProductInformation>> {
        // 회사별 상품을 조회한다.
        val result: List<ProductInformation> = productService.getProductsByCompanyCode(companyCode)
        return APIResponse.success(result)
    }


    @PostMapping("/v2/product")
    fun createProduct(
        @RequestBody productCreate: ProductCreate
    ): APIResponse<ProductInformation> {
        // 상품을 등록한다.
        val result: ProductInformation = productService.createProduct(productCreate)
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/product")
    fun updateProduct(
        @RequestBody productUpdate: ProductUpdate,
    ): APIResponse<ProductInformation> {
        // 상품을 정보를 수정한다
        val result: ProductInformation = productService.updateProduct(productUpdate)
        return APIResponse.success(result)
    }

    @DeleteMapping("/v2/{productCode}/product")
    fun deleteProduct(@PathVariable("productCode") productCode: String): APIResponse<ProductInformation> {
        // 상품을 삭제한다.
        val result: ProductInformation = productService.deleteProduct(productCode)
        return APIResponse.success(result)
    }
}
