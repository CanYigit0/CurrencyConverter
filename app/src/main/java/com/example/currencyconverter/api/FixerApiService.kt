package com.example.currencyconverter.api

import com.example.currencyconverter.model.CurrencyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerApiService {

    @GET("symbols")
    fun getSymbols(@Query("access_key") apiKey: String): Call<CurrencyResponse>

    // Döviz kurları verisini almak için yeni fonksiyon
    @GET("latest")
    fun getRates(@Query("access_key") apiKey: String): Call<CurrencyResponse>
}
