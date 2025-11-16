package com.example.service

import com.example.model.Quote
import com.example.repository.QuoteDao
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class QuoteServiceTest {

    private class MockQuoteDao : QuoteDao {
        private val quotes = mutableMapOf<String, Quote>()
        private var idCounter = 1

        override fun insert(quote: Quote): String {
            val id = "test-${idCounter++}"
            quotes[id] = quote.copy(id = id)
            return id
        }

        override fun findById(id: String): Quote? = quotes[id]

        override fun findAll(): List<Quote> = quotes.values.toList()

        override fun update(quote: Quote): Boolean {
            val id = quote.id ?: return false
            if (!quotes.containsKey(id)) return false
            quotes[id] = quote
            return true
        }

        override fun delete(id: String): Boolean = quotes.remove(id) != null
    }

    @Test
    fun `createQuote should validate and create quote successfully`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        val quote = service.createQuote("Test quote", "Test Author", "Test Source")

        assertNotNull(quote.id)
        assertEquals("Test quote", quote.quoteText)
        assertEquals("Test Author", quote.author)
        assertEquals("Test Source", quote.source)
    }

    @Test
    fun `createQuote should throw exception for blank quote text`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        assertThrows<IllegalArgumentException> {
            service.createQuote("", "Test Author")
        }
    }

    @Test
    fun `createQuote should throw exception for blank author`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        assertThrows<IllegalArgumentException> {
            service.createQuote("Test quote", "")
        }
    }

    @Test
    fun `createQuote should throw exception for quote text exceeding limit`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        val longText = "a".repeat(1001)
        assertThrows<IllegalArgumentException> {
            service.createQuote(longText, "Test Author")
        }
    }

    @Test
    fun `getQuote should return quote by id`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        val created = service.createQuote("Test quote", "Test Author")
        val retrieved = service.getQuote(created.id!!)

        assertNotNull(retrieved)
        assertEquals(created.id, retrieved.id)
        assertEquals("Test quote", retrieved.quoteText)
    }

    @Test
    fun `getAllQuotes should return all quotes`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        service.createQuote("Quote 1", "Author 1")
        service.createQuote("Quote 2", "Author 2")
        service.createQuote("Quote 3", "Author 3")

        val allQuotes = service.getAllQuotes()

        assertEquals(3, allQuotes.size)
    }

    @Test
    fun `updateQuote should update existing quote successfully`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        val created = service.createQuote("Original quote", "Original Author")
        val updated = created.copy(quoteText = "Updated quote", author = "Updated Author")
        
        val result = service.updateQuote(updated)

        assertTrue(result)
        val retrieved = service.getQuote(created.id!!)
        assertEquals("Updated quote", retrieved?.quoteText)
        assertEquals("Updated Author", retrieved?.author)
    }

    @Test
    fun `deleteQuote should delete quote successfully`() = runBlocking {
        val mockDao = MockQuoteDao()
        val service = QuoteServiceImpl(mockDao)

        val created = service.createQuote("Test quote", "Test Author")
        val result = service.deleteQuote(created.id!!)

        assertTrue(result)
        assertEquals(null, service.getQuote(created.id!!))
    }
}
