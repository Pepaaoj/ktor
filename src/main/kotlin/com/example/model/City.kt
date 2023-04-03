package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object City: Table("app_city") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class SelectCity(
    val id: Int
)

@Serializable
data class Cities(
    val id: Int,
    val name: String
)

@Serializable
data class CityResponse(
    val response: List<Cities>
)