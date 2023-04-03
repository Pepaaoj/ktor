package com.example.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object User : Table("app_customer") {

    val id = integer("id").autoIncrement()
    val fullName = varchar("full_name", 255)
    val name = varchar("name", 255)
    val surname = varchar("surname", 255)
    val patronymic = varchar("patronymic", 255)
    val number = varchar("number", 255)
    val address = varchar("address", 255).nullable()
    val dateOfBirth = date("date_of_birth")
    val email = varchar("email", 255).nullable()
    val idCurrentShop = integer("id_current_shop").references(Shop.id).nullable()
    val idCity = integer("id_city").references(City.id).nullable()

    override val primaryKey = PrimaryKey(id)
}

object DateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

@Serializable
data class UserDto(
    val id: Int,
    val fullName: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val number: String,
    val address: String?,
    val dateOfBirth: String,
    val email: String?,
    val idCurrentShop: ShopDto?,
    val idCity: Cities?
)

@Serializable
data class NewDataUser(
    val name: String,
    val surname: String,
    val patronymic: String,
    val number: String,
    val dateOfBirth: String,
    val email: String?,
)

@Serializable
data class ChangeCity(
    val newCity: Cities?
)

@Serializable
data class UserNumber(
    val number:String
)

@Serializable
data class SignUpData(
    val name: String,
    val surname: String,
    val patronymic: String,
    val number: String,
    val dateOfBirth: String,
    val email: String?,
    val password: String
)

@Serializable
data class SignInData(
    val number: String,
    val password: String
)