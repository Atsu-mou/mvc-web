package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Domain model representing a quote.
 * 
 * @property id Unique identifier for the quote
 * @property createdDate Creation date timestamp
 * @property createdAt Creation time timestamp
 * @property quoteText The actual text of the quote
 * @property author The author of the quote
 * @property source Optional source or category of the quote
 */
@Serializable
data class Quote(
    @SerialName("id")
    val id: String? = null,

    @SerialName("created_date")
    val createdDate: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("quote_text")
    val quoteText: String,

    @SerialName("author")
    val author: String = "Unknown",

    @SerialName("source")
    val source: String? = null
)