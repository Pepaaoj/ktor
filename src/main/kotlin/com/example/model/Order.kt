package com.example.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Order : Table("app_order") {

    val idOrder = integer("id").autoIncrement()
    val idCustomer = integer("id_customer").references(User.id)
    val idShop = integer("id_shop").references(Shop.id)
    val orderDate = date("order_date")
    val status = varchar("status", 255)
    val comment = varchar("comment", 255)
    val deliveryTime = varchar("delivery_time", 255)
    val totalSum = varchar("total_summ", 255)
    val totalWeight = varchar("total_weight", 255)
    val deliveryAddress = varchar("delivery_address", 255)

    override val primaryKey = PrimaryKey(idOrder)
}