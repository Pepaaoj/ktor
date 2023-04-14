package com.example.DAO.product

import com.example.model.*
import com.example.model.categories.CategoriesLevel1Response
import com.example.model.categories.CategoriesLevel2Response
import com.example.model.categories.CategoriesLevel3Response
import com.example.model.categories.CategoriesLevel4Response


interface DAOProduct {

    suspend fun getCategoriesLevel1(): CategoriesLevel1Response
    suspend fun getCategoriesLevel2(idLevel1: Int): CategoriesLevel2Response
    suspend fun getCategoriesLevel3(idLevel2: Int): CategoriesLevel3Response
    suspend fun getCategoriesLevel4(idLevel3: Int): CategoriesLevel4Response
    suspend fun getProducts(idShop: Int, groupId: Int, level: Int): ProductResponse
    fun findShop(address: String): Int
    suspend fun getDiscounts(): DiscountsResponse
}