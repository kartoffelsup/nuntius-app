package io.github.kartoffelsup.nuntius.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

val jsonx: Json by lazy { Json(JsonConfiguration.Stable) }
