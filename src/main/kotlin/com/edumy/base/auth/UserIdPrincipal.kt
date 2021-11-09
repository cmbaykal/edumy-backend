package com.edumy.base.auth

import io.ktor.auth.*

data class UserIdPrincipal(val userId: String) : Principal
