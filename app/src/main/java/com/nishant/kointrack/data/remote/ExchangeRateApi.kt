package com.nishant.kointrack.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(
        @Path("base") base: String
    ): ExchangeRateResponseDto
}
