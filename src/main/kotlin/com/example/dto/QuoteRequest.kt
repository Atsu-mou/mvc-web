package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteRequest(
    @SerialName("quote_text")
    val quoteText: String,

    @SerialName("author")
    val author: String? = null,

    @SerialName("source")
    val source: String? = null
)
