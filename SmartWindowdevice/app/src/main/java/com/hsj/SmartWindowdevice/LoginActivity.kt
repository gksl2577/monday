package com.hsj.SmartWindowdevice

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hsj.SmartWindowdevice.databinding.ActivityMainBinding

import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        connectBtn.setOnClickListener {
            val password = userpassword.text.toString()

            if(password == "swd"){
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



}



