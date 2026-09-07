package com.example.cineverse.data.remote.interceptor

import com.example.cineverse.util.Constants
import com.google.common.truth.Truth.assertThat
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ApiKeyInterceptorTest {

    private val interceptor = ApiKeyInterceptor()

    private fun dummyResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    @Test
    fun intercept_appendsApiKeyQueryParameter() {
        val originalRequest = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/popular")
            .build()

        val chain: Interceptor.Chain = mock()
        whenever(chain.request()).thenReturn(originalRequest)
        val requestCaptor = argumentCaptor<Request>()
        whenever(chain.proceed(requestCaptor.capture())).thenAnswer { invocation ->
            dummyResponse(invocation.getArgument(0))
        }

        interceptor.intercept(chain)

        val proceededRequest = requestCaptor.firstValue
        assertThat(proceededRequest.url.queryParameter("api_key")).isEqualTo(Constants.TMDB_API_KEY)
    }

    @Test
    fun intercept_preservesExistingQueryParameters() {
        val originalRequest = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/popular?page=2&language=en-US")
            .build()

        val chain: Interceptor.Chain = mock()
        whenever(chain.request()).thenReturn(originalRequest)
        val requestCaptor = argumentCaptor<Request>()
        whenever(chain.proceed(requestCaptor.capture())).thenAnswer { invocation ->
            dummyResponse(invocation.getArgument(0))
        }

        interceptor.intercept(chain)

        val proceededRequest = requestCaptor.firstValue
        assertThat(proceededRequest.url.queryParameter("page")).isEqualTo("2")
        assertThat(proceededRequest.url.queryParameter("language")).isEqualTo("en-US")
        assertThat(proceededRequest.url.queryParameter("api_key")).isEqualTo(Constants.TMDB_API_KEY)
    }

    @Test
    fun intercept_returnsResponseFromChain() {
        val originalRequest = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/popular")
            .build()

        val chain: Interceptor.Chain = mock()
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(chain.proceed(any())).thenAnswer { invocation ->
            dummyResponse(invocation.getArgument(0))
        }

        val response = interceptor.intercept(chain)

        assertThat(response.code).isEqualTo(200)
        verify(chain).proceed(any())
    }
}
