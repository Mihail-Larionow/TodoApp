package com.michel.core.data.repository

interface TokenRepository {

    // Сохраняет токен
    fun setToken(token: String)

    // Достает токен
    fun getToken(): String?

}