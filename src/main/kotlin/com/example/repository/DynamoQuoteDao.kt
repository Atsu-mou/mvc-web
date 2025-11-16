package com.example.repository


import com.example.model.Quote
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.Key
import java.util.UUID

class DynamoQuoteDao(
    private val enhancedClient: DynamoDbEnhancedClient,
    private val tableName: String = "quotes"
) : QuoteDao {

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
        table.putItem(item)
        return id
    }

    override fun findById(id: String): Quote? {
        val key = Key.builder().partitionValue(id).build()
        val item = table.getItem(key) ?: return null
        return itemToQuote(item)
    }

    override fun findAll(): List<Quote> {
        val results = mutableListOf<Quote>()
        val items = table.scan().items().iterator()
        while (items.hasNext()) {
            results.add(itemToQuote(items.next()))
        }
        return results
    }

    override fun update(quote: Quote): Boolean {
        val id = quote.id?.ifEmpty { return false } ?: return false
        val key = Key.builder().partitionValue(id).build()
        val existing = table.getItem(key) ?: return false
        table.putItem(quoteToItem(quote))
        return true
    }

    override fun delete(id: String): Boolean {
        val key = Key.builder().partitionValue(id).build()
        val existing = table.getItem(key) ?: return false
        table.deleteItem(key)
        return true
    }
}