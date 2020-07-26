package io.github.kartoffelsup.nuntius.data
import io.github.kartoffelsup.nuntius.client.NuntiusApiService
import io.github.kartoffelsup.nuntius.client.NuntiusHttpClient

val nuntiusApiService = NuntiusApiService("http://127.0.0.1:8080", NuntiusHttpClient(), jsonx)
