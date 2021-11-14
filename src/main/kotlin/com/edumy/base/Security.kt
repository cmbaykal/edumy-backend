package com.edumy.base

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity() {

//    val issuer: String = stringProperty("jwt.issuer")
//    val algorithm = Algorithm.HMAC256(stringProperty("jwt.access.secret"))
//
//    val accessLifetime = longProperty("jwt.access.lifetime")    // minutes
//    val refreshLifetime = longProperty("jwt.refresh.lifetime")  // days

    install(Authentication) {
        jwt("access") {
//            verifier {
//              makeJWTVerifier(algorithm, issuer)
//            }

            validate { token ->
                if (token.payload.expiresAt.time > System.currentTimeMillis())
                    JWTPrincipal(token.payload)
                else null
            }
        }
    }



}
