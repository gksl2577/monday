package com.hsj.SmartWindowdevice

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun dateNow(): String {

    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val formatted = (currentDate.format(formatter))

    return formatted


}

fun timeNow(): String {

    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH00")
    val formatted = (currentDate.format(formatter))

//    when(formatted){
//        "100" -> return "0000"
//        "200" -> return "0100"
//        "300" -> return "0200"
//        "400" -> return "0300"
//        "500" -> return "0400"
//        "600" -> return "0500"
//        "700" -> return "0600"
//        "800" -> return "0700"
//        "900" -> return "0800"
//        "1000" -> return "0900"
//        "1100" -> return "1000"
//        "1200" -> return "1100"
//        "1300" -> return "1200"
//        "1400" -> return "1300"
//        "1500" -> return "1400"
//        "1600" -> return "1500"
//        "1700" -> return "1600"
//        "1800" -> return "1700"
//        "1900" -> return "1800"
//        "2000" -> return "1900"
//        "2100" -> return "2000"
//        "2200" -> return "2100"
//        "2300" -> return "2200"


    return formatted





}







fun main(args: Array<String>) {
    val time = timeNow()
    val date = dateNow()

    println("$date" + "$time")
}