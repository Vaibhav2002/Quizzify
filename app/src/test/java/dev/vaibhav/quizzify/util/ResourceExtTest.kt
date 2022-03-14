package dev.vaibhav.quizzify.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResourceExtTest {

    @Test
    fun `messages are mapped correctly when success`() = runTest {
        var successResource: Resource<Unit> = Resource.Success(data = Unit)
        val successMessage = "Resource is success"
        successResource = successResource.mapMessages(successMessage)
        assertThat(successResource.message).isEqualTo(successMessage)
    }

    @Test
    fun `messages are mapped correctly when error`() = runTest {
        var errorResource: Resource<Unit> = Resource.Error()
        val errorMessage = "Resource is error"
        errorResource = errorResource.mapMessages(errorMessage = errorMessage)
        assertThat(errorResource.message).isEqualTo(errorMessage)
    }
}