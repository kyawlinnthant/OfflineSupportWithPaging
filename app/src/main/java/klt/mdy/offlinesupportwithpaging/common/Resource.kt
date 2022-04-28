package klt.mdy.offlinesupportwithpaging.common

import retrofit2.Response
import java.io.IOException

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T> : Resource<T>()
    class Error<T>(errorMessage: String) : Resource<T>(null, errorMessage)
    class Success<T>(data: T) : Resource<T>(data)
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body() ?: return Resource.Error("EMPTY BODY")
            Resource.Success(body)
        } else {
            Resource.Error("ERROR CODE ${response.code()} : ${response.message()}")
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: e.toString())
    } catch (e: IOException) {
        Resource.Error(e.message ?: e.toString())
    }
}