package com.nishant.kointrack.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponseDto(
    @SerialName("result") val result: String? = null,
    @SerialName("base_code") val baseCode: String? = null,
    @SerialName("rates") val rates: Map<String, Double> = emptyMap(),
    @SerialName("time_last_update_unix") val timeLastUpdateUnix: Long? = null
)
