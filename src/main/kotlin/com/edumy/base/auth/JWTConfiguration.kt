package com.edumy.base.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class JWTConfiguration private constructor(secret: String) {

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun makeAccessToken(userId: String): String = JWT
        .create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim(ClAIM, userId)
        .sign(algorithm)

    companion object {
        lateinit var instance: JWTConfiguration
            private set

        fun initialize(secret: String) {
            synchronized(this) {
                if (!this::instance.isInitialized) {
                    instance = JWTConfiguration(secret)
                }
            }
        }

        private const val ISSUER = "edumy-backend"
        private const val AUDIENCE = "edumy-backend"
        const val ClAIM = "userId"
    }
}