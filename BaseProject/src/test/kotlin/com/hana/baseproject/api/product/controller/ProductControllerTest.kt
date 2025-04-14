package com.hana.baseproject.api.product.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.request.ProductCreate
import com.hana.baseproject.api.product.controller.request.ProductUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation
import com.hana.baseproject.api.product.domain.constant.ProductLevel
import com.hana.baseproject.api.product.service.impl.ProductServiceImpl
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(controllers = [ProductController::class])
class ProductControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var om: ObjectMapper

    @MockitoBean
    private lateinit var productService: ProductServiceImpl

    // ====== 상품타입 관련 - start ======

    @Test
    fun 모든_상품타입을_조회한다() {
        //given
        val productCategoryInformation: ProductCategoryInformation = ProductCategoryInformation.fixture()
        val productCategoryInformation2: ProductCategoryInformation = ProductCategoryInformation.fixture(
            upCategoryCode = productCategoryInformation.categoryCode,
            categoryCode = productCategoryInformation.categoryCode + "01",
            categoryName = "아이스아메리카노"
        )

        val productCategoryInformations = listOf(productCategoryInformation, productCategoryInformation2)

        given(productService.getProductCategories()).willReturn(productCategoryInformations)

        //when & then
        mvc.perform(get("/v1/productCategories"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.size()").value(productCategoryInformations.size))
            .andExpect(jsonPath("$.result[0].productLevel").value(productCategoryInformations[0].productLevel.name))
            .andExpect(jsonPath("$.result[0].categoryCode").value(productCategoryInformations[0].categoryCode))
            .andExpect(jsonPath("$.result[0].upCategoryCode").value(productCategoryInformations[0].upCategoryCode))
            .andExpect(jsonPath("$.result[0].categoryName").value(productCategoryInformations[0].categoryName))

            .andExpect(jsonPath("$.result[1].productLevel").value(productCategoryInformations[1].productLevel.name))
            .andExpect(jsonPath("$.result[1].categoryCode").value(productCategoryInformations[1].categoryCode))
            .andExpect(jsonPath("$.result[1].upCategoryCode").value(productCategoryInformations[1].upCategoryCode))
            .andExpect(jsonPath("$.result[1].categoryName").value(productCategoryInformations[1].categoryName))

            .andDo(print())

        then(productService).should().getProductCategories()
    }

    @Test
    fun 상품_타입_생성에_성공한다() {
        //given
        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture()
        val productCategoryInformation: ProductCategoryInformation = ProductCategoryInformation.fixture(
            productLevel = productCategoryCreate.productLevel,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName
        )

        given(productService.createProductCategory(productCategoryCreate)).willReturn(productCategoryInformation)

        val json = om.writeValueAsString(productCategoryCreate)


        //when & then
        mvc.perform(
            post("/v2/productCategory")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.result.productLevel").value(productCategoryInformation.productLevel.name))
            .andExpect(jsonPath("$.result.upCategoryCode").value(productCategoryInformation.upCategoryCode))
            .andExpect(jsonPath("$.result.categoryName").value(productCategoryInformation.categoryName))

            .andDo(print())

        then(productService).should().createProductCategory(productCategoryCreate)
    }

    @Test
    fun 없는_상위타입의_하위상품타입을_입력하면_예외가_발생한다() {
        //given
        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.SUB,
            upCategoryCode = "WrongUpCategoryCode",
            categoryName = "잘못된 상위카테고리를 입력"
        )

        given(productService.createProductCategory(productCategoryCreate)).willThrow(
            ApplicationException(ErrorCode.CATEGORY_NOT_FOUND, "상위 카테고리를 찾을 수 없습니다.")
        )

        val json = om.writeValueAsString(productCategoryCreate)


        //when & then
        mvc.perform(
            post("/v2/productCategory")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value("상위 카테고리를 찾을 수 없습니다."))

            .andDo(print())

        then(productService).should().createProductCategory(productCategoryCreate)
    }

    @Test
    fun 상품타입_수정에_성공한다() {
        //given
        val productCategoryUpdate: ProductCategoryUpdate = ProductCategoryUpdate.fixture()
        val productCategoryInformation: ProductCategoryInformation = ProductCategoryInformation.fixture(
            productLevel = productCategoryUpdate.productLevel,
            categoryCode = productCategoryUpdate.categoryCode,
            upCategoryCode = productCategoryUpdate.upCategoryCode,
            categoryName = productCategoryUpdate.categoryName
        )

        given(productService.updateProductCategory(productCategoryUpdate)).willReturn(productCategoryInformation)

        val json = om.writeValueAsString(productCategoryUpdate)

        //when & then
        mvc.perform(
            patch("/v2/productCategory")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.result.productLevel").value(productCategoryInformation.productLevel.name))
            .andExpect(jsonPath("$.result.categoryCode").value(productCategoryInformation.categoryCode))
            .andExpect(jsonPath("$.result.upCategoryCode").value(productCategoryInformation.upCategoryCode))
            .andExpect(jsonPath("$.result.categoryName").value(productCategoryInformation.categoryName))

            .andDo(print())

        then(productService).should().updateProductCategory(productCategoryUpdate)
    }

    @Test
    fun 없는_상품타입에_대해서_수정시도시_예외가_발생한다() {
        //given
        val productCategoryUpdate: ProductCategoryUpdate = ProductCategoryUpdate.fixture()

        given(productService.updateProductCategory(productCategoryUpdate)).willThrow(
            ApplicationException(ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND.message)
        )

        val json = om.writeValueAsString(productCategoryUpdate)


        //when & then
        mvc.perform(
            patch("/v2/productCategory")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.CATEGORY_NOT_FOUND.message))

            .andDo(print())

        then(productService).should().updateProductCategory(productCategoryUpdate)
    }

    @Test
    fun 상품타입_삭제에_성공한다() {
        //given
        val deletedDate: LocalDateTime = LocalDateTime.of(2025,4,1,18,30,30)
        val productCategoryInformation: ProductCategoryInformation = ProductCategoryInformation.fixture(
            deleted = true,
            deletedDate = deletedDate,
        )

        val categoryCode: String = productCategoryInformation.categoryCode

        given(productService.deleteProductCategory(categoryCode)).willReturn(1)

        //when & then
        mvc.perform(
            delete("/v2/{categoryCode}/productCategory", categoryCode)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.result").value(1))
            .andDo(print())

        then(productService).should().deleteProductCategory(categoryCode)
    }

    @Test
    fun 존재하지_않는_상품타입에_대해서_삭제요청시_예외가_발생한다() {
        //given
        val deletedDate = LocalDateTime.now()
        val productCategoryInformation: ProductCategoryInformation = ProductCategoryInformation.fixture(
            deleted = true,
            deletedDate = deletedDate,
        )
        val categoryCode: String = "wrongCategoryCode"

        given(productService.deleteProductCategory(categoryCode)).willThrow(
            ApplicationException(ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(
            delete("/v2/{categoryCode}/productCategory", categoryCode)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.CATEGORY_NOT_FOUND.message))

            .andDo(print())

    }

    // ====== 상품타입 관련 - end ======

    @Test
    fun 상품_한건을_조회한다() {
        //given
        val productCode: String = "PD000001"
        val productInformation: ProductInformation = ProductInformation.fixture()

        given(productService.getProduct(productCode)).willReturn(productInformation)

        //when & then
        mvc.perform(get("/v1/{productCode}/product", productCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.productCode").value(productInformation.productCode))
            .andExpect(jsonPath("$.result.productName").value(productInformation.productName))
            .andExpect(jsonPath("$.result.price").value(productInformation.price))
            .andExpect(jsonPath("$.result.stock").value(productInformation.stock))
            .andExpect(jsonPath("$.result.productLevel").value(productInformation.productLevel.name))
            .andExpect(jsonPath("$.result.categoryCode").value(productInformation.categoryCode))
            .andExpect(jsonPath("$.result.categoryName").value(productInformation.categoryName))
            .andExpect(jsonPath("$.result.companyCode").value(productInformation.companyCode))
            .andExpect(jsonPath("$.result.companyName").value(productInformation.companyName))
            .andExpect(jsonPath("$.result.deleted").value(productInformation.deleted))
            .andExpect(jsonPath("$.result.deletedDate").value(productInformation.deletedDate))
            .andDo(print())

        then(productService).should().getProduct(productCode)

    }


    @Test
    fun 없는_상품코드로_상품조회시_예외가_발생한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val productCode: String = "wrongProductCode"

        given(productService.getProduct(productCode)).willThrow(
            ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND.message)
        )
        //when & then
        mvc.perform(get("/v1/{productCode}/product", productCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.PRODUCT_NOT_FOUND.message))
            .andDo(print())

        then(productService).should().getProduct(productCode)

    }

    @Test
    fun 회사코드로_상품조회가_성공한다() {
        //given
        val company: CompanyEntity = CompanyEntity.fixture()
        val companyCode: String = company.companyCode

        val productInformation1: ProductInformation = ProductInformation.fixture(
            productCode = "PD000001",
            productName = "아이스아메리카노",
            price = 3000,
            stock = 9999,
            productLevel = ProductLevel.SUB,
            categoryCode = "AA010101",
            categoryName = "아이스아메리카노",
            companyCode = companyCode,
            companyName = company.companyName,
            deleted = false,
            deletedDate = null,
        )

        val productInformation2: ProductInformation = ProductInformation.fixture(
            productCode = "PD000002",
            productName = "카페라떼",
            price = 4000,
            stock = 9999,
            productLevel = ProductLevel.SUB,
            categoryCode = "AA010102",
            categoryName = "카페라떼",
            companyCode = companyCode,
            companyName = company.companyName,
            deleted = false,
            deletedDate = null,
        )

        val products: List<ProductInformation> = listOf(productInformation1, productInformation2)

        given(productService.getProductsByCompanyCode(company.companyCode)).willReturn(products)

        //when & then
        mvc.perform(get("/v1/{companyCode}/products", company.companyCode))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.size()").value(products.size))
            .andExpect(jsonPath("$.result[0].productCode").value(products[0].productCode))
            .andExpect(jsonPath("$.result[0].productName").value(products[0].productName))
            .andExpect(jsonPath("$.result[0].price").value(products[0].price))
            .andExpect(jsonPath("$.result[0].stock").value(products[0].stock))
            .andExpect(jsonPath("$.result[0].productLevel").value(products[0].productLevel.name))
            .andExpect(jsonPath("$.result[0].categoryCode").value(products[0].categoryCode))
            .andExpect(jsonPath("$.result[0].categoryName").value(products[0].categoryName))
            .andExpect(jsonPath("$.result[0].companyCode").value(products[0].companyCode))
            .andExpect(jsonPath("$.result[0].companyName").value(products[0].companyName))
            .andExpect(jsonPath("$.result[0].deleted").value(products[0].deleted))
            .andExpect(jsonPath("$.result[0].deletedDate").value(products[0].deletedDate))

            .andExpect(jsonPath("$.result[1].productCode").value(products[1].productCode))
            .andExpect(jsonPath("$.result[1].productName").value(products[1].productName))
            .andExpect(jsonPath("$.result[1].price").value(products[1].price))
            .andExpect(jsonPath("$.result[1].stock").value(products[1].stock))
            .andExpect(jsonPath("$.result[1].productLevel").value(products[1].productLevel.name))
            .andExpect(jsonPath("$.result[1].categoryCode").value(products[1].categoryCode))
            .andExpect(jsonPath("$.result[1].categoryName").value(products[1].categoryName))
            .andExpect(jsonPath("$.result[1].companyCode").value(products[1].companyCode))
            .andExpect(jsonPath("$.result[1].companyName").value(products[1].companyName))
            .andExpect(jsonPath("$.result[1].deleted").value(products[1].deleted))
            .andExpect(jsonPath("$.result[1].deletedDate").value(products[1].deletedDate))

            .andDo(print())

        then(productService).should().getProductsByCompanyCode(companyCode)
    }

    @Test
    fun 상품등록에_성공한다() {
        //given
        val productCreate: ProductCreate = ProductCreate.fixture()
        val productInformation: ProductInformation = ProductInformation.fixture(
            productName = productCreate.productName,
            price = productCreate.price,
            stock = productCreate.stock,
            productLevel = productCreate.productLevel,
            categoryCode = productCreate.categoryCode,
            companyCode = productCreate.companyCode,
        )


        val json = om.writeValueAsString(productCreate)


        given(productService.createProduct(productCreate)).willReturn(productInformation)

        //when & then

        mvc.perform(post("/v2/product")
            .contentType(APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.productCode").value(productInformation.productCode))
            .andExpect(jsonPath("$.result.productName").value(productInformation.productName))
            .andExpect(jsonPath("$.result.price").value(productInformation.price))
            .andExpect(jsonPath("$.result.stock").value(productInformation.stock))
            .andExpect(jsonPath("$.result.productLevel").value(productInformation.productLevel.name))
            .andExpect(jsonPath("$.result.categoryCode").value(productInformation.categoryCode))
            .andExpect(jsonPath("$.result.companyCode").value(productInformation.companyCode))
            .andExpect(jsonPath("$.result.deleted").value(productInformation.deleted))
            .andExpect(jsonPath("$.result.deletedDate").value(productInformation.deletedDate))
            .andDo(print())

        then(productService).should().createProduct(productCreate)
    }

    @Test
    fun 없는_회사코드로_상품_등록시_예외가_발생한다() {
        //given
        val companyCode: String = "wrongCompanyCode"
        val productCreate: ProductCreate = ProductCreate.fixture(companyCode = companyCode)

        val json = om.writeValueAsString(productCreate)


        given(productService.createProduct(productCreate)).willThrow(
            ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(post("/v2/product")
            .contentType(APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.COMPANY_NOT_FOUND.message))
            .andDo(print())

        then(productService).should().createProduct(productCreate)
    }

    @Test
    fun 없는_상품타입으로_상품_등록시_예외가_발생한다() {
        //given
        val categoryCode: String = "wrongCategoryCode"
        val productCreate: ProductCreate = ProductCreate.fixture(categoryCode = categoryCode)

        val json = om.writeValueAsString(productCreate)


        given(productService.createProduct(productCreate)).willThrow(
            ApplicationException(ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(post("/v2/product")
            .contentType(APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.CATEGORY_NOT_FOUND.message))
            .andDo(print())

        then(productService).should().createProduct(productCreate)

    }

    @Test
    fun 상품수정에_성공한다() {
        //given
        val productUpdate: ProductUpdate = ProductUpdate.fixture()
        val productInformation: ProductInformation = ProductInformation.fixture(
            productCode = productUpdate.productCode,
            productName = productUpdate.productName,
            price = productUpdate.price,
            stock = productUpdate.stock,
        )


        val json = om.writeValueAsString(productUpdate)


        given(productService.updateProduct(productUpdate)).willReturn(productInformation)

        //when & then

        mvc.perform(
            patch("/v2/product")
            .contentType(APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.OK.name))
            .andExpect(jsonPath("$.result.productCode").value(productInformation.productCode))
            .andExpect(jsonPath("$.result.productName").value(productInformation.productName))
            .andExpect(jsonPath("$.result.price").value(productInformation.price))
            .andExpect(jsonPath("$.result.stock").value(productInformation.stock))
            .andDo(print())

        then(productService).should().updateProduct(productUpdate)

    }

    @Test
    fun 없는_상품코드로_상품수정시_예외가_발생한다() {
        //given
        val productCode: String = "wrongProductCode"
        val productUpdate: ProductUpdate = ProductUpdate.fixture(
            productCode = productCode,
        )

        val json = om.writeValueAsString(productUpdate)

        given(productService.updateProduct(productUpdate)).willThrow(
            ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND.message)
        )
        //when & then
        mvc.perform(patch("/v2/product")
            .contentType(APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.PRODUCT_NOT_FOUND.message))
            .andDo(print())

        then(productService).should().updateProduct(productUpdate)

    }

    @Test
    fun 상품삭제에_성공한다() {
        //given
        val deletedDate: LocalDateTime = LocalDateTime.of(2025,4,1,18,30,30)
        val productCode = "PD000001"
        val productInformation: ProductInformation = ProductInformation.fixture(
            productCode = productCode,
            deleted = true,
            deletedDate = deletedDate,
        )

        given(productService.deleteProduct(productCode)).willReturn(productInformation)

        //when & then
        mvc.perform(
            delete("/v2/{productCode}/product", productCode)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.result.productCode").value(productInformation.productCode))
            .andExpect(jsonPath("$.result.productName").value(productInformation.productName))
            .andExpect(jsonPath("$.result.deleted").value(productInformation.deleted))
            .andExpect(jsonPath("$.result.deletedDate").value(productInformation.deletedDate.toString()))

            .andDo(print())

        then(productService).should().deleteProduct(productCode)

    }

    @Test
    fun 없는_상품_삭제시_예외가_발생한다() { //given
        val productCode = "wrongProductCode"

        given(productService.deleteProduct(productCode)).willThrow(
            ApplicationException(ErrorCode.PRODUCT_NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND.message)
        )

        //when & then
        mvc.perform(
            delete("/v2/{productCode}/product", productCode)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value(HttpStatus.NOT_FOUND.name))
            .andExpect(jsonPath("$.result").value(ErrorCode.PRODUCT_NOT_FOUND.message))

            .andDo(print())

        then(productService).should().deleteProduct(productCode)
    }

}
