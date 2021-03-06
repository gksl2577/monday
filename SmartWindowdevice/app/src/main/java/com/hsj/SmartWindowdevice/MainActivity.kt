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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.concurrent.thread


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
        requestLocationPermission()
        Log.d("data22", "${(LoginActivity.context as LoginActivity).pm25data}")
        Log.d("data22", "${(LoginActivity.context as LoginActivity).pm10data}")
        Log.d("data22", "${(LoginActivity.context as LoginActivity).co2data}")


        location_image.setOnClickListener {

            Thread {
                try {
                    (LoginActivity.context as LoginActivity).tcp?.stationName(address.text.toString())
                    Log.d("station11", "${address.text}")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
            Log.d("station", "${address.text}")


        }





////

        // ?????? ?????? ?????? ??????
        manualmode.setOnClickListener {
            val intentControl = Intent(this, WindowControlActivity::class.java)
            startActivity(intentControl)
        }


        // PDLC ?????? ??????
        pdlcswitch.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("PDLC ?????? ??????")
            builder.setMessage("PDLC ????????? ON/OFF ???????????????????")
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

                } else {
                    Toast.makeText(applicationContext, "pdlc??? ?????? ????????????.", Toast.LENGTH_SHORT).show()
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

                } else {
                    Toast.makeText(applicationContext, "pdlc??? ?????? ????????????.", Toast.LENGTH_SHORT).show()
                }

            }
            builder.show()
        }


        // ?????? ?????? ?????? ?????????
        automode.setOnClickListener {
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("?????? ?????? ??????")
            builder2.setMessage("?????? ?????? ????????? ?????????????????????????")
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
                                    "?????? ?????? ????????? ???????????????.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                } else {
                    Toast.makeText(applicationContext, "?????? ?????? ????????? ?????? ????????????.", Toast.LENGTH_SHORT)
                        .show()
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
                                    "?????? ?????? ????????? ???????????????.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }.start()

                } else {
                    Toast.makeText(applicationContext, "?????? ?????? ????????? ?????? ????????????.", Toast.LENGTH_SHORT)
                        .show()
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
                    timeNow(), 62.1, location.longitude
                )

                Log.d("api", "${monitoringStation?.stationName}")
                address.setText(monitoringStation?.stationName)

                Log.d("api", "${timeNow()}")
                Log.d("api", "${forecastValue}")
                if(forecastValue?.obsrValue != null)
                {
                    temperature.setText("${forecastValue?.obsrValue}" + "???")

                }else{
                    temperature.setText("?????????")
                }


                displayAirDustData(monitoringStation, dustValue!!)

                inside_co2.setText("${(LoginActivity.context as LoginActivity).sensordata[2]}" + "ppm")
                fineDustDisplay((LoginActivity.context as LoginActivity).sensordata[1].toInt())
                ultraFineDustDisplay((LoginActivity.context as LoginActivity).sensordata[0].toInt())


            }


        }

    }

    fun fineDustDisplay(sensordata: Int) {
        if (sensordata >= 0 && sensordata <= 30) {
            inside_dust1_state.setText("??????")
            inside_dust1_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 31 && sensordata <= 80) {
            inside_dust1_state.setText("??????")
            inside_dust1_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 81 && sensordata <= 150) {
            inside_dust1_state.setText("??????")
            inside_dust1_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 151) {
            inside_dust1_state.setText("????????????")
            inside_dust1_value.setText("${sensordata}" + "???/")
        }
    }

    fun ultraFineDustDisplay(sensordata: Int) {
        if (sensordata >= 0 && sensordata <= 15) {
            inside_dust2_state.setText("??????")
            inside_dust2_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 16 && sensordata <= 35) {
            inside_dust2_state.setText("??????")
            inside_dust2_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 36 && sensordata <= 75) {
            inside_dust2_state.setText("??????")
            inside_dust2_value.setText("${sensordata}" + "???/")
        } else if (sensordata >= 76) {
            inside_dust2_state.setText("????????????")
            inside_dust2_value.setText("${sensordata}" + "???/")
        }
    }

    fun displayAirDustData(moniteringStation: MonitoringStation, dustValue: DustValue) {
        // ????????? ????????? null??????????????? unknown?????? ?????????
        (dustValue.pm10Grade ?: Grade.UNKNOWN).let { grade ->
            outside_dust1_state.text = grade.label

        }

        (dustValue.pm25Grade ?: Grade.UNKNOWN).let { grade ->
            outside_dust2_state.text = grade.label


        }
        with(dustValue) {
            outside_dust1_value.text = "$pm10Value ???/???"

            outside_dust2_value.text = "$pm25Value ???/???"

        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }


}








