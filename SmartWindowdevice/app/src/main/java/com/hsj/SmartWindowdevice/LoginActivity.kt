package com.hsj.SmartWindowdevice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.hsj.SmartWindowdevice.client.SocketClient


import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_server_test.*
import java.io.BufferedReader




class LoginActivity : AppCompatActivity() {
    var sensordata = arrayOf<String>("0","0","0")
    var pm25data = ""
    var pm10data = ""
    var co2data = ""
    var tcp: SocketClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginActivity.Companion.context = this


        connectBtn.setOnClickListener {



                val handler = Handler{
                    val tmp = it.obj.toString()
                    Log.d("data","${tmp}" )
                    sensordata = tmp.split(",").toTypedArray()
                    Log.d("data","${sensordata[0]}" )
                    pm25data = sensordata[0]
                    pm10data = sensordata[1]
                    co2data = sensordata[2]
//                    inside_co2.setText("${sensordata}")
                    false
                }

                tcp = SocketClient(handler)
                tcp!!.start()



                val toastSuccess = Toast.makeText(applicationContext, "연결되었습니다.", Toast.LENGTH_SHORT)
                toastSuccess.show()

                val toMain = Intent(this, MainActivity::class.java)
                startActivity(toMain)

            
        }



    }

    companion object{
        var context: Context? = null
        var pdlcFlag = false
        var windowAutoModeFlag = false
        var resetFlag = false
    }



}



