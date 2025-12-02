package com.biblioteca.app.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LibrosRetrofitClient {
    
    // backend desplegado en render
    private const val URL_BASE = "https://booksy-backend-jkte.onrender.com/"
    
    // variable para guardar el token
    private var tokenActual: String? = null
    
    fun setToken(token: String?) {
        tokenActual = token
    }
    
    fun getToken(): String? = tokenActual
    
    // interceptor para agregar el token a las peticiones
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        tokenActual?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val servicioLibros: LibrosApiService by lazy {
        retrofit.create(LibrosApiService::class.java)
    }
    
    val servicioAuth: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
