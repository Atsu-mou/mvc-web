package com.example.repository

import com.example.model.Quote

interface QuoteDao {
    fun insert(quote: Quote): String
    fun findById(id: String): Quote?
    fun findAll(): List<Quote>
    fun update(quote: Quote): Boolean
    fun delete(id: String): Boolean
}