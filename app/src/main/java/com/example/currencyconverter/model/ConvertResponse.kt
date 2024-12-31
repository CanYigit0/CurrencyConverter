package com.example.currencyconverter.model

data class ConvertResponse(
    val success: Boolean,
    val query: Query?,
    val info: Info?,
    val result: Double?
)

data class Query(
    val from: String,
    val to: String,
    val amount: Double
)

data class Info(
    val timestamp: Long,
    val rate: Double
)

