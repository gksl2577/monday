package com.hsj.SmartWindowdevice

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_window_control.*
import java.io.IOException

class WindowControlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_control)

        backarrow.setOnClickListener {
            finish()
        }

        resetBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("초기화")
            builder.setMessage("모터 회전 수 정보를 삭제 하시겠습니까?")
            builder.setPositiveButton(
                "예"
            ) { _: DialogInterface?, _: Int ->
                Log.d("reset", "yes")
                Thread {
                    try {
                        (LoginActivity.context as LoginActivity).tcp?.resetMotorRotations()
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "모터 회전수를 초기화합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }.start()

            }
            builder.setNegativeButton(
                "아니오"
            ) { _: DialogInterface?, _: Int ->
                Log.d("reset", "no")

            }
            builder.show()
        }

        measurelengthBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("모터 회전수 측정")
            builder.setMessage("모터 회전 수 정보를 수집 하시겠습니까?")
            builder.setPositiveButton(
                "예"
            ) { _: DialogInterface?, _: Int ->
                Log.d("measure", "yes")
                Thread {
                    try {
                        (LoginActivity.context as LoginActivity).tcp?.measureMotorRotations()
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "모터 회전수를 측정합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }.start()
            }
            builder.setNegativeButton(
                "아니오"
            ) { _: DialogInterface?, _: Int ->
                Log.d("measure", "no")

            }
            builder.show()
        }

        close.setOnClickListener {
            Log.d("move", "close")
            Thread {
                try {
                    (LoginActivity.context as LoginActivity).tcp?.windowClose()
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "창문을 닫습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }.start()
        }

        stop.setOnClickListener {
            Log.d("move", "stop")
            Thread {
                try {
                    (LoginActivity.context as LoginActivity).tcp?.windowStop()
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "창문 동작을 멈춥니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }.start()
        }

        open.setOnClickListener {
            Log.d("move", "open")
            Thread {
                try {
                    (LoginActivity.context as LoginActivity).tcp?.windowOpen()
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "창문을 엽니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }.start()
        }
    }


}
