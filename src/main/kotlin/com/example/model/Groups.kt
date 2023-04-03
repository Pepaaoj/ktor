package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table

@Serializable
data class Group(
    val id: Int,
    val name: String,
    val idParent: Int?,
    val tree: String,
    val image: String?
)

@Serializable
data class Test(
    val id: Int = 15,
    val name: String = "Test Name"
)

@Serializable
data class MainGroup(
    val id: Int,
    val name: String,
    val idParent: List<Group>?,
    val tree: String,
    val image: String?
)

@Serializable
data class GetGroupId(
    val id: Int
)

object Groups: Table("app_group"){
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val idParent = integer("idParent").references(id).nullable()
    val tree = varchar("tree", 255)
    val image = varchar("image", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}


