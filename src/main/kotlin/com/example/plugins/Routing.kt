package com.example.plugins

import com.example.DAO.product.DAOProduct
import com.example.DAO.product.DAOProductImpl
import com.example.DAO.user.DAOUser
import com.example.DAO.user.DAOUserImpl
import com.example.model.*
import com.example.security.SHA256HashingService
import com.example.security.SaltedHash
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    secret: String,
    issuer: String,
    audience: String
) {
    routing {
        val daoProduct: DAOProduct = DAOProductImpl()
        val daoUser: DAOUser = DAOUserImpl()
        get("/app/tree") {
            call.respond(daoProduct.getMainGroup())
        }

        authenticate("auth-jwt") {
            get("/app/getUser{number}") {
                val number = call.parameters["number"]
                val user = daoUser.getUser(number!!)
                if (user == null) {
                    call.respond(HttpStatusCode.Conflict, "Пользователь не найден")
                    return@get
                }
                call.respond(user)
            }
        }

        get("/app/getShops{nameCity}") {
            val nameCity = call.parameters["nameCity"]
            if (nameCity.isNullOrEmpty()){
                call.respond(HttpStatusCode.Conflict, "Пользователь уже существует")
                return@get
            }
            call.respond(daoUser.getAllShopsInCity(nameCity))
        }

        get("/app/getCities") {
            call.respond(daoUser.getAllCities())
        }

        post("/app/signUp") {
            val signUpData = call.receive<SignUpData>()
            val user = daoUser.getUser(signUpData.number)
            if (user != null) {
                call.respond(HttpStatusCode.Conflict, "Пользователь уже существует")
                return@post
            }
            call.respond(daoUser.signUp(signUpData))
        }

        post("/app/signIn") {
            val signInData = call.receive<SignInData>()
            val user = daoUser.getUser(signInData.number)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, "Неверно указан номер или пароль")
                return@post
            }
            val passwordDto = daoUser.getUserSalt(user.id)
            val hashingService = SHA256HashingService()
            val isValidPassword = hashingService.verify(
                value = signInData.password,
                saltedHash = SaltedHash(
                    hash = passwordDto.password,
                    salt = passwordDto.salt
                )
            )
            if (!isValidPassword) {
                call.respond(HttpStatusCode.Conflict, "Неверно указан номер или пароль")
                return@post
            }
            call.respond(HttpStatusCode.OK, daoUser.signIn(signInData, issuer, audience, secret))
        }

        authenticate("auth-jwt") {
            put("/app/updateDate{number}") {
                val number = call.parameters["number"]
                val user = daoUser.getUser(number!!)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "Ошибка")
                    return@put
                }
                val newData = call.receive<NewDataUser>()
                call.respond(daoUser.updateData(newData, number))
            }
        }

        put("/app/selectCityInUser{number}") {
            val number = call.parameters["number"]
            if (number == null) {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@put
            }
            val user = daoUser.getUser(number)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "Ошибка")
                return@put
            }
            val cityId = daoUser.getCity(call.receive<SelectCity>().id)
            if (cityId == null) {
                call.respond(HttpStatusCode.NotFound, "Ошибка")
                return@put
            }
            call.respond(daoUser.selectCity(number, cityId.id))
        }

        post("/app/getProducts{address}") {
            val address = call.parameters["address"]
            val group = call.receive<GetGroupId>()

            if (address.isNullOrEmpty()) {
                call.respond(HttpStatusCode.NotFound, "Ошибка")
                return@post
            }
            val shop = daoProduct.findShop(address)
            call.respond(daoProduct.getProducts(shop, group.id))
        }

        get("/app/getDiscounts"){
            val discountDto = daoProduct.getDiscounts()
            if (discountDto.isEmpty()){
                call.respond(HttpStatusCode.NoContent, "Ошибка")
                return@get
            }
            call.respond(discountDto)
        }

        get("/app/getBonus{number}"){
            val number = call.parameters["number"]
            if (number.isNullOrEmpty()) {
                call.respond(HttpStatusCode.NotFound, "Ошибка")
                return@get
            }
            val user = daoUser.getUser(number)
            if (user == null ){
                call.respond(HttpStatusCode.NotFound, "Ошибка")
                return@get
            }

            call.respond(daoUser.getBonus(user.id))

        }

    }
}

