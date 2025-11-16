package com.example.routing

import com.example.routing.quoteRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Add the quote routes to the application's routing
        quoteRoutes()
    }
}