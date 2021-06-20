package com.hsj.SmartWindowdevice.data.models.villageforecast


import com.google.gson.annotations.SerializedName

data class VillageForecastResponse(
    @SerializedName("response")
    val response: Response?
)