package com.example.model

import org.jetbrains.exposed.sql.Table

object Token: Table("app_refresh_tokens") {
    val number = varchar("number", 30)
    val refreshToken = varchar("refreshToken", 255)
    val expiresAt = long("expiresAt")
}