package com.example.currencyconverter.model

data class CurrencyResponse(
    val success: Boolean,
    val symbols: Map<String, String>,
    val rates: Map<String, Double>? = null, // rates alanını ekledik
    val error: ErrorResponse? = null // API hatalarını yakalamak için
)

data class ErrorResponse(
    val code: Int,
    val info: String
)
