package com.example.service

import com.example.model.Quote
import com.example.repository.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

/**
 * Implementation of QuoteService that handles business logic for quote operations.
 */
class QuoteServiceImpl(private val quoteRepository: QuoteDao) : QuoteService {
    
    private val logger = LoggerFactory.getLogger(QuoteServiceImpl::class.java)

    override suspend fun createQuote(text: String, author: String, source: String?): Quote =
        withContext(Dispatchers.IO) {
            validateQuoteText(text)
            validateAuthor(author)
            
            logger.info("Creating new quote by author: $author")
            val toInsert = Quote(
                id = "",
                createdDate = System.currentTimeMillis().toString(),
                createdAt = System.currentTimeMillis().toString(),
                quoteText = text,
                author = author,
                source = source
            )
            val id = quoteRepository.insert(toInsert)
            logger.info("Quote created successfully with ID: $id")
            toInsert.copy(id = id)
        }

    override suspend fun getQuote(id: String): Quote? =
        withContext(Dispatchers.IO) {
            require(id.isNotBlank()) { "Quote ID cannot be blank" }
            logger.debug("Retrieving quote with ID: $id")
            quoteRepository.findById(id)
        }

    override suspend fun getAllQuotes(): List<Quote> =
        withContext(Dispatchers.IO) {
            logger.debug("Retrieving all quotes")
            quoteRepository.findAll()
        }

    override suspend fun updateQuote(quote: Quote): Boolean =
        withContext(Dispatchers.IO) {
            require(quote.id?.isNotBlank() == true) { "Quote ID is required for update" }
            validateQuoteText(quote.quoteText)
            validateAuthor(quote.author)
            
            logger.info("Updating quote with ID: ${quote.id}")
            val result = quoteRepository.update(quote)
            if (result) {
                logger.info("Quote updated successfully: ${quote.id}")
            } else {
                logger.warn("Quote update failed: ${quote.id}")
            }
            result
        }

    override suspend fun deleteQuote(id: String): Boolean =
        withContext(Dispatchers.IO) {
            require(id.isNotBlank()) { "Quote ID cannot be blank" }
            logger.info("Deleting quote with ID: $id")
            val result = quoteRepository.delete(id)
            if (result) {
                logger.info("Quote deleted successfully: $id")
            } else {
                logger.warn("Quote deletion failed: $id")
            }
            result
        }

    private fun validateQuoteText(text: String) {
        require(text.isNotBlank()) { "Quote text cannot be blank" }
        require(text.length <= 1000) { "Quote text cannot exceed 1000 characters" }
    }

    private fun validateAuthor(author: String) {
        require(author.isNotBlank()) { "Author name cannot be blank" }
        require(author.length <= 200) { "Author name cannot exceed 200 characters" }
    }
}
