package com.example.service

import com.example.model.Quote

/**
 * Service interface for quote operations.
 * Defines the contract for business logic related to quotes.
 */
interface QuoteService {
    /**
     * Creates a new quote.
     * @param text The quote text
     * @param author The author of the quote
     * @param source Optional source/category of the quote
     * @return The created quote with generated ID
     * @throws IllegalArgumentException if the input is invalid
     */
    suspend fun createQuote(text: String, author: String, source: String? = null): Quote

    /**
     * Retrieves a quote by its ID.
     * @param id The quote ID
     * @return The quote if found, null otherwise
     */
    suspend fun getQuote(id: String): Quote?

    /**
     * Retrieves all quotes.
     * @return List of all quotes
     */
    suspend fun getAllQuotes(): List<Quote>

    /**
     * Updates an existing quote.
     * @param quote The quote to update
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if the quote is invalid
     */
    suspend fun updateQuote(quote: Quote): Boolean

    /**
     * Deletes a quote by its ID.
     * @param id The quote ID
     * @return true if the deletion was successful, false otherwise
     */
    suspend fun deleteQuote(id: String): Boolean
}
