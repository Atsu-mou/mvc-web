package com.example.repository

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

/**
 * DynamoDB entity class for storing quotes.
 * Maps to the DynamoDB table structure.
 */
@DynamoDbBean
data class DynamoQuoteItem(
    @get:DynamoDbPartitionKey
    var id: String? = null,
    var createdDate: String? = null,
    var createdAt: String? = null,
    var quoteText: String? = null,
    var author: String? = null,
    var source: String? = null
)
