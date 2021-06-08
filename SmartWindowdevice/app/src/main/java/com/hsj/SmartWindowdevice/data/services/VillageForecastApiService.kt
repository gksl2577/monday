package com.hsj.SmartWindowdevice.data.services

import com.hsj.SmartWindowdevice.BuildConfig
import com.hsj.SmartWindowdevice.data.models.villageforecast.VillageForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VillageForecastApiService {
    @GET(
        "1360000/VilageFcstInfoService/getUltraSrtNcst" +
                "?serviceKey=${BuildConfig.VILLAGE_FORECAST_API_KEY}" +
                "&numOfRows=10" +
                "&pageNo=1" +
                "&dataType=json"
    )

    suspend fun getDateNLocation(
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ):Response<VillageForecastResponse>
}