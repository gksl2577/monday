package com.hsj.SmartWindowdevice.data.models.villageforecast


import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("item")
    val forecastValue: List<ForecastValue>?
)