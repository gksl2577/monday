package com.hsj.SmartWindowdevice.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.hsj.SmartWindowdevice.R
import com.hsj.SmartWindowdevice.client.SocketClient
import com.hsj.SmartWindowdevice.tcpThread
import kotlinx.android.synthetic.main.activity_server_test.*

class ServerTestActivity : AppCompatActivity() {
    var sensordata = ""
    var tcp: SocketClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_test)

        val handler = Handler{
            val tmp = it.obj.toString()
            Log.d("data","${tmp}" )
            sensordata = tmp
            Log.d("data","${sensordata}" )
            serverdata.setText("${sensordata}")
            false
        }

        tcp = SocketClient(handler)
        tcp!!.start()
    }
}