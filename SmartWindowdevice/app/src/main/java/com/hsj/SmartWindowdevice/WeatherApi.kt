package com.hsj.SmartWindowdevice

import java.io.Serializable


data class WEATHER(
    val response: RESPONSE
)

data class RESPONSE(
    val header: HEADER,
    val body: BODY
)

data class HEADER(
    val resultCode: String,
    val resultMsg: String
)

data class BODY(
    val dataType: String,
    val items: ITEMS
)

data class ITEMS(
    val item: List<ITEM>
)

data class ITEM(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val obsrValue: String,
    val fcstValue: String,
    val pm10Value: String,
    val pm10Grade: String,
    val pm25Value: String,
    val pm25Grade: String,
)