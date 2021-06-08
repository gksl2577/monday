package com.hsj.SmartWindowdevice.data.services

import androidx.core.provider.FontsContractCompat
import com.hsj.SmartWindowdevice.BuildConfig
import com.hsj.SmartWindowdevice.data.models.airdust.AirDustResponse
import com.hsj.SmartWindowdevice.data.models.monitortingstation.MonitoringStationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {
    @GET(
        "B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json"
    )
    suspend fun getNearbyMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>


    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"+
    "?serviceKey=XndVTn3yY4Rt8uzteBr%2FfcbYDAq9LiyBJPqIth%2F5oRKh6X23URynAib7Cqj03SWYjejetd5T940tylw%2BFGjAgg%3D%3D"+
    "&returnType=json"+
    "&dataTerm=DAILY"+
    "&ver=1.3")
    suspend fun getRealtimeAirDust(

        @Query("stationName") stationName:String
    ):Response<AirDustResponse>
}