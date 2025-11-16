package com.example.controller

import com.example.model.Quote
import com.example.repository.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuoteController(private val quoteRepository: QuoteDao) {


    suspend fun addQuote(text: String, author: String, category: String? = null): Quote =
        withContext(Dispatchers.IO) {
            val toInsert = Quote(
                id = "",
                createdDate = System.currentTimeMillis().toString(),
                createdAt = System.currentTimeMillis().toString(),
                quoteText = text,
                author = author,
                source = category
            )
            val id = quoteRepository.insert(toInsert)
            toInsert.copy(id = id)
        }

    suspend fun getQuote(id: String): Quote? =
        withContext(Dispatchers.IO) { quoteRepository.findById(id) }

    suspend fun getAllQuotes(): List<Quote> =
        withContext(Dispatchers.IO) { quoteRepository.findAll() }

    suspend fun updateQuote(quote: Quote): Boolean =
        withContext(Dispatchers.IO) { quoteRepository.update(quote) }

    suspend fun deleteQuote(id: String): Boolean =
        withContext(Dispatchers.IO) { quoteRepository.delete(id) }
}