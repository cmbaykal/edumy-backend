package com.edumy.routing.user

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/user/auth/token")
class UserAuth(val userId: String)

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
class ChangePasswordUser

@Serializable
@Resource("/user/info")
class UserInfo(val userId: String)

@Serializable
@Resource("/user/delete")
class DeleteUser(val userId: String)

@Serializable
@Resource("/user/all")
class AllUsers
