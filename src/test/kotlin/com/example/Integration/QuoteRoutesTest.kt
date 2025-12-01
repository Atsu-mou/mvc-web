package com.example.Integration

import com.example.controller.QuoteController
import com.example.dto.QuoteRequest
import com.example.model.Quote
import com.example.routing.quoteRoutes
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.assertEquals

class QuoteRoutesTest {

    private fun TestApplicationBuilder.setupTestApplication(mockController: QuoteController) {
        application {
            install(Koin) {
                modules(module {
                    single { mockController }
                })
            }
            install(ServerContentNegotiation) {
                json()
            }
            routing {
                quoteRoutes()
            }
        }
    }

    @Test
    fun `GET quote by id should return 200 OK when quote exists`() = testApplication {
        val mockController = mockk<QuoteController>()
        val quoteId = "test-id"
        val expectedQuote = Quote(id = quoteId, quoteText = "A test quote", author = "Tester")

        coEvery { mockController.getQuote(quoteId) } returns expectedQuote

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.get("/quote/$quoteId")
        val actualQuote = response.body<Quote>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedQuote, actualQuote)
    }

    @Test
    fun `GET quote by id should return 404 Not Found when quote does not exist`() = testApplication {
        val mockController = mockk<QuoteController>()
        val quoteId = "non-existent-id"

        coEvery { mockController.getQuote(quoteId) } returns null

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.get("/quote/$quoteId")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET quote list should return 200 OK with a list of quotes`() = testApplication {
        val mockController = mockk<QuoteController>()
        val expectedQuotes = listOf(
            Quote(id = "1", quoteText = "First quote", author = "Author 1"),
            Quote(id = "2", quoteText = "Second quote", author = "Author 2")
        )

        coEvery { mockController.getAllQuotes() } returns expectedQuotes

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.get("/quote/list")
        val actualQuotes = response.body<List<Quote>>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedQuotes, actualQuotes)
    }

    @Test
    fun `POST quote add should return 201 Created with the new quote`() = testApplication {
        val mockController = mockk<QuoteController>()
        val quoteRequest = QuoteRequest(quoteText = "New quote", author = "New Author")
        val expectedQuote = Quote(id = "new-id", quoteText = "New quote", author = "New Author")

        coEvery { mockController.addQuote(quoteRequest.quoteText, quoteRequest.author!!, quoteRequest.source) } returns expectedQuote

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.post("/quote/add") {
            contentType(ContentType.Application.Json)
            setBody(quoteRequest)
        }
        val actualQuote = response.body<Quote>()

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedQuote, actualQuote)
    }

    @Test
    fun `DELETE quote by id should return 200 OK when quote is deleted`() = testApplication {
        val mockController = mockk<QuoteController>()
        val quoteId = "delete-id"

        coEvery { mockController.deleteQuote(quoteId) } returns true

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.delete("/quote/$quoteId")
        val message = response.body<Map<String, String>>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Quote deleted successfully", message["message"])
    }

    @Test
    fun `DELETE quote by id should return 404 Not Found when quote does not exist`() = testApplication {
        val mockController = mockk<QuoteController>()
        val quoteId = "non-existent-id"

        coEvery { mockController.deleteQuote(quoteId) } returns false

        setupTestApplication(mockController)

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.delete("/quote/$quoteId")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
