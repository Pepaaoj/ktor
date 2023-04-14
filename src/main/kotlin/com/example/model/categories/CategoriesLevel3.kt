package com.example.model.categories

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object CategoriesLevel3: Table("app_categories_level3"){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val image = varchar("image", 255).nullable()
    val idParent = integer("id_parent").references(CategoriesLevel2.id)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class CategoriesLevel3Dto(
    val id: Int,
    val name: String,
    val image: String?,
    val idParent: Int,
)

@Serializable
data class CategoriesLevel3Response(
    val response: List<CategoriesLevel3Dto>
)