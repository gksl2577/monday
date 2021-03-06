package com.hsj.SmartWindowdevice.data.models.villageforecast


import com.google.gson.annotations.SerializedName

data class ForecastValue(
    @SerializedName("baseDate")
    val baseDate: String?,
    @SerializedName("baseTime")
    val baseTime: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("nx")
    val nx: Int?,
    @SerializedName("ny")
    val ny: Int?,
    @SerializedName("obsrValue")
    val obsrValue: String?
)