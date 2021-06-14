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
    var sensordata = ""
    var tcp: SocketClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LoginActivity.Companion.context = this


        connectBtn.setOnClickListener {
            val ipAddress = userpassword.text.toString()

            if(ipAddress != null){

                val handler = Handler{
                    val tmp = it.obj.toString()
                    Log.d("data","${tmp}" )
                    sensordata = tmp
                    Log.d("data","${sensordata}" )
//                    inside_co2.setText("${sensordata}")
                    false
                }

                tcp = SocketClient(handler)
                tcp!!.start()


                val toastSuccess = Toast.makeText(applicationContext, "연결되었습니다.", Toast.LENGTH_SHORT)
                toastSuccess.show()

                val toMain = Intent(this, MainActivity::class.java)
                startActivity(toMain)
            }else{
                val toastFail = Toast.makeText(applicationContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT)
                toastFail.show()
            }
            
        }



    }

    companion object{
        var context: Context? = null
        var pdlcFlag = false
        var windowAutoModeFlag = false
        var resetFlag = false
    }



}



