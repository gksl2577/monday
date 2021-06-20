package com.hsj.SmartWindowdevice.data.models.airdust


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val dustValues: List<DustValue>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)