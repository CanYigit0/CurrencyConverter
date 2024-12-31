package com.example.currencyconverter

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.api.RetrofitClient
import com.example.currencyconverter.model.CurrencyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val apiKey = "b54547f604a01f94c40e4b1c4fab8329"

    // Spinner'lar
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var amountEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Spinner'ları ve EditText'i bul
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        amountEditText = findViewById(R.id.editTextAmount)

        fetchCurrencies()

        // Convert butonuna tıklandığında dönüşüm yap
        findViewById<Button>(R.id.btnConvert).setOnClickListener {
            val fromCurrency = spinnerFrom.selectedItem.toString()
            val toCurrency = spinnerTo.selectedItem.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 1.0
            manualCurrencyConversion(fromCurrency, toCurrency, amount)
        }
    }

    // Kurları API'den al
    private fun fetchCurrencies() {
        val apiService = RetrofitClient.instance
        apiService.getSymbols(apiKey).enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val symbols = response.body()?.symbols
                    val currencyList = symbols?.keys?.toList() ?: emptyList()
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, currencyList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinnerFrom.adapter = adapter
                    spinnerTo.adapter = adapter
                } else {
                    val errorInfo = response.body()?.error?.info ?: "Bilinmeyen hata"
                    Log.e("MainActivity", "API başarısız: $errorInfo")
                    Toast.makeText(this@MainActivity, "API başarısız: $errorInfo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                Log.e("MainActivity", "Bağlantı hatası: ${t.message}")
                Toast.makeText(this@MainActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Manuel dönüşüm hesaplaması
    private fun manualCurrencyConversion(fromCurrency: String, toCurrency: String, amount: Double) {
        val apiService = RetrofitClient.instance
        apiService.getRates(apiKey).enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    val rates = response.body()?.rates
                    val fromRate = rates?.get(fromCurrency)
                    val toRate = rates?.get(toCurrency)
                    if (fromRate != null && toRate != null) {
                        val result = amount * (toRate / fromRate)
                        // Sonuç virgülden sonra iki basamağa yuvarlanır
                        val formattedResult = String.format("%.2f", result)
                        Toast.makeText(this@MainActivity, "Sonuç: $formattedResult", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Kurlar bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorInfo = response.body()?.error?.info ?: "Bilinmeyen hata"
                    Log.e("MainActivity", "Kurlar alınamadı: $errorInfo")
                    Toast.makeText(this@MainActivity, "Kurlar alınamadı: $errorInfo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                Log.e("MainActivity", "Bağlantı hatası: ${t.message}")
                Toast.makeText(this@MainActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
