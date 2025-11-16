package com.example.repository


import com.example.model.Quote
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.Key
import java.util.UUID

/**
 * DynamoDB implementation of QuoteDao.
 * Handles persistence operations for quotes using AWS DynamoDB.
 */
class DynamoQuoteDao(
    private val enhancedClient: DynamoDbEnhancedClient,
    private val tableName: String = "quotes"
) : QuoteDao {

    private val logger = LoggerFactory.getLogger(DynamoQuoteDao::class.java)
    private val table = enhancedClient.table(tableName, TableSchema.fromClass(DynamoQuoteItem::class.java))

    private fun itemToQuote(i: DynamoQuoteItem): Quote =
        Quote(
            id = i.id ?: UUID.randomUUID().toString(),
            createdDate = i.createdDate ?: "",
            createdAt = i.createdAt ?: "",
            quoteText = i.quoteText ?: "",
            author = i.author ?: "Unknown",
            source = i.source
        )

    private fun quoteToItem(q: Quote) = DynamoQuoteItem(
        id = q.id,
        createdDate = q.createdDate,
        createdAt = q.createdAt,
        quoteText = q.quoteText,
        author = q.author,
        source = q.source
    )

    override fun insert(quote: Quote): String {
        val id = if (quote.id?.isNotBlank() == true) quote.id else UUID.randomUUID().toString()
        val item = quoteToItem(quote.copy(id = id))
        try {
            table.putItem(item)
            logger.debug("Inserted quote with ID: $id")
        } catch (e: Exception) {
            logger.error("Failed to insert quote", e)
            throw e
        }
        return id
    }

    override fun findById(id: String): Quote? {
        return try {
            val key = Key.builder().partitionValue(id).build()
            val item = table.getItem(key) ?: return null
            logger.debug("Found quote with ID: $id")
            itemToQuote(item)
        } catch (e: Exception) {
            logger.error("Failed to find quote by ID: $id", e)
            throw e
        }
    }

    override fun findAll(): List<Quote> {
        return try {
            val results = mutableListOf<Quote>()
            val items = table.scan().items().iterator()
            while (items.hasNext()) {
                results.add(itemToQuote(items.next()))
            }
            logger.debug("Retrieved ${results.size} quotes")
            results
        } catch (e: Exception) {
            logger.error("Failed to retrieve all quotes", e)
            throw e
        }
    }

    override fun update(quote: Quote): Boolean {
        val id = quote.id?.ifEmpty { return false } ?: return false
        return try {
            val key = Key.builder().partitionValue(id).build()
            val existing = table.getItem(key) ?: return false
            table.putItem(quoteToItem(quote))
            logger.debug("Updated quote with ID: $id")
            true
        } catch (e: Exception) {
            logger.error("Failed to update quote with ID: $id", e)
            throw e
        }
    }

    override fun delete(id: String): Boolean {
        return try {
            val key = Key.builder().partitionValue(id).build()
            val existing = table.getItem(key) ?: return false
            table.deleteItem(key)
            logger.debug("Deleted quote with ID: $id")
            true
        } catch (e: Exception) {
            logger.error("Failed to delete quote with ID: $id", e)
            throw e
        }
    }
}