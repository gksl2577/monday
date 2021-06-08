package com.hsj.SmartWindowdevice.data

import android.util.Log
import com.hsj.SmartWindowdevice.BuildConfig
import com.hsj.SmartWindowdevice.data.models.airdust.DustValue
import com.hsj.SmartWindowdevice.data.models.monitortingstation.MonitoringStation
import com.hsj.SmartWindowdevice.data.models.villageforecast.ForecastValue
import com.hsj.SmartWindowdevice.data.services.AirKoreaApiService
import com.hsj.SmartWindowdevice.data.services.KakaoLocationApiService
import com.hsj.SmartWindowdevice.data.services.VillageForecastApiService
import com.hsj.SmartWindowdevice.dateNow
import com.hsj.SmartWindowdevice.timeNow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {



    suspend fun getNearbyMonitoringStation(
        latitude: Double,
        longitude: Double
    ): MonitoringStation? {
        val tmCoordinates = kakaoLocationApiService
            .getTmCoordinates(longitude, latitude)
            .body()
            ?.documents
            ?.firstOrNull()

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y
        Log.d("ttt", "" + tmX + " " + tmY)
        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it.tm ?: Double.MAX_VALUE }

    }

    suspend fun getAirDustData(stationName: String):DustValue?=
        airKoreaApiService
            .getRealtimeAirDust(stationName)
            .body()
            ?.response
            ?.body
            ?.dustValues
            ?. firstOrNull()

    suspend fun getVillageForecastData(
        basedate:String,
        basetime:String,

        latitude: Double,
        longitude: Double):ForecastValue?{

        val nx = latitude.toInt()
        val ny = longitude.toInt()

        return villageForecastApiService
            .getDateNLocation(basedate,basetime,nx,ny)
            .body()
            ?.response
            ?.body
            ?.items
            ?.forecastValue!![3]

    }


    private val kakaoLocationApiService: KakaoLocationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(KakaoLocationApiService::class.java)
    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(AirKoreaApiService::class.java)
    }
    private val villageForecastApiService:VillageForecastApiService by lazy{
        Retrofit.Builder()
            .baseUrl(Url.VILLAGE_FORECAST_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(VillageForecastApiService::class.java)
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }

                }
            )
            .build()
}