package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for creating or updating a quote.
 * 
 * @property quoteText The text content of the quote (required)
 * @property author The author of the quote (optional, defaults to "Unknown")
 * @property source The source or category of the quote (optional)
 */
@Serializable
data class QuoteRequest(
    @SerialName("quote_text")
    val quoteText: String,

    @SerialName("author")
    val author: String? = null,

    @SerialName("source")
    val source: String? = null
)
