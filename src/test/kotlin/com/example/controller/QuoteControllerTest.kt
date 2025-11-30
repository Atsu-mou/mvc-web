package com.example.controller

import com.example.model.Quote
import com.example.service.QuoteService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows
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
    fun `addQuote should throw IllegalArgumentException when service throws`() = runTest {
        val text = "Test quote"
        val author = "Test author"
        val errorMessage = "Invalid quote data"

        // Mock the service to throw an exception
        coEvery {
            quoteService.createQuote(text, author, null)
        } throws IllegalArgumentException(errorMessage)

        // Assert that calling the controller method throws the expected exception
        val exception = assertThrows<IllegalArgumentException> {
            quoteControllerImpl.addQuote(text, author, null)
        }

        // Assert that the exception message is correct
        assertEquals(errorMessage, exception.message)

        // Verify that the service method was called
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
    fun `getQuote should call service and return null`() = runTest {
        val id = UUID.randomUUID().toString()
        coEvery { quoteService.getQuote(id) } returns null

        val result = quoteControllerImpl.getQuote(id)

        assertNull(result)
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
    fun `updateQuote should call service and return IllegalArgumentException`() = runTest {
        val text = "Test quote"
        val author = "Test author"
        val errorMessage = "Invalid quote data"

        val quoteToUpdate = Quote(UUID.randomUUID().toString(), "Updated text", "Updated author", Instant.now().toString())
        coEvery { quoteService.updateQuote(quoteToUpdate) } throws IllegalArgumentException(errorMessage)

        val exception = assertThrows<IllegalArgumentException> {
            quoteControllerImpl.updateQuote(quoteToUpdate)
        }

        assertEquals(errorMessage, exception.message)
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

    @Test
    fun `deleteQuote should call service and return false`() = runTest {
        val id = UUID.randomUUID().toString()
        coEvery { quoteService.deleteQuote(id) } returns false

        val result = quoteControllerImpl.deleteQuote(id)

        assertFalse(result)
        coVerify(exactly = 1) { quoteService.deleteQuote(id) }
    }
}