package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Password:Table("app_password") {
    val userId = integer("user_id").references(User.id)
    val password = varchar("password", 255)
    val salt = varchar("salt",255)
}

data class PasswordDto(
    val userId: Int,
    val password: String,
    val salt: String
)

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)