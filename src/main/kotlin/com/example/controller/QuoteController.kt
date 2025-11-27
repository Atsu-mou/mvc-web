package com.example.controller

import com.example.model.Quote

interface QuoteController {
    suspend fun addQuote(text: String, author: String, category: String? = null): Quote

    /**
     * Retrieves a quote by ID.
     */
    suspend fun getQuote(id: String): Quote?
    /**
     * Retrieves all quotes.
     */
    suspend fun getAllQuotes(): List<Quote>

    /**
     * Updates an existing quote.
     */
    suspend fun updateQuote(quote: Quote): Boolean

    /**
     * Deletes a quote by ID.
     */
    suspend fun deleteQuote(id: String): Boolean
}