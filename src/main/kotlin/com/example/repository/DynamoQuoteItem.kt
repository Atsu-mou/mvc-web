package com.example.repository

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

/**
 * DynamoDB entity class for storing quotes.
 * Maps to the DynamoDB table structure.
 */
@DynamoDbBean
data class DynamoQuoteItem(
    var id: String? = null,
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("created_date")
    var createdDate: String? = null,
    @get:DynamoDbAttribute("created_at")
    var createdAt: String? = null,
    @get:DynamoDbAttribute("quote_text")
    var quoteText: String? = null,
    var author: String? = null,
    var source: String? = null
)
