package com.hsj.SmartWindowdevice

data class DUST(
    val response: DUSTRESPONSE
)

data class DUSTRESPONSE(
    val body: DUSTBODY,
    val header: DUSTHEADER
)

data class DUSTHEADER(
    val resultCode: String,
    val resultMsg: String
)

data class DUSTBODY(
    val totalCount: Int,
    val items: DUSTITEMS,
    val pageNo: Int,
    val numOfRows: Int
)
data class DUSTITEMS(
    val item:List<DUSTITEM>
)

data class DUSTITEM(
    val pm10Value: String,
    val pm10Grade: String,
    val pm25Value: String,
    val pm25Grade: String,
)