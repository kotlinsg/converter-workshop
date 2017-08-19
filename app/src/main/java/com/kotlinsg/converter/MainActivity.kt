package com.kotlinsg.converter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val api = CurrencyApi()

    private val from by bindView<EditText>(R.id.from)
    private val to by bindView<EditText>(R.id.to)
    private val convert by bindView<View>(R.id.convert)
    private val result by bindView<TextView>(R.id.result)
    private val progress by bindView<ProgressBar>(R.id.progress)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convert.setOnClickListener {
            val fromCur = from.text
            val toCur = to.text
            result.text = ""
            progress.visibility = View.VISIBLE
            convert(fromCur.toCurrency(), toCur.toCurrency())
        }

        result.setOnClickListener {
            openInfoPage(to.text.toCurrency())
        }
    }

    private fun convert(from: Currency, to: Currency) {
        api.convert(from, to) { rate ->
            if (rate != null) {
                result.text = getString(R.string.result, from.code, rate, to.code)
            } else {
                result.text = getString(R.string.error, from.code, to.code)
            }
            progress.visibility = View.GONE
        }
    }

    private fun openInfoPage(currency: Currency?) {
        currency ?: return
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currency.infoUri)))
    }

    private fun <T : View> bindView(@IdRes id: Int): Lazy<T> {
        return lazy { findViewById<T>(id) }
    }
}

private fun CharSequence.toCurrency() = Currency(toString())

class Currency(name: String) {
    init {
        if (name.length != 3) {
            Log.w("Currency", "Wrong currency code $name")
        }
    }

    val code = name.toUpperCase()
    val infoUri = "http://www.xe.com/currency/$code"
}