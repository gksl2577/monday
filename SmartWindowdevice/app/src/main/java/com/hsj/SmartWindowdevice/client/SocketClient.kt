package com.hsj.SmartWindowdevice.client

import android.R
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hsj.SmartWindowdevice.tcpThread
import java.io.*
import java.net.Socket


class SocketClient(var handler: Handler) : Thread(){
    var dataInputStream: InputStream? = null
    var dataOutputStream: OutputStream? = null
    private var socket: Socket? = null
    var ip = "27.115.159.82"
    private val port = 8888
    val TAG = "TAG+Thread"
    override fun run() {
        try {
            Log.d(TAG, "접속")
            socket = Socket(ip, port)
            dataOutputStream = socket!!.getOutputStream()
            dataInputStream = socket!!.getInputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val buffer = ByteArray(1024)
        var bytes: Int
        var tmp = ""
        Log.d(TAG, "수신 시작")
        while (true) {
            try {
                Log.d(TAG, "수신 대기")
                bytes = dataInputStream!!.read(buffer)
                Log.d(TAG, "byte = $bytes")
                if (bytes > 0) {
                    tmp = String(buffer, 0, bytes)
                } else {
                    Log.d(TAG, "재접속")
                    socket = Socket(ip, port)
                    dataOutputStream = socket!!.getOutputStream()
                    dataInputStream = socket!!.getInputStream()
                }
                Log.d(TAG, tmp)
                handler.obtainMessage(0, tmp).sendToTarget()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    @Throws(IOException::class)
    fun windowAutoModeOn() {
        val inst = "automodeon".toByteArray()
        dataOutputStream!!.write(inst)
    } // 창문 자동 모드

    @Throws(IOException::class)
    fun windowAutoModeOff() {
        val inst = "automodeoff".toByteArray()
        dataOutputStream!!.write(inst)
    } // 창문 자동 모드

    @Throws(IOException::class)
    fun getSensorData() {
        val inst = "sensordata".toByteArray()
        dataOutputStream!!.write(inst)
    } // 센서 데이터 받기

    @Throws(IOException::class)
    fun pdlcOn() {
        val inst = "pdlcon".toByteArray()
        dataOutputStream!!.write(inst)
    } // pdlc on

    @Throws(IOException::class)
    fun pdlcOff() {
        val inst = "pdlcoff".toByteArray()
        dataOutputStream!!.write(inst)
    } // pdlc off

    @Throws(IOException::class)
    fun measureMotorRotationsOn() {
        val inst = "measureon".toByteArray()
        dataOutputStream!!.write(inst)
    } // 모터 회전수 측정 시작

    @Throws(IOException::class)
    fun measureMotorRotationsOff(){
        val inst = "measureoff".toByteArray()
        dataOutputStream!!.write(inst)
    } // 모터 회전수 측정값 삭제

    @Throws(IOException::class)
    fun windowOpen(){
        val inst = "windowopen".toByteArray()
        dataOutputStream!!.write(inst)
    }// 창문 열기

    @Throws(IOException::class)
    fun windowStop(){
        val inst = "windowstop".toByteArray()
        dataOutputStream!!.write(inst)
    }// 창문 동작 정지

    @Throws(IOException::class)
    fun windowClose(){
        val inst = "windowclose".toByteArray()
        dataOutputStream!!.write(inst)
    }// 창문 닫기
    @Throws(IOException::class)
    fun stationName(stationName11:String){
        val inst = "111,${stationName11}".toByteArray()
        dataOutputStream!!.write(inst)
    }// 창문 닫기

} // Tcp 소켓통신을 담당하는 클래스


