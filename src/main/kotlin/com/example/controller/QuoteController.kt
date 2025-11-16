package com.example.controller

import com.example.model.Quote
import com.example.service.QuoteService
import org.slf4j.LoggerFactory

/**
 * Controller that handles HTTP requests for quote operations.
 * Delegates business logic to the QuoteService.
 */
class QuoteController(private val quoteService: QuoteService) {
    
    private val logger = LoggerFactory.getLogger(QuoteController::class.java)

    /**
     * Adds a new quote.
     */
    suspend fun addQuote(text: String, author: String, category: String? = null): Quote {
        logger.debug("Controller: Adding new quote")
        return quoteService.createQuote(text, author, category)
    }

    /**
     * Retrieves a quote by ID.
     */
    suspend fun getQuote(id: String): Quote? {
        logger.debug("Controller: Retrieving quote with ID: $id")
        return quoteService.getQuote(id)
    }

    /**
     * Retrieves all quotes.
     */
    suspend fun getAllQuotes(): List<Quote> {
        logger.debug("Controller: Retrieving all quotes")
        return quoteService.getAllQuotes()
    }

    /**
     * Updates an existing quote.
     */
    suspend fun updateQuote(quote: Quote): Boolean {
        logger.debug("Controller: Updating quote with ID: ${quote.id}")
        return quoteService.updateQuote(quote)
    }

    /**
     * Deletes a quote by ID.
     */
    suspend fun deleteQuote(id: String): Boolean {
        logger.debug("Controller: Deleting quote with ID: $id")
        return quoteService.deleteQuote(id)
    }
}