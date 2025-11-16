//package com.example.repository
//
//import com.example.model.Quote
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicLong
//import kotlin.collections.set
//
//class InMemoryQuoteDao : QuoteDao {
//    private val store = ConcurrentHashMap<Long, Quote>()
//    private val idGen = AtomicLong(1)
//
//    override fun insert(quote: Quote): Long {
//        val id = idGen.getAndIncrement()
//        val toStore = quote.copy(id = id)
//        store[id] = toStore
//        return id
//    }
//
//    override fun findById(id: Long): Quote? = store[id]
//
//    override fun findAll(): List<Quote> = store.values.sortedBy { it.id }.toList()
//
//    override fun update(quote: Quote): Boolean {
//        val id = quote.id ?: return false
//        if (!store.containsKey(id)) return false
//        store[id] = quote
//        return true
//    }
//
//    override fun delete(id: Long): Boolean = store.remove(id) != null
//}