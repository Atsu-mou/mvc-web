// kotlin
package com.example.repository

import com.example.model.Quote
import java.sql.ResultSet

fun ResultSet.toQuote(): Quote {
    val id = getLong("id")
    val text = getString("text")
    val author = getString("author")
    val category = getString("category") // returns null if DB NULL
    return Quote(
        id = id.toString(),
        createdDate = getString("created_date"),
        createdAt = getString("created_at"),
        quoteText = text,
        author = author,
        source = category
    )
}
