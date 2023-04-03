package com.example.DAO.user

import com.example.DatabaseFactory.dbQuery
import com.example.model.*
import com.example.security.SHA256HashingService
import com.example.security.jwt.generateTokenPair
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern


class DAOUserImpl : DAOUser {

    /**
     * Маппер таблицы City
     */
    private fun city(row: ResultRow) = Cities(
        id = row[City.id],
        name = row[City.name]
    )

    /**
     * Маппер таблицы Shop
     */
    private fun shop(row: ResultRow) = ShopDto(
        id = row[Shop.id],
        name = row[Shop.name],
        address = row[Shop.address],
        delivery = row[Shop.delivery],
        idCityDistricts = getCityDistricts(row[Shop.idCityDistricts]),
        primeTime = row[Shop.primeTime]
    )

    /**
     * Маппер таблицы Shop
     */
    private fun shopByCityName(row: ResultRow) = ShopByCityNameDto(
        id = row[Shop.id],
        name = row[Shop.name],
        address = row[Shop.address],
        delivery = row[Shop.delivery],
        primeTime = row[Shop.primeTime]
    )

    /**
     * Маппер таблицы CityDistricts
     */
    private fun cityDistricts(row: ResultRow) = CityDistrictsDto(
        id = row[CityDistricts.id],
        idCity = getCity(row[CityDistricts.idCity]),
        district = row[CityDistricts.district]
    )

    /**
     * Маппер таблицы CityDistricts
     */
    private fun users(row: ResultRow): UserDto {
        print(row[User.dateOfBirth])
        return UserDto(
            id = row[User.id],
            fullName = row[User.fullName],
            name = row[User.name],
            surname = row[User.surname],
            patronymic = row[User.patronymic],
            number = row[User.number],
            address = row[User.address],
            dateOfBirth = row[User.dateOfBirth].toString(),
            email = row[User.email],
            idCity = getCity(row[User.idCity]),
            idCurrentShop = selectedShop(row[User.idCurrentShop])
        )
    }

    /**
     * Запрос для получения магазина по айди
     * */
    private fun selectedShop(id: Int?): ShopDto? {
        return try {
            transaction {
                Shop.select(Shop.id eq id!!).map(::shop).single()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Запрос для получения города по названию
     * */
    private fun getCityByName(name: String): Int {
        val transaction = transaction {
            City.select(City.name eq name).map(::city).single()
        }
        return transaction.id
    }

    /**
     * Запрос для получения города/района по айди
     * */
    private fun getCityDistrictsById(name: String): Int {
        val transaction = transaction {
            CityDistricts.select(CityDistricts.idCity eq getCityByName(name)).map(::cityDistricts)
                .single()
        }

        return transaction.id
    }

    /**
     * Запрос для получения города/района по айди
     * */

    private fun getCityDistricts(id: Int): CityDistrictsDto {
        return transaction {
            CityDistricts.select(CityDistricts.id eq id).map(::cityDistricts).single()
        }
    }

    /**
     * Запрос для получения города по айди
     * */
    override fun getCity(id: Int?): Cities? {
        return try {
            transaction {
                City.select(City.id eq id!!).map(::city).single()
            }
        } catch (e: Exception) {
            null
        }
    }



    override suspend fun getAllCities(): CityResponse {
        return transaction {
            val response = City.selectAll().map(::city)
            CityResponse(response)
        }
    }

    override suspend fun getAllShopsInCity(name: String): List<ShopByCityNameDto> = dbQuery {
        Shop.select(Shop.idCityDistricts eq getCityDistrictsById(name)).map(::shopByCityName)
    }

    private fun bonusCardMapper(row: ResultRow) = BonusCardDto(
        idUser = row[BonusCard.idUser],
        cardNumber = row[BonusCard.cardNumber],
        bonusPoints = row[BonusCard.bonusPoints]
    )

    override suspend fun getBonus(id: Int): BonusCardDto {
        return transaction {
            (BonusCard innerJoin User).select(BonusCard.idUser eq id).map { bonusCardMapper(it) }
                .single()
        }
    }

    /**
     * Запрос для получения данных пользователя по номеру телефона
     * */
    override suspend fun getUser(number: String): UserDto? {
        return try {
            transaction {
                User.select(User.number eq number).map(::users).single()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun saltResult(row: ResultRow) = PasswordDto(
        userId = row[Password.userId], password = row[Password.password], salt = row[Password.salt]
    )

    override fun getUserSalt(id: Int): PasswordDto {
        return transaction {
            Password.select(Password.userId eq id).map(::saltResult).single()
        }
    }

    override suspend fun signIn(
        signInData: SignInData, issuer: String, audience: String, secret: String
    ): TokenPair {
        val user = getUser(signInData.number)
        return generateTokenPair(user!!.number, false, user, audience, issuer, secret)
    }

    private suspend fun insertPassword(signUpData: SignUpData) {
        val hashingService = SHA256HashingService()
        val saltedHash = hashingService.generateSaltedHash(signUpData.password)
        val user = getUser(signUpData.number)
        if (user != null) {
            transaction {
                Password.insert {
                    it[userId] = user.id
                    it[password] = saltedHash.hash
                    it[salt] = saltedHash.salt
                }
            }
        }
    }

    override suspend fun signUp(signUpData: SignUpData) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(signUpData.dateOfBirth, formatter)
        transaction {
            User.insert {
                it[name] = signUpData.name
                it[surname] = signUpData.surname
                it[dateOfBirth] = date
                it[patronymic] = signUpData.patronymic
                it[number] = signUpData.number
                it[fullName] = "${signUpData.surname} ${signUpData.name} ${signUpData.patronymic}"
            }
        }
        insertPassword(signUpData)
    }

    override suspend fun updateData(newData: NewDataUser, number: String) {
        transaction {
            val formatter = ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(newData.dateOfBirth, formatter)
            User.update({ User.number eq number }) {
                it[name] = newData.name
                it[surname] = newData.surname
                it[patronymic] = newData.patronymic
                it[fullName] = "${newData.surname} ${newData.name} ${newData.patronymic}"
                it[dateOfBirth] = date
                it[email] = newData.email
            }
        }
    }

    override suspend fun selectCity(number: String, city: Int) {
        transaction {
            try {
                User.update({ User.number eq number }) {
                    it[idCity] = city
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}