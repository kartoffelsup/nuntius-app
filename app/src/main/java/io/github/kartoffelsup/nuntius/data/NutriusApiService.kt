package io.github.kartoffelsup.nuntius.data

import arrow.core.Either
import arrow.core.Left
import arrow.core.left
import arrow.core.right
import io.github.kartoffelsup.nuntius.data.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NutriusApiService {
    private val okHttpClient: OkHttpClient = OkHttpClient()

    suspend fun <A, B> post(
        path: String,
        request: A,
        requestSerializer: KSerializer<A>,
        responseSerializer: KSerializer<B>,
        credentials: String? = null
    ): Either<String, B> = request(path, "POST", request, requestSerializer, responseSerializer, credentials)

    suspend fun <B> get(
        path: String,
        responseSerializer: KSerializer<B>,
        credentials: String? = null
    ): Either<String, B> = request(path, "GET", null, null, responseSerializer, credentials)

    private suspend fun <A, B> request(
        path: String,
        method: String,
        request: A? = null,
        requestSerializer: KSerializer<A>? = null,
        responseSerializer: KSerializer<B>,
        credentials: String? = null
    ): Either<String, B> =
        withContext(Dispatchers.IO) {
            val authTokenHeader = credentials?.let { creds: String ->
                "Bearer $creds"
            }
            val call = okHttpClient.newCall(
                Request.Builder()
                    .url("http://<url>:<port>/$path")
                    .method(method, requestSerializer?.let {
                        request?.let {
                            JsonHolder.json.stringify(requestSerializer, request)
                                .toRequestBody("application/json".toMediaTypeOrNull())
                        }
                    })
                    .apply {
                        if (authTokenHeader != null) {
                            this.addHeader("Authorization", authTokenHeader)
                        }
                    }
                    .build()
            )
            val result = kotlin.runCatching { call.execute() }
            val ret: Either<String, B>? = result.getOrNull()?.let { response ->
                response.body.use { body ->
                    if (response.isSuccessful) {
                        body?.string()?.let { r ->
                            JsonHolder.json.parse(responseSerializer, r).right()
                        } ?: "No Response Body.".left()
                    } else {
                        "${response.message}: ${body?.string() ?: ""}".left()
                    }
                }
            }
            if (ret == null) {
                result.exceptionOrNull()?.printStackTrace()
                Left("Internal Server Error.")
            } else {
                ret
            }
        }
}
