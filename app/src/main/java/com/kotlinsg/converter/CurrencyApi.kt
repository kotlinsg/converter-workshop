package com.kotlinsg.converter

import android.util.Log
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet

class CurrencyApi {
    fun convert(from: Currency, to: Currency, onComplete: (Float?) -> Unit) {
        "http://api.fixer.io/latest?base=${from.code}&symbols=${to.code}"
                .httpGet()
                .responseJson { _, _, result ->
                    val (data, error) = result
                    Log.e("API", "Result", error)
                    val rate = parseRate(data, to)
                    onComplete(rate)
                }
    }

    private fun parseRate(result: Json?, currency: Currency): Float? {
        val json = result ?: return null

        return try {
            json.obj()
                    .getJSONObject("rates")
                    .getString(currency.code)
                    .toFloat()
        } catch (e: Exception) {
            Log.e("API", "Parse error", e)
            null
        }
    }
}