package com.example

import com.example.plugins.*
import com.example.security.jwt.installJwt
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    DatabaseFactory.init()
    configureSerialization()
    installJwt(myRealm, secret, issuer, audience)
    configureMonitoring()
    configureRouting(secret, issuer, audience)
}
