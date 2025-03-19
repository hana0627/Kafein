package com.hana.baseproject.api.product.controller

import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class ProductController {


    @GetMapping("/v1/productType")
    fun showProductType(): APIResponse<String> {
        // 모든 상품타입을 조회한다.
        return APIResponse.success("");

    }
    @PostMapping("/v2/productType")
    fun createProductType(): APIResponse<String> {
        // 상품타입을 수정한다.
        return APIResponse.success("");

    }
    @DeleteMapping("/v2/{productTypeId}/productType")
    fun deleteProductType(@PathVariable("productTypeId") id: Long): APIResponse<String> {
        // 상품타입을 삭제한다.
        return APIResponse.success("");
    }


    @GetMapping("/v1/{productCode}/product")
    fun showProduct(@PathVariable("productCode") productCode: String): APIResponse<String> {
        // 상품 한건을 조회한다.
        return APIResponse.success("");
    }

    @GetMapping("/v1/{companyCode}/products")
    fun showProducts(@PathVariable("companyCode") companyCode: String): APIResponse<String> {
        // 회사별 상품을 조회한다.
        return APIResponse.success("");
    }


    @PostMapping("/v2/product")
    fun createProduct(): APIResponse<String> {
        // 상품을 등록한다.
        return APIResponse.success("");
    }

    @PatchMapping("/v2/product")
    fun updateProduct(): APIResponse<String> {
        // 상품을 정보를 수정한다
        return APIResponse.success("");
    }

    @DeleteMapping("/v2/{productCode}/product")
    fun deleteUser(@PathVariable("productCode") productCode: String): APIResponse<String> {
        // 상품을 삭제한다.
        return APIResponse.success("");
    }
}
