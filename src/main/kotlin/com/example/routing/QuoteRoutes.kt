package com.example.routing

import com.example.controller.QuoteController
import com.example.dto.QuoteRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.quoteRoutes() {
    val quoteController by inject<QuoteController>()

    route("/quote") {
        get("/list") {
            val quotes = quoteController.getAllQuotes()
            call.respond(quotes)
        }

        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID parameter is missing")
                return@get
            }
            val quote = quoteController.getQuote(id)
            if (quote != null) {
                call.respond(quote)
            } else {
                call.respond(HttpStatusCode.NotFound, "Quote not found")
            }
        }

        post("/add") {
            val quoteRequest = call.receive<QuoteRequest>()
            val newQuote = quoteController.addQuote(
                quoteRequest.quoteText,
                quoteRequest.author ?: "Unknown",
                quoteRequest.source
            )
            call.respond(HttpStatusCode.Created, newQuote)
        }

        delete("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID parameter is missing")
                return@delete
            }
            if (quoteController.deleteQuote(id)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Quote not found or could not be deleted")
            }
        }
    }
}