package com.example.model

import com.example.model.categories.CategoriesLevel1
import com.example.model.categories.CategoriesLevel2
import com.example.model.categories.CategoriesLevel3
import com.example.model.categories.CategoriesLevel4
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Product: Table("app_product") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    val description = varchar("description", 255)
    val weight = varchar("weight",255)
    val country = varchar("country", 255)
    val measure = varchar("measure",255)
    val idTax = integer("id_tax")
    val article = varchar("article", 255)
    val image = varchar("image",255)
    val categoryLevel2 = integer("id_category_level2").references(CategoriesLevel2.id).nullable()
    val categoryLevel3 = integer("id_category_level3").references(CategoriesLevel3.id).nullable()
    val categoryLevel4 = integer("id_category_level4").references(CategoriesLevel4.id).nullable()

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class ProductResponse(
    val response: List<ProductDto>
)

@Serializable
data class ProductDto(
    val productId: Int,
    val productName: String,
    val productDescription: String,

    val productArticle: String,
    val productImage: String,
    val productWeight: String,
    val productCountry: String,
    val productMeasure: String,
    val currentPrice: String,
    val oldPrice: String
)
