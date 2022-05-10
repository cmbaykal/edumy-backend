package com.edumy.base

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.edumy.data.user.UserEntity
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureSecurity() {
    val appRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt {
            verifier(JWTConfig.verifier)
            realm = appRealm
            validate { credentials ->
                JWTPrincipal(credentials.payload)
            }
        }
    }
}

object JWTConfig {
    private const val secretValue = "LocalSecret"
    private const val issuerValue = "ktor.io"
    private const val expireInterval = 36_000_00 // 1 Hour
    private val algorithm = Algorithm.HMAC512(secretValue)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuerValue)
        .build()

    val expireTime get() = Date(System.currentTimeMillis() + expireInterval)

    fun generateToken(user: UserEntity): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuerValue)
        .withClaim("userId", user.id)
        .withClaim("email", user.mail)
        .withExpiresAt(expireTime)
        .sign(algorithm)
}
