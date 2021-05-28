package com.hsj.SmartWindowdevice

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_window_control.*

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
        }

        stop.setOnClickListener {
            Log.d("move", "stop")
        }

        open.setOnClickListener {
            Log.d("move", "open")
        }
    }


}
