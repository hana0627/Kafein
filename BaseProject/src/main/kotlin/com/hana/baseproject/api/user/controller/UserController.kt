package com.hana.baseproject.api.user.controller

import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class UserController {

    @GetMapping("/v1/{companyCode}/{username}/user/duplicate")
    fun duplicateUser(@PathVariable("companyCode") companyCode: String, @PathVariable("username") username: String): APIResponse<String> {
        // 회원아이디 중복확인을 진행한다.
        return APIResponse.success("");
    }

    @GetMapping("/v2/{companyCode}/{username}/user")
    fun showUser(@PathVariable("companyCode") companyCode: String, @PathVariable("username") username: String): APIResponse<String> {
        // 회원한명을 조회한다.
        return APIResponse.success("");
    }
    @GetMapping("/v2/{companyCode}/users")
    fun showCompanyUsers(@PathVariable("companyCode") companyCode: String): APIResponse<String> {
        // 특정 회사의 회원을 조회한다.
        return APIResponse.success("");
    }

    @GetMapping("/v2/users")
    fun showAllUsers(): APIResponse<String> {
        // 모든 회원의 목록을 조회한다.
        return APIResponse.success("");
    }

    @PostMapping("/v1/user")
    fun createUser(): APIResponse<String> {
        // 회원을 등록한다.
        return APIResponse.success("");
    }

    @PatchMapping("/v2/user")
    fun updateUser(): APIResponse<String> {
        // 회원 정보를 갱신한다.
        return APIResponse.success("");
    }

    @DeleteMapping("/v2/{companyCode}/{username}/company")
    fun deleteUser(@PathVariable("companyCode") companyCode: String, @PathVariable("username") username: String): APIResponse<String> {
        // 회원를 삭제한다.
        return APIResponse.success("");
    }
}
