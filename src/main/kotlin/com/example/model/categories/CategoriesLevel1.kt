package com.example.model.categories


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object CategoriesLevel1: Table("app_categories_level1"){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val image = varchar("image", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}


@Serializable
data class CategoriesLevel1Dto(
    val id: Int,
    val name: String,
    val image: String?,
)

@Serializable
data class CategoriesLevel1Response(
    val response: List<CategoriesLevel1Dto>
)

@Serializable
data class GetCategoryId(
    val id: Int
)