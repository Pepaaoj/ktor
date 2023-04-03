package com.example.model

import com.example.model.CityDistricts.references
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


object CityDistricts: Table("app_cities_districts") {

    val id = integer("id").autoIncrement()
    val idCity = integer("id_city").references(City.id).nullable()
    val district = varchar("district", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class CityDistrictsDto(
    val id: Int,
    val idCity: Cities?,
    val district: String?
)