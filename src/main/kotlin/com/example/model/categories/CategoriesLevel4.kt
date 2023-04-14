package com.example.model.categories

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object CategoriesLevel4: Table("app_categories_level4"){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val image = varchar("image", 255).nullable()
    val idParent = integer("id_parent").references(CategoriesLevel3.id)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class CategoriesLevel4Dto(
    val id: Int,
    val name: String,
    val image: String?,
    val idParent: Int,
)

@Serializable
data class CategoriesLevel4Response(
    val response: List<CategoriesLevel4Dto>
)