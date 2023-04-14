package com.example.DAO.product

import com.example.DatabaseFactory.dbQuery
import com.example.model.*
import com.example.model.categories.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

class DAOProductImpl : DAOProduct {

    private fun level1Mapper(resultRow: ResultRow) = CategoriesLevel1Dto(
        id = resultRow[CategoriesLevel1.id],
        name = resultRow[CategoriesLevel1.name],
        image = resultRow[CategoriesLevel1.image],
    )

    private fun level2Mapper(resultRow: ResultRow) = CategoriesLevel2Dto(
        id = resultRow[CategoriesLevel2.id],
        name = resultRow[CategoriesLevel2.name],
        image = resultRow[CategoriesLevel2.image],
        idParent = resultRow[CategoriesLevel2.idParent],
    )
    private fun level3Mapper(resultRow: ResultRow) = CategoriesLevel3Dto(
        id = resultRow[CategoriesLevel3.id],
        name = resultRow[CategoriesLevel3.name],
        image = resultRow[CategoriesLevel3.image],
        idParent = resultRow[CategoriesLevel3.idParent],
    )
    private fun level4Mapper(resultRow: ResultRow) = CategoriesLevel4Dto(
        id = resultRow[CategoriesLevel4.id],
        name = resultRow[CategoriesLevel4.name],
        image = resultRow[CategoriesLevel4.image],
        idParent = resultRow[CategoriesLevel4.idParent],
    )

    override suspend fun getCategoriesLevel1(): CategoriesLevel1Response {
        return transaction {
            val response = CategoriesLevel1.selectAll().map(::level1Mapper)
            CategoriesLevel1Response(response)
        }
    }

    override suspend fun getCategoriesLevel2(idLevel1: Int): CategoriesLevel2Response {
        return transaction {
            val response = CategoriesLevel2.select(CategoriesLevel2.idParent eq idLevel1).map(::level2Mapper)
            CategoriesLevel2Response(response)
        }
    }

    override suspend fun getCategoriesLevel3(idLevel2: Int): CategoriesLevel3Response {
        return transaction {
            val response = CategoriesLevel3.select(CategoriesLevel3.idParent eq idLevel2).map(::level3Mapper)
            CategoriesLevel3Response(response)
        }
    }

    override suspend fun getCategoriesLevel4(idLevel3: Int): CategoriesLevel4Response {
        return transaction {
            val response = CategoriesLevel4.select(CategoriesLevel4.idParent eq idLevel3).map(::level4Mapper)
            CategoriesLevel4Response(response)
        }
    }


    override suspend fun getProducts(idShop: Int, groupId: Int, level: Int): ProductResponse {
        return transaction {

            val level2 = 2
            val level3 = 3
            val level4 = 4
            val result = when (level) {
                level4 -> Product.categoryLevel4
                level3 -> Product.categoryLevel3
                level2 -> Product.categoryLevel2
                else -> Product.categoryLevel2
            }

            val response = (Price innerJoin Product).slice(
                Product.id,
                Product.name,
                Product.country,
                Product.description,
                Product.measure,
                Product.weight,
                Product.article,
                Product.image,
                Price.price,
                Price.priceWithOutStock,
            )
                .select((Price.idShop eq idShop) and ( result eq groupId) ).map(::productMapper)
            ProductResponse(response)
        }
    }

    private fun productMapper(row: ResultRow) = ProductDto(
        productId = row[Product.id],
        productName = row[Product.name],
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

    override suspend fun getDiscounts(): DiscountsResponse {
        return transaction {
            val response = Discount.selectAll().map{ discountMapper(it) }
            DiscountsResponse(response)
        }
    }
}

