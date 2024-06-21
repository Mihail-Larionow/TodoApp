package com.michel.todoapp.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
val formatter = SimpleDateFormat("dd MMM yyyy")

fun Long.toDateText(): String {
    return formatter.format(this)
}