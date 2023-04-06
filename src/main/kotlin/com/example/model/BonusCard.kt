package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object BonusCard : Table("app_bonus_cards") {
    val cardNumber = integer("card_number")
    val idUser = integer("id_customer").references(User.id)
    val bonusPoints = varchar("bonus_points", 255)
}

@Serializable
data class BonusCardDto(
    val idUser: Int,
    val cardNumber: Int,
    val bonusPoints: String,
)

