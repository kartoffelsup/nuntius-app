package io.github.kartoffelsup.nuntius.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object JsonHolder {
    val json: Json by lazy { Json(JsonConfiguration.Stable) }
}