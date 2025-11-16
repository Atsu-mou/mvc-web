package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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