package com.example.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.Token
import com.example.model.TokenPair
import com.example.model.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.*


fun Application.installJwt(myRealm: String, secret: String, issuer: String, audience: String) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier {
                createJWT(Algorithm.HMAC256(secret), issuer, audience)
            }
            validate { credential ->
                if (credential.payload.expiresAt.time > System.currentTimeMillis())
                    JWTPrincipal(credential.payload)
                else
                    null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

private fun createJWT(algorithm: Algorithm, issuer: String, audience: String): JWTVerifier =
    JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

fun Long.withOffset(offset: Duration) = this + offset.toMillis()

fun generateTokenPair(
    number: String,
    isUpdate: Boolean = false,
    userDto: UserDto,
    audience: String,
    issuer: String,
    secret: String
): TokenPair {
    val currentTime = System.currentTimeMillis()

    val token = JWT.create()
        .withSubject(number)
        .withExpiresAt(Date(currentTime.withOffset(Duration.ofMinutes(260640))))
        .withClaim("number", userDto.number)
        .withClaim("name", userDto.name)
        .withClaim("surname", userDto.surname)
        .withClaim("email", userDto.email)
        .withClaim("patronymic", userDto.patronymic)
        .withClaim("address", userDto.address)
        .withClaim("currentShop", userDto.idCurrentShop?.name)
        .withClaim("currentShopAddress", userDto.idCurrentShop?.address)
        .withClaim("city", userDto.idCity?.name)

        .withAudience(audience)
        .withIssuer(issuer)
        .sign(Algorithm.HMAC256(secret))

    val refreshToken = UUID.randomUUID().toString()
    if (!isUpdate) {
        transaction {
            Token.insert {
                it[Token.number] = number
                it[Token.refreshToken] = refreshToken
                it[expiresAt] = currentTime.withOffset(Duration.ofDays(175))
            }
        }
    }
    return TokenPair(token, refreshToken)
}
