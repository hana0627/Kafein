package com.hana.baseproject.api.product.service.impl

import com.hana.baseproject.api.company.domain.CompanyEntity
import com.hana.baseproject.api.company.repository.CompanyRepository
import com.hana.baseproject.api.product.controller.request.ProductCategoryCreate
import com.hana.baseproject.api.product.controller.request.ProductCategoryUpdate
import com.hana.baseproject.api.product.controller.request.ProductCreate
import com.hana.baseproject.api.product.controller.request.ProductUpdate
import com.hana.baseproject.api.product.controller.response.ProductCategoryInformation
import com.hana.baseproject.api.product.controller.response.ProductInformation
import com.hana.baseproject.api.product.domain.ProductCategoryEntity
import com.hana.baseproject.api.product.domain.ProductEntity
import com.hana.baseproject.api.product.domain.constant.ProductLevel
import com.hana.baseproject.api.product.repository.ProductCategoryRepository
import com.hana.baseproject.api.product.repository.ProductRepository
import com.hana.baseproject.api.product.service.ProductService
import com.hana.baseproject.core.config.ClockConfig
import com.hana.baseproject.core.exception.ApplicationException
import com.hana.baseproject.core.exception.constant.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.UUID.randomUUID

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productCategoryRepository: ProductCategoryRepository,
    private val productRepository: ProductRepository,
    private val companyRepository: CompanyRepository,
    private val clockConfig: ClockConfig,
) : ProductService {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    // ====== 상품타입 관련 - start ======
    override fun getProductCategories(): List<ProductCategoryInformation> {
        // 모든 상품타입을 조회한다.
        return productCategoryRepository.findAll().map { it: ProductCategoryEntity ->
            ProductCategoryInformation(
                productLevel = it.productLevel,
                categoryCode = it.categoryCode,
                upCategoryCode = it.upCategoryCode,
                categoryName = it.categoryName,
                deleted = it.deleted,
                deletedDate = it.deletedDate,
            )
        }
    }

    @Transactional
    override fun createProductCategory(productCategoryCreate: ProductCategoryCreate): ProductCategoryInformation {
        // 상품타입을 생성한다.
        while (true) {
            val categoryCode: String = createCategoryCode(productCategoryCreate)

            val productCategory: ProductCategoryEntity = ProductCategoryEntity(
                productLevel = productCategoryCreate.productLevel,
                categoryCode = categoryCode,
                categoryName = productCategoryCreate.categoryName,
                upCategoryCode = productCategoryCreate.upCategoryCode,
                productEntity = listOf(),
            )

            try {
                val savedProductCategory = productCategoryRepository.save(productCategory)

                return ProductCategoryInformation(
                    productLevel = savedProductCategory.productLevel,
                    categoryCode = savedProductCategory.categoryCode,
                    upCategoryCode = savedProductCategory.upCategoryCode,
                    categoryName = savedProductCategory.categoryName,
                    deleted = savedProductCategory.deleted,
                    deletedDate = savedProductCategory.deletedDate,
                )
            } catch (ex: DataIntegrityViolationException) {
                log.info("duplicate categoryCode [categoryCode={}]", categoryCode)
            }
        }
    }

    @Transactional
    override fun updateProductCategory(productCategoryUpdate: ProductCategoryUpdate): ProductCategoryInformation {
        // 상품타입을 수정한다.
        val foundProductCategory: ProductCategoryEntity =
            productCategoryRepository.findByCategoryCode(productCategoryUpdate.categoryCode)
                ?: throw ApplicationException(ErrorCode.CATEGORY_NOT_FOUND)

        val updatedCategory = foundProductCategory.updateCategoryName(productCategoryUpdate.categoryName)
        productCategoryRepository.save(updatedCategory)

        return ProductCategoryInformation(
            productLevel = updatedCategory.productLevel,
            categoryCode = updatedCategory.categoryCode,
            upCategoryCode = updatedCategory.upCategoryCode,
            categoryName = updatedCategory.categoryName,
            deleted = updatedCategory.deleted,
            deletedDate = updatedCategory.deletedDate,
        )
    }

    @Transactional
    override fun deleteProductCategory(categoryCode: String): Int {

        // 상품타입을 삭제한다.
        val currentTime: LocalDateTime = LocalDateTime.now(clockConfig.getClock())

        // prefix로 Like조회하여 일괄삭제
        // em.flush 해야할수도...?
        // TODO how to test?
        val result: Int = productCategoryRepository.deleteByCategoryCodeStartingWith(categoryCode, currentTime)

        if (result == 0) {
            throw ApplicationException(ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND.message)
        }
        return result

    }
    // ====== 상품타입 관련 - end ======

    override fun getProduct(productCode: String): ProductInformation {
        // 상품 한건을 조회한다.
        val foundProduct: ProductEntity =
            productRepository.findByProductCode(productCode) ?: throw ApplicationException(
                ErrorCode.PRODUCT_NOT_FOUND,
                ErrorCode.PRODUCT_NOT_FOUND.message
            )
        val productCategory: ProductCategoryEntity = foundProduct.productCategory
        val company: CompanyEntity = foundProduct.companyEntity

        return ProductInformation(
            productCode = foundProduct.productCode,
            productName = foundProduct.productName,
            price = foundProduct.price,
            stock = foundProduct.stock,
            productLevel = productCategory.productLevel,
            categoryCode = productCategory.categoryCode,
            categoryName = productCategory.categoryName,
            companyCode = company.companyCode,
            companyName = company.companyName,
            deleted = foundProduct.deleted,
            deletedDate = foundProduct.deletedDate,
        )
    }

    override fun getProductsByCompanyCode(companyCode: String): List<ProductInformation> {
        // 회사별 상품을 조회한다.
        val company: CompanyEntity = companyRepository.findByCompanyCode(companyCode) ?: throw ApplicationException(
            ErrorCode.COMPANY_NOT_FOUND,
            ErrorCode.COMPANY_NOT_FOUND.message
        )

        //TODO N+1 문제 발생
        val products: List<ProductEntity> = company.productEntity

        return products.map { it ->
            ProductInformation(
                productCode = it.productCode,
                productName = it.productName,
                price = it.price,
                stock = it.stock,
                productLevel = it.productCategory.productLevel,
                categoryCode = it.productCategory.categoryCode,
                categoryName = it.productCategory.categoryName,
                companyCode = company.companyCode,
                companyName = company.companyName,
                deleted = it.deleted,
                deletedDate = it.deletedDate,
            )
        }
    }

    @Transactional
    override fun createProduct(productCreate: ProductCreate): ProductInformation {
        // 상품을 등록한다.
        val company: CompanyEntity = companyRepository.findByCompanyCode(productCreate.companyCode)
            ?: throw ApplicationException(ErrorCode.COMPANY_NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.message)
        val category: ProductCategoryEntity =
            productCategoryRepository.findByCategoryCode(productCreate.categoryCode) ?: throw ApplicationException(
                ErrorCode.CATEGORY_NOT_FOUND,
                ErrorCode.CATEGORY_NOT_FOUND.message
            )

        // 상품코드생성로직
        val productCode: String = createProductCode(clockConfig)
        println("[Service] productCode = ${productCode}")

        val product: ProductEntity = ProductEntity(
            productCode = productCode,
            productName = productCreate.productName,
            price = productCreate.price,
            stock = productCreate.stock,
            productCategory = category,
            orders = mutableListOf(),
            companyEntity = company,
            deleted = false,
            deletedDate = null,
            createdAt = LocalDateTime.now(clockConfig.getClock()),
            id = null,
        )

        val savedProduct = productRepository.save(product)

        return ProductInformation(
            productCode = savedProduct.productCode,
            productName = savedProduct.productName,
            price = savedProduct.price,
            stock = savedProduct.stock,
            productLevel = category.productLevel,
            categoryCode = category.categoryCode,
            categoryName = category.categoryName,
            companyCode = company.companyCode,
            companyName = company.companyName,
            deleted = savedProduct.deleted,
            deletedDate = savedProduct.deletedDate,
        )
    }

    @Transactional
    override fun updateProduct(productUpdate: ProductUpdate): ProductInformation {
        // 상품을 정보를 수정한다
        TODO("Not yet implemented")
    }

    @Transactional
    override fun deleteProduct(productCode: String): ProductInformation {
        // 상품을 삭제한다.
        TODO("Not yet implemented")
    }

    // 상품타입 생생로직
    // 메가타입 -> AA01 ~ AA99
    // 메이저이후타입 -> AA0101 ~ AA9999
    // 서브타입 -> AA010101 ~ AA999999
    // 상위타입코드를 prefix로 사용. 이후 추가되는 코드마다 subfix 부분에 max +01 씩 증가
    internal fun createCategoryCode(productCategoryCreate: ProductCategoryCreate): String {
        // 카테고리 레벨 조회 (MEGA, MAJOR,SUB)
        val productLevel: ProductLevel = productCategoryCreate.productLevel
        // 상위 카테고리 조회
        val upCategoryCode: String = productCategoryCreate.upCategoryCode

        // maxCategoryCode 조회
        // MEGA : MEGA 레벨 중 가장 큰 categoryCode 조회 (빈값이면 AA00)
        // MAJOR, SUB : 동일한 상위 카테고리 중 가장 큰 categoryCode 조회(빈값이면 upCategoryCode+00)
        val maxCategoryCode: String = when (productLevel) {
            ProductLevel.MEGA -> productCategoryRepository.getMaxCategoryCodeByProductLevel(productLevel) ?: "AA00"
            else -> productCategoryRepository.getMaxCategoryCodeByUpCategoryCode(upCategoryCode)
                ?: (upCategoryCode + "00")
        }

        // prefix 추출
        // MEGA : 앞 두글자
        // MAJOR : 앞 네글자
        // SUB : 앞 여섯글자
        val prefixLength: Int = when (productLevel) {
            ProductLevel.MEGA -> 2
            ProductLevel.MAJOR -> 4
            ProductLevel.SUB -> 6
        }


        val prefix = maxCategoryCode.substring(0, prefixLength) // AA, AA00, AA0000
        val numberPart = maxCategoryCode.substring(prefixLength) // 00~99


        val nextNumber = numberPart.toInt() + 1
        val formattedNumber = nextNumber.toString().padStart(2, '0')


        // TODO 100 이상 이면 예외발생
        if (nextNumber >= 100) {
            throw ApplicationException(
                ErrorCode.CATEGORY_CODE_LIMIT_EXCEEDED,
                ErrorCode.CATEGORY_CODE_LIMIT_EXCEEDED.message
            )
        }


        return prefix + formattedNumber
    }

    // 생성시간날짜(yyyyMMdd) + 4자리 UUID() + sequence 6자리
    internal fun createProductCode(clockConfig: ClockConfig): String {
        // 날짜 생성
        val currentDateTime: LocalDateTime = LocalDateTime.now(clockConfig.getClock())
        val currentDate: LocalDate = currentDateTime.toLocalDate()
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val datePart: String = currentDate.format(dateFormatter)

        // UUID 생성
        val uuid: String = randomUUID().toString().substring(0, 4).uppercase()

        // sequence 생성 - start
        val startDay: LocalDateTime = currentDate.atStartOfDay()
        val endDay: LocalDateTime = currentDate.plusDays(1).atStartOfDay()
        // TODO redis 적용하기
        val countToday = productRepository.countByCreatedAtBetween(startDay, endDay)

        // 다음 시퀀스 번호 계산 (개수 + 1)
        val sequenceNumber = countToday + 1
        val sequencePart = String.format("%06d", sequenceNumber) // 000000 ~ 999999

        // sequence 생성 - end


        // 만약 sequence가 999999가 넘는다면?


        val result = datePart + uuid + sequencePart

        return result
    }

}
