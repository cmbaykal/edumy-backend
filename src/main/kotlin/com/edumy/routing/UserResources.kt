package com.edumy.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/user/auth/token")
data class UserAuth(val userId: String)

@Serializable
@Resource("/user/register")
class RegisterUser

@Serializable
@Resource("/user/login")
class LoginUser

@Serializable
@Resource("/user/update")
class UpdateUser

@Serializable
@Resource("/user/changePassword")
class ChangePassword

@Serializable
@Resource("/user/info")
data class UserInfo(val userId: String)

@Serializable
@Resource("/user/delete")
data class DeleteUser(val userId: String)

@Serializable
@Resource("/user/all")
class AllUsers

