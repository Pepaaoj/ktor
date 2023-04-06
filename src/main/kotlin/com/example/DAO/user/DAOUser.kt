package com.example.DAO.user

import com.example.model.*

interface DAOUser {

    suspend fun signIn(signInData: SignInData,issuer: String, audience: String, secret: String): TokenPair

    suspend fun signUp(signUpData: SignUpData)

    suspend fun updateData(newData: NewDataUser,number: String)

    suspend fun selectCity(number: String, city: Int)

    suspend fun getUser(number: String): UserDto?

    fun getCity(id: Int?): Cities?

    fun getUserSalt(id: Int): PasswordDto

    suspend fun getAllCities(): CityResponse

    suspend fun getAllShopsInCity(name: String): ShopResponse

    suspend fun getBonus(id: Int): BonusCardDto
}

