package com.example.DAO.product

import com.example.DatabaseFactory.dbQuery
import com.example.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

class DAOProductImpl : DAOProduct {

    private fun resultRowToSubGroup(row: ResultRow) = Group(
        id = row[Groups.id],
        name = row[Groups.name],
        idParent = row[Groups.idParent],
        tree = row[Groups.tree],
        image = row[Groups.image]
    )

    private fun resultRowToMainGroup(row: ResultRow) = MainGroup(
        id = row[Groups.id],
        name = row[Groups.name],
        idParent = sub(row[Groups.id]),
        tree = row[Groups.tree],
        image = row[Groups.image]
    )

    private fun sub(id: Int): List<Group> {
        return transaction {
            Groups.select { Groups.idParent eq id }.map(::resultRowToSubGroup)
        }
    }

    override suspend fun getSubGroup(id: Int): List<Group> = dbQuery {
        sub(id)
    }

    override suspend fun getMainGroup(): List<MainGroup> = dbQuery {
        Groups.select(Groups.idParent.isNull()).map(::resultRowToMainGroup)
    }

    override suspend fun getProducts(idShop: Int, groupId: Int): ProductResponse {
        return transaction {
            val response = (Price innerJoin Product).slice(
                Product.id,
                Product.name,
                Product.idGroup,
                Product.country,
                Product.description,
                Product.measure,
                Product.weight,
                Product.article,
                Product.image,
                Price.price,
                Price.priceWithOutStock,
            )
                .select((Price.idShop eq idShop) and (Product.idGroup eq groupId) ).map(::productMapper)
            ProductResponse(response)
        }
    }

    private fun productMapper(row: ResultRow) = ProductDto(
        productId = row[Product.id],
        productName = row[Product.name],
        productIdGroup = row[Product.idGroup],
        productCountry = row[Product.country],
        productDescription = row[Product.description],
        productMeasure = row[Product.measure],
        productWeight = row[Product.weight],
        productArticle = row[Product.article],
        productImage = row[Product.image],
        currentPrice = row[Price.price],
        oldPrice = row[Price.priceWithOutStock]
    )

    private fun shop(row: ResultRow) = FindShop(
        id = row[Shop.id],
        name = row[Shop.name],
        address = row[Shop.address],
    )

    override fun findShop(address: String): Int {
        val result = transaction {
            Shop.select(Shop.address eq address).map(::shop).single()
        }
        return result.id
    }

    private fun discountMapper(row: ResultRow) = DiscountDto(
        idProduct = row[Discount.idProduct],
        idShop = row[Discount.idShop],
        name = row[Discount.name],
        description = row[Discount.description],
        startDate = row[Discount.startDate].toString(),
        endDate = row[Discount.endDate].toString(),
        image = row[Discount.image]
    )

    override suspend fun getDiscounts(): List<DiscountDto> {
        return transaction {
            Discount.selectAll().map{ discountMapper(it) }
        }
    }
}

