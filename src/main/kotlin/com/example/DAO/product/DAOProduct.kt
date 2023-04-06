package com.example.DAO.product

import com.example.model.*


interface DAOProduct {

    suspend fun getSubGroup(id: Int): List<Group>

    suspend fun getMainGroup(): List<MainGroup>

    suspend fun getProducts(idShop: Int, groupId: Int): ProductResponse

    fun findShop(address: String): Int

    suspend fun getDiscounts(): DiscountsResponse?
}