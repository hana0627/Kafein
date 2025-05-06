package com.hana.baseproject.api.product.service.impl

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation
import com.hana.baseproject.api.product.domain.ProductCategoryEntity
import com.hana.baseproject.api.product.domain.ProductEntity
import com.hana.baseproject.api.product.domain.constant.ProductLevel
import com.hana.baseproject.api.product.repository.ProductCategoryRepository
import com.hana.baseproject.api.product.repository.ProductRepository
import com.hana.baseproject.core.config.ClockConfig
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@ExtendWith(MockitoExtension::class)
class ProductServiceImplTest(
    @Mock private val productCategoryRepository: ProductCategoryRepository,
    @Mock private val productRepository: ProductRepository,
    @Mock private val companyRepository: CompanyRepository,
    @Mock private val clockConfig: ClockConfig,
) {

    @InjectMocks
    private val productService: ProductServiceImpl = ProductServiceImpl(productCategoryRepository, productRepository, companyRepository, clockConfig)

    @Test
    fun 모든_상품타입_조회에_성공한다() {
        //given
        val productCategory1: ProductCategoryEntity = ProductCategoryEntity.fixture()
        val productCategory2: ProductCategoryEntity = ProductCategoryEntity.fixture(
            upCategoryCode = productCategory1.categoryCode,
            categoryCode = productCategory1.categoryCode + "01",
            categoryName = "아이스아메리카노"
        )
        val productCategories = listOf(productCategory1, productCategory2)

        given(productCategoryRepository.findAll()).willReturn(productCategories)

        //when
        val result: List<ProductCategoryInformation> = productService.getProductCategories()

        //then
        then(productCategoryRepository).should().findAll()
        assertThat(result.size).isEqualTo(productCategories.size)
        assertThat(result[0].productLevel).isEqualTo(productCategory1.productLevel)
        assertThat(result[0].categoryCode).isEqualTo(productCategory1.categoryCode)
        assertThat(result[0].upCategoryCode).isEqualTo(productCategory1.upCategoryCode)
        assertThat(result[0].categoryName).isEqualTo(productCategory1.categoryName)
        assertThat(result[0].deleted).isEqualTo(productCategory1.deleted)
        assertThat(result[0].deletedDate).isEqualTo(productCategory1.deletedDate)

        assertThat(result[1].productLevel).isEqualTo(productCategory2.productLevel)
        assertThat(result[1].categoryCode).isEqualTo(productCategory2.categoryCode)
        assertThat(result[1].upCategoryCode).isEqualTo(productCategory2.upCategoryCode)
        assertThat(result[1].categoryName).isEqualTo(productCategory2.categoryName)
        assertThat(result[1].deleted).isEqualTo(productCategory2.deleted)
        assertThat(result[1].deletedDate).isEqualTo(productCategory2.deletedDate)

    }

    @Test
    fun 상품타입_대분류_생성에_성공한다_최초생성() {
        //given
        val firstCategoryCode = "AA01"
        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MEGA,
        )


        val productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = firstCategoryCode,
            categoryName = productCategoryCreate.categoryName,
            upCategoryCode = productCategoryCreate.upCategoryCode,
        )

        given(productCategoryRepository.getMaxCategoryCodeByProductLevel(productCategoryCreate.productLevel)).willReturn(null)
        given(productCategoryRepository.save(productCategory)).willReturn(productCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByProductLevel(productCategory.productLevel)
        then(productCategoryRepository).should().save(productCategory)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(productCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(productCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(productCategory.deletedDate)

    }

    @Test
    fun 상품타입_대분류_생성에_성공한다_대분류가_이미_존재하는_경우() {
        //given
        val firstCategoryCode = "AA01"
        val secondCategoryCode = "AA02"

        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MEGA,
            upCategoryCode = "",
            categoryName = "베이커리",
        )


        val productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            categoryCode = firstCategoryCode,
        )

        val savedProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = secondCategoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )


        given(productCategoryRepository.getMaxCategoryCodeByProductLevel(productCategoryCreate.productLevel)).willReturn(productCategory.categoryCode)
        given(productCategoryRepository.save(savedProductCategory)).willReturn(savedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByProductLevel(savedProductCategory.productLevel)
        then(productCategoryRepository).should().save(savedProductCategory)

        assertThat(result.productLevel).isEqualTo(savedProductCategory.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(savedProductCategory.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(savedProductCategory.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory.deletedDate)
    }


    @Test
    fun 상품타입_중분류_생성에_성공한다_최초생성() {
        //given
        val upCategoryCode = "AA01"
        val categoryCode = "AA0101"

        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
        )

        val savedProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = categoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(null)
        given(productCategoryRepository.save(savedProductCategory)).willReturn(savedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)
        then(productCategoryRepository).should().save(savedProductCategory)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory.deletedDate)

    }


    @Test
    fun 상품타입_중분류_생성에_성공한다_중분류가_이미_존재하는_경우() {
        //given
        val upCategoryCode = "AA01"
        val firstCategoryCode = "AA0101"
        val secondCategoryCode = "AA0102"


        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
        )

        val firstProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
            categoryCode = firstCategoryCode,
        )

        val savedProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = secondCategoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(firstProductCategory.categoryCode)
        given(productCategoryRepository.save(savedProductCategory)).willReturn(savedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)
        then(productCategoryRepository).should().save(savedProductCategory)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory.deletedDate)

    }



    @Test
    fun 상품타입_소분류_생성에_성공한다_최초생성() {
        //given
        val upCategoryCode = "AA0101"
        val categoryCode = "AA010101"

        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.SUB,
            upCategoryCode = upCategoryCode,
        )

        val savedProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = categoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(null)
        given(productCategoryRepository.save(savedProductCategory)).willReturn(savedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)
        then(productCategoryRepository).should().save(savedProductCategory)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory.deletedDate)

    }


    @Test
    fun 상품타입_소분류_생성에_성공한다_소분류가_이미_존재하는_경우() {
        //given
        val upCategoryCode = "AA0101"
        val firstCategoryCode = "AA010101"
        val secondCategoryCode = "AA010102"


        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.SUB,
            upCategoryCode = upCategoryCode,
        )

        val firstProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = ProductLevel.SUB,
            upCategoryCode = upCategoryCode,
            categoryCode = firstCategoryCode,
        )

        val savedProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = secondCategoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(firstProductCategory.categoryCode)
        given(productCategoryRepository.save(savedProductCategory)).willReturn(savedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)
        then(productCategoryRepository).should().save(savedProductCategory)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory.deletedDate)

    }

    @Test
    fun 상품타입_생성시점에_카테고리코드가_존재하면_카테고리코드_생성과정을_반복한다() {
        //given
        val upCategoryCode = "AA01"
        val firstCategoryCode = "AA0101"
        val secondCategoryCode = "AA0102"
        val thirdCategoryCode = "AA0103"


        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
        )

        val savedProductCategory1: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = secondCategoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )
        val savedProductCategory2: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = productCategoryCreate.productLevel,
            categoryCode = thirdCategoryCode,
            upCategoryCode = productCategoryCreate.upCategoryCode,
            categoryName = productCategoryCreate.categoryName,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(firstCategoryCode, secondCategoryCode)

        given(productCategoryRepository.save(savedProductCategory1))
            .willThrow(DataIntegrityViolationException("duplicate categoryCode"))
            .willAnswer { invocation -> invocation.getArgument(0) }

        given(productCategoryRepository.save(savedProductCategory2)).willReturn(savedProductCategory2)

        //when
        val result: ProductCategoryInformation = productService.createProductCategory(productCategoryCreate)

        //then
        then(productCategoryRepository).should(times(2)).getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)
        then(productCategoryRepository).should().save(savedProductCategory1)
        then(productCategoryRepository).should().save(savedProductCategory2)

        assertThat(result.productLevel).isEqualTo(productCategoryCreate.productLevel)
        assertThat(result.categoryCode).isEqualTo(savedProductCategory2.categoryCode)
        assertThat(result.upCategoryCode).isEqualTo(productCategoryCreate.upCategoryCode)
        assertThat(result.categoryName).isEqualTo(productCategoryCreate.categoryName)
        assertThat(result.deleted).isEqualTo(savedProductCategory2.deleted)
        assertThat(result.deletedDate).isEqualTo(savedProductCategory2.deletedDate)

    }

    @Test
    fun 카테고리_등록_시도시_100개가_넘으면_예외가_발생한다() {
        //given
        val upCategoryCode = "AA01"
        val firstCategoryCode = "AA0199"

        val productCategoryCreate: ProductCategoryCreate = ProductCategoryCreate.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
        )

        val firstProductCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            productLevel = ProductLevel.MAJOR,
            upCategoryCode = upCategoryCode,
            categoryCode = firstCategoryCode,
        )

        given(productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)).willReturn(firstProductCategory.categoryCode)

        //when
        val result = assertThrows<ApplicationException> { productService.createProductCategory(productCategoryCreate) }

        //then
        then(productCategoryRepository).should().getMaxCategoryCodeByUpCategoryCode(productCategoryCreate.upCategoryCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.CATEGORY_CODE_LIMIT_EXCEEDED)
        assertThat(result.getMessage).isEqualTo(ErrorCode.CATEGORY_CODE_LIMIT_EXCEEDED.message)
    }

    @Test
    fun 카테고리_수정에_성공한다() {
        //given
        val productCategoryUpdate: ProductCategoryUpdate = ProductCategoryUpdate.fixture()
        val productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            id = 1L,
            categoryName = "잘못된 카테고리 이름",
        )
        val updatedProductCategory: ProductCategoryEntity = productCategory.copy(
            categoryName = productCategoryUpdate.categoryName,
        )

        given(productCategoryRepository.findByCategoryCode(productCategoryUpdate.categoryCode)).willReturn(productCategory)
        given(productCategoryRepository.save(updatedProductCategory)).willReturn(updatedProductCategory)

        //when
        val result: ProductCategoryInformation = productService.updateProductCategory(productCategoryUpdate)

        //then
        then(productCategoryRepository).should().findByCategoryCode(productCategoryUpdate.categoryCode)
        then(productCategoryRepository).should().save(updatedProductCategory)

        assertThat(result.categoryName).isEqualTo(productCategoryUpdate.categoryName)

    }

    @Test
    fun 없는_카테고리코드로_카테고리_수정시_예외가_발생한다() {
        //given
        val productCategoryUpdate: ProductCategoryUpdate = ProductCategoryUpdate.fixture(
            categoryCode = "wrongCategoryCode"
        )
        val productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
            id = 1L,
        )

        given(productCategoryRepository.findByCategoryCode(productCategoryUpdate.categoryCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            productService.updateProductCategory(productCategoryUpdate)
        }

        //then
        then(productCategoryRepository).should().findByCategoryCode(productCategoryUpdate.categoryCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.message)
    }

    @Test
    fun 소분류_카테고리_삭제에_성공한다() {
        //given
        val upCategoryCode = "AA0101"
        val categoryCode = "AA010101"
        given(clockConfig.getClock()).willReturn(Clock.fixed(Instant.parse("2025-04-02T12:30:30Z"), ZoneId.systemDefault()))
        val currentTime: LocalDateTime = LocalDateTime.now(clockConfig.getClock())

//        val productCategory: ProductCategoryEntity = ProductCategoryEntity.fixture(
//            productLevel = ProductLevel.SUB,
//            upCategoryCode = upCategoryCode,
//            categoryCode = categoryCode,
//        )

        given(productCategoryRepository.deleteByCategoryCodeStartingWith(categoryCode, currentTime)).willReturn(1)


        //when
        val result: Int = productService.deleteProductCategory(categoryCode)

        //then
        then(productCategoryRepository).should().deleteByCategoryCodeStartingWith(categoryCode, currentTime)

        assertThat(result).isEqualTo(1)
    }

    @Test
    fun 상위카테고리_삭제시_하위_카테고리항목도_함께_삭제된다() {
        //given
        val upCategoryCode = "AA01"
        val categoryCode = "AA0101"
        given(clockConfig.getClock()).willReturn(Clock.fixed(Instant.parse("2025-04-02T12:30:30Z"), ZoneId.systemDefault()))
        val currentTime: LocalDateTime = LocalDateTime.now(clockConfig.getClock())

//        val productCategory1: ProductCategoryEntity = ProductCategoryEntity.fixture(
//            productLevel = ProductLevel.MAJOR,
//            upCategoryCode = upCategoryCode,
//            categoryCode = categoryCode,
//        )

//        val productCategory2: ProductCategoryEntity = ProductCategoryEntity.fixture(
//            productLevel = ProductLevel.SUB,
//            upCategoryCode = categoryCode,
//            categoryCode = categoryCode+"01",
//        )

//        val productCategory3: ProductCategoryEntity = ProductCategoryEntity.fixture(
//            productLevel = ProductLevel.SUB,
//            upCategoryCode = categoryCode,
//            categoryCode = categoryCode+"02",
//        )

//        val productCategory4: ProductCategoryEntity = ProductCategoryEntity.fixture(
//            productLevel = ProductLevel.SUB,
//            upCategoryCode = categoryCode,
//            categoryCode = categoryCode+"03",
//        )


        given(productCategoryRepository.deleteByCategoryCodeStartingWith(categoryCode, currentTime)).willReturn(4)


        //when
        val result: Int = productService.deleteProductCategory(categoryCode)

        //then
        then(productCategoryRepository).should().deleteByCategoryCodeStartingWith(categoryCode, currentTime)

        assertThat(result).isEqualTo(4)
    }
    @Test
    fun 없는_카테고리코드로_삭제시도시_예외가_발생한다() {

        //given
        val categoryCode = "wrongCategoryCode"
        given(clockConfig.getClock()).willReturn(Clock.fixed(Instant.parse("2025-04-02T12:30:30Z"), ZoneId.systemDefault()))

        val currentTime: LocalDateTime = LocalDateTime.now(clockConfig.getClock())
        given(productCategoryRepository.deleteByCategoryCodeStartingWith(categoryCode, currentTime)).willReturn(0)

        //when
        val result = assertThrows<ApplicationException> {
            productService.deleteProductCategory(categoryCode)
        }

        //then
        then(productCategoryRepository).should().deleteByCategoryCodeStartingWith(categoryCode, currentTime)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.message)
    }

    @Test
    fun 상품한건_조회에_성공한다() {
        //given
        val product = ProductEntity.fixture()
        given(productRepository.findByProductCode(product.productCode)).willReturn(product)

        //when
        val result: ProductInformation = productService.getProduct(product.productCode)

        //then
        then(productRepository).should().findByProductCode(product.productCode)

        assertThat(result.productCode).isEqualTo(product.productCode)
        assertThat(result.productName).isEqualTo(product.productName)
        assertThat(result.price).isEqualTo(product.price)
        assertThat(result.stock).isEqualTo(product.stock)
        assertThat(result.productLevel).isEqualTo(product.productCategory.productLevel)
        assertThat(result.categoryCode).isEqualTo(product.productCategory.categoryCode)
        assertThat(result.categoryName).isEqualTo(product.productCategory.categoryName)
        assertThat(result.companyCode).isEqualTo(product.companyEntity.companyCode)
        assertThat(result.companyName).isEqualTo(product.companyEntity.companyName)
        assertThat(result.deleted).isEqualTo(product.deleted)
        assertThat(result.deletedDate).isEqualTo(product.deletedDate)
    }

    @Test
    fun 존재하지_않는_상품코드로_상품조회시_예외가_발생한다() {
        //given
        val wrongProductCode = "wrongProductCode"
        given(productRepository.findByProductCode(wrongProductCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            productService.getProduct(wrongProductCode)
        }

        //then
        then(productRepository).should().findByProductCode(wrongProductCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND.message)
    }

    @Test
    fun 특정회사의_상품조회에_성공한다() {
        //given
        val companyCode = "A0000001"
        val company = CompanyEntity.fixture(
            companyCode = companyCode,
        )
        val company2 = CompanyEntity.fixture(
            companyCode = "A9999999",
        )
        val product1 = ProductEntity.fixture(
            companyEntity = company,
        )
        val product2 = ProductEntity.fixture(
            productCode = "2025041600000002",
            productName = "아이스카페라떼",
            price = 4000,
            companyEntity = company,
        )
        val product3 = ProductEntity.fixture(
            productCode = "2025041600001223",
            productName = "카라멜마끼야또",
            price = 4500,
            companyEntity = company,
        )
        val products: List<ProductEntity> = mutableListOf(product1, product2)

        given(companyRepository.findByCompanyCode(companyCode)).willReturn(company)
        company.productEntity.addAll(products)

        //when
        val result: List<ProductInformation> = productService.getProductsByCompanyCode(companyCode)

        //then
        then(companyRepository).should().findByCompanyCode(companyCode)

        assertThat(result.size).isEqualTo(2)
        assertThat(result[0].productCode).isEqualTo(product1.productCode)
        assertThat(result[0].productName).isEqualTo(product1.productName)
        assertThat(result[0].price).isEqualTo(product1.price)
        assertThat(result[0].stock).isEqualTo(product1.stock)
        assertThat(result[0].productLevel).isEqualTo(product1.productCategory.productLevel)
        assertThat(result[0].categoryCode).isEqualTo(product1.productCategory.categoryCode)
        assertThat(result[1].categoryName).isEqualTo(product2.productCategory.categoryName)
        assertThat(result[1].companyCode).isEqualTo(product2.companyEntity.companyCode)
        assertThat(result[1].companyName).isEqualTo(product2.companyEntity.companyName)
        assertThat(result[1].deleted).isEqualTo(product2.deleted)
        assertThat(result[1].deletedDate).isEqualTo(product2.deletedDate)
    }

    @Test
    fun 특정회사의_상품이_0개일때_빈_리스트를_반환한다() {
        //given
        val companyCode = "A0000001"
        val company = CompanyEntity.fixture(
            companyCode = companyCode,
            productEntity = mutableListOf()
        )

        given(companyRepository.findByCompanyCode(companyCode)).willReturn(company)

        //when
        val result: List<ProductInformation> = productService.getProductsByCompanyCode(companyCode)

        //then
        then(companyRepository).should().findByCompanyCode(companyCode)

        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun 존재하지_않는_회사코드로_회사상품_조회시_예외가_발생한다() {
        //given
        val wrongCompanyCode = "wrongComapnyCode"

        given(companyRepository.findByCompanyCode(wrongCompanyCode)).willReturn(null)

        //when
        val result = assertThrows<ApplicationException> {
            productService.getProductsByCompanyCode(wrongCompanyCode)
        }

        //then
        then(companyRepository).should().findByCompanyCode(wrongCompanyCode)

        assertThat(result.getErrorCode).isEqualTo(ErrorCode.COMPANY_NOT_FOUND)
        assertThat(result.getMessage).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.message)
    }

    @Test
    fun 상품생성에_성공한다() {
        //given

        //when

        //then
    }

    @Test
    fun 잘못된_회사코드로_상품생성시_예외가_발생한다() {
        //given

        //when

        //then
    }

    @Test
    fun 잘못된_카테고리코드로_상품생성시_예외가_발생한다() {
        //given

        //when

        //then
    }

    @Test
    fun 하루에_999999개_이상의_상품등록시_예외가_발생한다() {
        //given

        //when

        //then
    }

    @Test
    fun 처음_등록한_상품은_끝자리가_000001로_끝난다() {
        //given

        //when

        //then
    }

    @Test
    fun 세번째_등록한_상품은_끝자리가_000003로_끝난다() {
        //given

        //when

        //then
    }
}
