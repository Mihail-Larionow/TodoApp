package com.michel.core.ui.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("SimpleDateFormat")
val formatter = SimpleDateFormat("dd MMM yyyy", Locale("ru"))

// Форматирует дату в текст
fun Long.toDateText(): String {
    return formatter.format(this)
}