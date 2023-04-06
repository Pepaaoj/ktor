package com.example.model

import io.netty.util.collection.IntObjectMap
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Discount: Table("app_discounts") {
    val idProduct = integer("id_product").references(Product.id)
    val idShop = integer("id_shop").references(Shop.id)
    val name = varchar("name", 255)
    val startDate = date("start_date")
    val image = varchar("image", 255)
    val description = varchar("description", 255)
    val endDate = date("end_date")
}

@Serializable
data class DiscountDto(
    val idProduct: Int,
    val idShop: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val image: String,
    val description: String
)

@Serializable
data class DiscountsResponse(
    val response: List<DiscountDto>
)