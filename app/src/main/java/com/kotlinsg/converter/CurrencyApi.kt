package com.kotlinsg.converter

import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet

class CurrencyApi {
    fun convert(from: Currency, to: Currency, onComplete: (Float?, Exception?) -> Unit) {
        "http://api.fixer.io/latest?base=${from.code}&symbols=${to.code}"
                .httpGet()
                .responseJson { _, _, result ->
                    val (data, error) = result
                    val rate = parseRate(data, to)
                    onComplete(rate, error?.exception)
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
            null
        }
    }
}