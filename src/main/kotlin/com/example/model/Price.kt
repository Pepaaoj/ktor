package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Price: Table("app_prices") {
    val idProduct = integer("id_product").references(Product.id)
    val idShop = integer("id_shop").references(Shop.id)
    val price = varchar("price", 255)
    val priceWithOutStock = varchar("price_without_stock", 255)
}

@Serializable
data class PricesDto(
    val price: String,
    val oldPrice: String
)