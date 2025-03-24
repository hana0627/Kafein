package com.hana.baseproject.api.user.controller

import com.hana.baseproject.api.user.controller.request.UserCreate
import com.hana.baseproject.api.user.controller.request.UserUpdate
import com.hana.baseproject.api.user.controller.response.UserInformation
import com.hana.baseproject.api.user.service.UserService
import com.hana.baseproject.core.response.APIResponse
import org.springframework.web.bind.annotation.*

@RestController
class UserController (
    private val userService: UserService
) {

    @GetMapping("/v1/{username}/user/duplicate")
    fun duplicateUser(@PathVariable("username") username: String): APIResponse<Boolean> {
        val result = userService.duplicateUser(username)
        // 회원아이디 중복확인을 진행한다.
        return APIResponse.success(result)
    }

    @GetMapping("/v2/{companyCode}/{username}/user")
    fun showUser(@PathVariable("companyCode") companyCode: String, @PathVariable("username") username: String): APIResponse<UserInformation> {
        // 회원한명을 조회한다.
        val result = userService.findUser(companyCode, username)
        return APIResponse.success(result)
    }
    @GetMapping("/v2/{companyCode}/users")
    fun showCompanyUsers(@PathVariable("companyCode") companyCode: String): APIResponse<List<UserInformation>> {
        // 특정 회사의 회원을 조회한다.
        val result = userService.findUsersByCompanyCode(companyCode)
        return APIResponse.success(result)
    }

    @GetMapping("/v2/users")
    fun showAllUsers(): APIResponse<List<UserInformation>> {
        // 모든 회원의 목록을 조회한다.
        val result = userService.findUsers()
        return APIResponse.success(result)
    }

    @PostMapping("/v1/user")
    fun createUser(@RequestBody userCreate: UserCreate): APIResponse<UserInformation> {
        // 회원을 등록한다.
        val result = userService.createUser(userCreate)
        return APIResponse.success(result)
    }

    @PatchMapping("/v2/user")
    fun updateUser(@RequestBody userUpdate: UserUpdate): APIResponse<UserInformation> {
        // 회원 정보를 갱신한다.
        val result = userService.updateUser(userUpdate)
        return APIResponse.success(result)
    }

    @DeleteMapping("/v2/{username}/user")
    fun deleteUser(@PathVariable("username") username: String): APIResponse<UserInformation> {
        // 회원를 삭제한다.
        val result = userService.deleteUser(username)
        return APIResponse.success(result)
    }
}
