package com.example.repository

import com.example.model.Quote

/**
 * Data Access Object interface for quote persistence operations.
 * Defines the contract for quote repository implementations.
 */
interface QuoteDao {
    /**
     * Inserts a new quote into the repository.
     * @param quote The quote to insert
     * @return The generated ID for the inserted quote
     */
    fun insert(quote: Quote): String

    /**
     * Finds a quote by its ID.
     * @param id The quote ID
     * @return The quote if found, null otherwise
     */
    fun findById(id: String): Quote?

    /**
     * Retrieves all quotes from the repository.
     * @return List of all quotes
     */
    fun findAll(): List<Quote>

    /**
     * Updates an existing quote.
     * @param quote The quote to update
     * @return true if the update was successful, false otherwise
     */
    fun update(quote: Quote): Boolean

    /**
     * Deletes a quote by its ID.
     * @param id The quote ID
     * @return true if the deletion was successful, false otherwise
     */
    fun delete(id: String): Boolean
}