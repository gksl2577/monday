package com.hsj.SmartWindowdevice

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat


import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.hsj.SmartWindowdevice.data.Repository
import com.hsj.SmartWindowdevice.data.models.airdust.DustValue
import com.hsj.SmartWindowdevice.data.models.airdust.Grade
import com.hsj.SmartWindowdevice.data.models.monitortingstation.MonitoringStation

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null

    //    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scope = MainScope()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initVariables()
        Log.d("data","${(LoginActivity.context as LoginActivity).sensordata}" )
        inside_co2.setText((LoginActivity.context as LoginActivity).sensordata)
        location_image.setOnClickListener {
            requestLocationPermission()
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
                if (LoginActivity.Companion.pdlcFlag == false) {
                    Thread {
                        try {
                            (LoginActivity.context as LoginActivity).tcp?.pdlcOn()
                            LoginActivity.Companion.pdlcFlag = true
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                }else{
                    Toast.makeText(applicationContext,"pdlc가 켜져 있습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            builder.setNegativeButton(
                "OFF"
            ) { _: DialogInterface?, _: Int ->
                Log.d("pdlcSwitch", "off")
                if (LoginActivity.Companion.pdlcFlag == true) {
                    Thread {
                        try {
                            (LoginActivity.context as LoginActivity).tcp?.pdlcOff()
                            LoginActivity.Companion.pdlcFlag = false
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                }else{
                    Toast.makeText(applicationContext,"pdlc가 꺼져 있습니다.", Toast.LENGTH_SHORT).show()
                }

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
                if (LoginActivity.Companion.windowAutoModeFlag == false) {
                    Thread {
                        try {
                            (LoginActivity.context as LoginActivity).tcp?.windowAutoModeOn()
                            LoginActivity.Companion.windowAutoModeFlag = true
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "창문 자동 모드를 사용합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                }else{
                    Toast.makeText(applicationContext,"창문 자동 모드가 켜져 있습니다.", Toast.LENGTH_SHORT).show()
                }

                Log.d("automode", "on")
            }
            builder2.setNegativeButton(
                "OFF"
            ) { _: DialogInterface?, _: Int ->
                if (LoginActivity.windowAutoModeFlag == true) {
                    Thread {
                        try {
                            (LoginActivity.context as LoginActivity).tcp?.windowAutoModeOff()
                            LoginActivity.Companion.windowAutoModeFlag = false
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "창문 자동 모드를 종료합니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                }else{
                    Toast.makeText(applicationContext,"창문 자동 모드가 꺼져 있습니다.", Toast.LENGTH_SHORT).show()
                }
                Log.d("automode", "off")

            }
            builder2.show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationPermissionGranted =
            requestCode == 100 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (!locationPermissionGranted) {
            requestLocationPermission()
        } else {
            fetchAirQualityData()
        }
    }


    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            100
        )
    }


    @SuppressLint("MissingPermission")
    private fun fetchAirQualityData() {
        cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token

        ).addOnSuccessListener { location ->
            Log.d("api", "${location.latitude},${location.longitude}}")
            scope.launch {
                val monitoringStation =
                    Repository.getNearbyMonitoringStation(location.latitude, location.longitude)
                val dustValue = Repository.getAirDustData(monitoringStation!!.stationName!!)
                val forecastValue = Repository.getVillageForecastData(
                    dateNow(),
                    timeNow(), 61.0, location.longitude
                )
                Log.d("api", "${monitoringStation?.stationName}")
                address.setText(monitoringStation?.stationName)
                Log.d("api", "${timeNow()}")
                Log.d("api", "${forecastValue}")
                temperature.setText("${forecastValue?.obsrValue}" + "℃")

                displayAirDustData(monitoringStation, dustValue!!)

            }


        }

    }

    fun displayAirDustData(moniteringStation: MonitoringStation, dustValue: DustValue) {
        // 파싱이 안되면 null값이되므로 unknown처리 해준다
        (dustValue.pm10Grade ?: Grade.UNKNOWN).let { grade ->
            outside_dust1_state.text = grade.label

        }

        (dustValue.pm25Grade ?: Grade.UNKNOWN).let { grade ->
            outside_dust2_state.text = grade.label


        }
        with(dustValue) {
            outside_dust1_value.text = "$pm10Value ㎍/㎥"

            outside_dust2_value.text = "$pm25Value ㎍/㎥"

        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }


}








