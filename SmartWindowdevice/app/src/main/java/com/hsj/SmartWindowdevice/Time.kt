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

    return formatted


}
fun timeNowForSKY(): String{
    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH00")
    val formatted = (currentDate.format(formatter)).toInt()
    if(formatted == 0){
        return "2300"
    }
//    else if(formatted == 100){
//        return "2100"
//    }else if(formatted == 200){
//        return "2200"
//    }else if(formatted == 300){
//        return "2300"
//    }
    else {
        val ago = formatted - 100
        return ago.toString()
    }
}






fun main(args: Array<String>) {
    val time = timeNow()
    val date = dateNow()

    println("$date" + "$time")
}