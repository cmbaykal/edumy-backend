package com.edumy.data.user

import io.ktor.locations.*

@Location("/user/auth/token")
data class UserAuth(val userId: String)

@Location("/user/register")
class RegisterUser

@Location("/user/login")
class LoginUser

@Location("/user/update")
class UpdateUser

@Location("/user/changePassword")
class ChangePassword

@Location("/user/info")
data class UserInfo(val userId: String)

@Location("/user/delete")
data class DeleteUser(val userId: String)

@Location("/user/all")
class AllUsers

