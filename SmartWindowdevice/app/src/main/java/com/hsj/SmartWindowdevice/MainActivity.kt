package com.hsj.SmartWindowdevice

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val num_of_rows = 10

val page_no = 1
val data_type = "JSON"
val base_time = "1828"
val base_time_sky = timeNowForSKY()
val base_date = dateNow()
val nx = "61"
val ny = "127"


val num_of_rows_dust = 100
val data_term = "DAILY"
val station_name = "종로구"
val key =
    "XndVTn3yY4Rt8uzteBr%2FfcbYDAq9LiyBJPqIth%2F5oRKh6X23URynAib7Cqj03SWYjejetd5T940tylw%2BFGjAgg%3D%3D"
private val retrofitDust = Retrofit.Builder()
//    .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object dustApiObject {
    val retrofitDustService: DustInterface by lazy {
        retrofitDust.create(DustInterface::class.java)
    }
}

private val retrofitWeather = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()


object weatherApiObject {
    val retrofitWeatherService: WeatherInterface by lazy {
        retrofitWeather.create(WeatherInterface::class.java)
    }
}

object skyApiObject {
    val retrofitSkyService: SkyInterface by lazy {
        retrofitWeather.create(SkyInterface::class.java)
    }
}



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        location_image.setOnClickListener {
            val location1PermissionCheck = ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION

            )
            val location2PermissionCheck = ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION

            )

            PackageManager.PERMISSION_GRANTED
            Log.d("permission", "ok")

            // 권한 허용 확인
            if ((location1PermissionCheck != PackageManager.PERMISSION_GRANTED) || (location2PermissionCheck != PackageManager.PERMISSION_GRANTED)) {

                // 권한이 없는 경우
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1000
                )


                Log.d("permission", "ok2")
            } else {
                // 권한이 이미 있는 경우
                Log.d("permission", "ok")

                // 주소 출력
                address.setText("동대문구 휘경동")

                val callSky = skyApiObject.retrofitSkyService.GetSky(
                    data_type, num_of_rows, page_no,
                    base_date, base_time, nx, ny
                )
                callSky.enqueue(object : retrofit2.Callback<WEATHER> {
                    override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                        if (response.isSuccessful) {
                            Log.d("apisky", response.body().toString())
                            if (response.body() != null) {

                                if (response.body()!!.response.body.items.item[3].fcstValue == "1") {
                                    weatherNow_image.setImageResource(R.drawable.sun_day)
                                } else if (response.body()!!.response.body.items.item[3].fcstValue == "3") {
                                    weatherNow_image.setImageResource(R.drawable.sun_cloudy)
                                } else {
                                    weatherNow_image.setImageResource(R.drawable.cloudyyy)

                                }
                            }
                        } else {
                            weatherNow_image.setImageResource(R.drawable.sun_day)

                        }
                    }

                    override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                        Log.d("apisky", "fail")
                    }
                })
                // 온도 정보
                val callWeather = weatherApiObject.retrofitWeatherService.GetWeather(
                    data_type, num_of_rows, page_no,
                    base_date, base_time_sky, nx, ny
                )
                Log.d("timeNow", "time : " + timeNow())
                callWeather.enqueue(object : retrofit2.Callback<WEATHER> {


                    override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                        Log.d("api", "ok22")
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                temperature.setText(response.body()!!.response.body.items.item[3].obsrValue + "℃")

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "정보를 불러오는 도중 문제가 발생하였습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.d("api", response.body().toString())
                        }
                    }

                    override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                        Log.d("api", "fail")

                    }
                })

                val callDust = dustApiObject.retrofitDustService.GetDust(
//                    data_term, page_no, num_of_rows_dust, data_type, key
                )
                callDust.enqueue(object : retrofit2.Callback<WEATHER>{

                    override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                        if (response.isSuccessful) {
                            Log.d("apidust", response.body().toString())
                        }
                    }

                    override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                        Log.d("apidust", "fail")
                    }
                    //                    override fun onResponse(call: Call<DUST>, response: Response<DUST>) {
//                        Log.d("api", "ok22")
//                        if (response.isSuccessful) {
//                            Log.d("apidust", response.body().toString())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<DUST>, t: Throwable) {
//                        Log.d("apidust", "fail")
//                    }
                })


            }
        }

        // 창문 수동 모드 버튼
        manualmode.setOnClickListener {
            val intentControl = Intent(this, WindowControlActivity::class.java)
            startActivity(intentControl)
        }


        // PDLC 제어 버튼
        pdlcswitch.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("PDLC 필름 제어")
            builder.setMessage("PDLC 필름을 ON/OFF 하시겠습니까?")
            builder.setPositiveButton(
                "ON"
            ) { _: DialogInterface?, _: Int ->
                Log.d("pdlcSwitch", "on")
            }
            builder.setNegativeButton(
                "OFF"
            ) { _: DialogInterface?, _: Int ->
                Log.d("pdlcSwitch", "off")

            }
            builder.show()
        }


        // 창문 자동 모드 버튼튼
        automode.setOnClickListener {
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("창문 자동 모드")
            builder2.setMessage("창문 자동 모드를 사용하시겠습니까?")
            builder2.setPositiveButton(
                "ON"
            ) { _: DialogInterface?, _: Int ->
                Toast.makeText(applicationContext, "창문 자동 모드를 사용합니다.", Toast.LENGTH_SHORT).show()
                Log.d("automode", "on")
            }
            builder2.setNegativeButton(
                "OFF"
            ) { _: DialogInterface?, _: Int ->
                Toast.makeText(applicationContext, "창문 자동 모드를 중지합니다.", Toast.LENGTH_SHORT).show()

                Log.d("automode", "off")

            }
            builder2.show()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 승낙
                Log.d("permissions", "승낙")
            } else {
                //거부
                Log.d("permissions", "거부")
            }
        }
    }


}








