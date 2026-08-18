package com.example.cineverse.data.remote.interceptor

import com.example.cineverse.util.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Appends the TMDB API key to every outgoing request as a query parameter. */
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlWithApiKey = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", Constants.TMDB_API_KEY)
            .build()

        return chain.proceed(originalRequest.newBuilder().url(urlWithApiKey).build())
    }
}
