package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Shop: Table("app_shops") {
    val id = integer("id").autoIncrement()
    val name = varchar("name",255)
    val address = varchar("address", 255)
    val delivery = integer("delivery")
    val idCityDistricts = integer("id_cities_districts").references(CityDistricts.id)
    val primeTime = varchar("prime_time", 255)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class ShopDto(
    val id: Int,
    val name: String,
    val address:String,
    val delivery: Int,
    val idCityDistricts: CityDistrictsDto,
    val primeTime: String
)

@Serializable
data class FindShop(
    val id: Int,
    val name: String,
    val address: String
)

@Serializable
data class ShopByCityNameDto(
    val id: Int,
    val name: String,
    val address:String,
    val delivery: Int,
    val primeTime: String
)

@Serializable
data class SearchShopsInCity(
    val nameCity: String
)