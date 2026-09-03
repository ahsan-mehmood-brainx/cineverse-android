package com.example.cineverse.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceTest {

    @Test
    fun success_createsSuccessResource() {
        val data = "test data"
        val resource = Resource.Success(data)

        assertThat(resource).isInstanceOf(Resource.Success::class.java)
        assertThat((resource as Resource.Success).data).isEqualTo(data)
    }

    @Test
    fun error_createsErrorResource() {
        val message = "Error message"
        val throwable = Exception("Test exception")
        val resource = Resource.Error(message, throwable)

        assertThat(resource).isInstanceOf(Resource.Error::class.java)
        assertThat((resource as Resource.Error).message).isEqualTo(message)
        assertThat(resource.throwable).isEqualTo(throwable)
    }

    @Test
    fun error_createsErrorWithoutThrowable() {
        val message = "Error message"
        val resource = Resource.Error(message)

        assertThat(resource).isInstanceOf(Resource.Error::class.java)
        assertThat((resource as Resource.Error).message).isEqualTo(message)
        assertThat(resource.throwable).isNull()
    }

    @Test
    fun loading_createsLoadingResource() {
        val resource = Resource.Loading

        assertThat(resource).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun multipleLoadingInstances_areSameObject() {
        val resource1 = Resource.Loading
        val resource2 = Resource.Loading

        assertThat(resource1).isEqualTo(resource2)
    }
}
