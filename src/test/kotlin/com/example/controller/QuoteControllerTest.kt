package com.example.controller

import com.example.model.Quote
import com.example.service.QuoteService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class QuoteControllerTest {

    private lateinit var quoteService: QuoteService
    private lateinit var quoteControllerImpl: QuoteControllerImpl

    @BeforeEach
    fun setUp() {
        quoteService = mockk()
        quoteControllerImpl = QuoteControllerImpl(quoteService)
    }

    @Test
    fun `addQuote should call service and return new quote`() = runTest {
        val text = "Test quote"
        val author = "Test author"
        val expectedQuote = Quote(UUID.randomUUID().toString(), text, author, Instant.now().toString())
        coEvery { quoteService.createQuote(text, author, null) } returns expectedQuote

        val result = quoteControllerImpl.addQuote(text, author)

        assertEquals(expectedQuote, result)
        coVerify(exactly = 1) { quoteService.createQuote(text, author, null) }
    }

    @Test
    fun `getQuote should call service and return quote`() = runTest {
        val id = UUID.randomUUID().toString()
        val expectedQuote = Quote(id, "Test quote", "Test author", Instant.now().toString())
        coEvery { quoteService.getQuote(id) } returns expectedQuote

        val result = quoteControllerImpl.getQuote(id)

        assertEquals(expectedQuote, result)
        coVerify(exactly = 1) { quoteService.getQuote(id) }
    }

    @Test
    fun `getAllQuotes should call service and return all quotes`() = runTest {
        val expectedQuotes = listOf(
            Quote(UUID.randomUUID().toString(), "Quote 1", "Author 1", Instant.now().toString()),
            Quote(UUID.randomUUID().toString(), "Quote 2", "Author 2", Instant.now().toString())
        )
        coEvery { quoteService.getAllQuotes() } returns expectedQuotes

        val result = quoteControllerImpl.getAllQuotes()

        assertEquals(expectedQuotes, result)
        coVerify(exactly = 1) { quoteService.getAllQuotes() }
    }

    @Test
    fun `updateQuote should call service and return success`() = runTest {
        val quoteToUpdate = Quote(UUID.randomUUID().toString(), "Updated text", "Updated author", Instant.now().toString())
        coEvery { quoteService.updateQuote(quoteToUpdate) } returns true

        val result = quoteControllerImpl.updateQuote(quoteToUpdate)

        assertTrue(result)
        coVerify(exactly = 1) { quoteService.updateQuote(quoteToUpdate) }
    }

    @Test
    fun `deleteQuote should call service and return success`() = runTest {
        val id = UUID.randomUUID().toString()
        coEvery { quoteService.deleteQuote(id) } returns true

        val result = quoteControllerImpl.deleteQuote(id)

        assertTrue(result)
        coVerify(exactly = 1) { quoteService.deleteQuote(id) }
    }
}