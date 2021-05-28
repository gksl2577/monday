package com.hsj.SmartWindowdevice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WeatherInterface {
    @GET("getUltraSrtNcst?serviceKey=ThevuCX%2FkpkGc1XL20%2BnBkCcHGzKlu1BGqslrn4js6tBeDPWhuJUvaUTXdOkWyh9KSN2SsG%2FwkSRp3IL02Twbg%3D%3D")
    fun GetWeather(
        @Query("dataType") dataType: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String,

        ): Call<WEATHER>
}

interface SkyInterface {
    @GET("getUltraSrtFcst?serviceKey=ThevuCX%2FkpkGc1XL20%2BnBkCcHGzKlu1BGqslrn4js6tBeDPWhuJUvaUTXdOkWyh9KSN2SsG%2FwkSRp3IL02Twbg%3D%3D")
    fun GetSky(
        @Query("dataType") dataType: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: String,
        @Query("base_time") base_time: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String,

        ): Call<WEATHER>
}

interface DustInterface {
//    @GET("getMsrstnAcctoRltmMesureDnsty?stationName=jjs&dataTerm=daily&pageNo=1&numOfRows=100&returnType=json&serviceKey=XndVTn3yY4Rt8uzteBr%2FfcbYDAq9LiyBJPqIth%2F5oRKh6X23URynAib7Cqj03SWYjejetd5T940tylw%2BFGjAgg%3D%3D")
    @GET("getUltraSrtNcst?serviceKey=ThevuCX%2FkpkGc1XL20%2BnBkCcHGzKlu1BGqslrn4js6tBeDPWhuJUvaUTXdOkWyh9KSN2SsG%2FwkSRp3IL02Twbg%3D%3D&numOfRows=10&pageNo=1&base_date=20210529&base_time=0100&nx=55&ny=127")
      fun GetDust(): Call<WEATHER>
//    fun GetDust(
////        @Query("stationName") stationName: String,
//        @Query("dataTerm") dataTerm: String,
//        @Query("pageNo") pageNo: Int,
//        @Query("numOfRows") numOfRows: Int,
//        @Query("returnType") returnType: String,
//        @Query("serviceKey") serviceKey: String    )

}