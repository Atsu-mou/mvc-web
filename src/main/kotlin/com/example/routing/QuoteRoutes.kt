package com.example.routing

import com.example.controller.QuoteController
import com.example.dto.QuoteRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Route.quoteRoutes() {
    val quoteController by inject<QuoteController>()
    val logger = LoggerFactory.getLogger("QuoteRoutes")

    route("/quote") {
        get("/list") {
            try {
                val quotes = quoteController.getAllQuotes()
                call.respond(quotes)
            } catch (e: Exception) {
                logger.error("Error retrieving all quotes", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve quotes"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]
                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID parameter is missing or invalid"))
                    return@get
                }
                val quote = quoteController.getQuote(id)
                if (quote != null) {
                    call.respond(quote)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Quote not found"))
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("Invalid request: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            } catch (e: Exception) {
                logger.error("Error retrieving quote", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve quote"))
            }
        }

        post("/add") {
            try {
                val quoteRequest = call.receive<QuoteRequest>()
                val newQuote = quoteController.addQuote(
                    quoteRequest.quoteText,
                    quoteRequest.author ?: "Unknown",
                    quoteRequest.source
                )
                call.respond(HttpStatusCode.Created, newQuote)
            } catch (e: IllegalArgumentException) {
                logger.warn("Invalid quote data: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid quote data")))
            } catch (e: Exception) {
                logger.error("Error creating quote", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create quote"))
            }
        }

        delete("/{id}") {
            try {
                val id = call.parameters["id"]
                if (id.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID parameter is missing or invalid"))
                    return@delete
                }
                if (quoteController.deleteQuote(id)) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Quote deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Quote not found or could not be deleted"))
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("Invalid request: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            } catch (e: Exception) {
                logger.error("Error deleting quote", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete quote"))
            }
        }
    }
}