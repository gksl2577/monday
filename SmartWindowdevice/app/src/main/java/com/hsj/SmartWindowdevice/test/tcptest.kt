package com.hsj.SmartWindowdevice.test//package com.hsj.SmartWindowdevice.test//package com.hsj.SmartWindowdevice
//
//import android.R
//import android.annotation.SuppressLint
//import android.app.*
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.MotionEvent
//import android.view.View
//import android.webkit.WebSettings
//import android.webkit.WebView
//import android.widget.ImageButton
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.NotificationCompat
//import androidx.core.content.ContextCompat
//import java.io.IOException
//
//
//class tcpThread(var handler: Handler) : Thread() {
//    var dataInputStream: InputStream? = null
//    var dataOutputStream: OutputStream? = null
//    private var socket: Socket? = null
//    var ip = "121.153.150.157"
//    private val port = 9999
//    val TAG = "TAG+Thread"
//    override fun run() {
//        try {
//            Log.d(TAG, "접속")
//            socket = Socket(ip, port)
//            dataOutputStream = socket!!.getOutputStream()
//            dataInputStream = socket!!.getInputStream()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        val buffer = ByteArray(1024)
//        var bytes: Int
//        var tmp = ""
//        Log.d(TAG, "수신 시작")
//        while (true) {
//            try {
//                Log.d(TAG, "수신 대기")
//                bytes = dataInputStream!!.read(buffer)
//                Log.d(TAG, "byte = $bytes")
//                if (bytes > 0) {
//                    tmp = String(buffer, 0, bytes)
//                } else {
//                    Log.d(TAG, "재접속")
//                    socket = Socket(ip, port)
//                    dataOutputStream = socket!!.getOutputStream()
//                    dataInputStream = socket!!.getInputStream()
//                }
//                Log.d(TAG, tmp)
//                handler.obtainMessage(0, tmp).sendToTarget()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    fun cctv_center() {
//        val inst = "center".toByteArray()
//        dataOutputStream!!.write(inst)
//    } // cctv 중앙으로 명령
//
//    @Throws(IOException::class)
//    fun cctv_right() {
//        val inst = "right".toByteArray()
//        dataOutputStream!!.write(inst)
//    } // cctv 오른쪽으로 명령
//
//    @Throws(IOException::class)
//    fun cctv_left() {
//        val inst = "left".toByteArray()
//        dataOutputStream!!.write(inst)
//    } // cctv 왼쪽으로 명령
//
//    @Throws(IOException::class)
//    fun cctvOn() {
//        val inst = "cctvOn".toByteArray()
//        dataOutputStream!!.write(inst)
//    } // CCTV On 명령
//
//    @Throws(IOException::class)
//    fun cctvOff() {
//        val inst = "cctvOff".toByteArray()
//        dataOutputStream!!.write(inst)
//    } // CCTV Off 명령
//} // Tcp 소켓통신을 담당하는 클래스
////
////
//class cctvActivity() : AppCompatActivity() {
//    val TAG = "TAG+CCTVFragment"
//    var cctvOnButton: Button? = null
//    var cctvOffButton: Button? = null
//    var cctvCenterButton: Button? = null
//    var cctvLeftButton: Button? = null
//    var cctvRightButton: Button? = null
//    var webView: WebView? = null
//    var webSettings: WebSettings? = null
//    var callText: TextView? = null
//
//    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_cctv)
//        Log.d(TAG, "Create CCTV Fragment")
//        webView = findViewById<View>(R.id.cctvWeb) as WebView
//        callText = findViewById<View>(R.id.callText) as TextView
//        cctvOnButton = findViewById<View>(R.id.cctvOnButton) as Button
//        cctvOffButton = findViewById<View>(R.id.cctvOffButton) as Button
//        cctvCenterButton = findViewById<View>(R.id.cctvCenterButton) as Button
//        cctvLeftButton = findViewById<View>(R.id.cctvLeftButton) as Button
//        cctvRightButton = findViewById<View>(R.id.cctvRightButton) as Button
//        webSettings = webView!!.settings
//        webSettings!!.javaScriptEnabled = true
//        webView!!.loadData(
//            "<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} " +
//                    "img{width:100%25;} div{overflow: hidden;} </style></head>" +
//                    "<body><div><img src='http://" + (MainActivity.context as MainActivity).tcpThread.ip + ":8082/'/></div></body></html>",
//            "text/html", "UTF-8"
//        )
//        // WebView 에 CCTV 화면 띄움
//        webView!!.setOnTouchListener(object : OnTouchListener() {
//            fun onTouch(v: View?, event: MotionEvent): Boolean {
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    webView!!.reload()
//                }
//                return true
//            }
//        }) // WebView 터치 시 새로고침
//        cctvCenterButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Thread {
//                    try {
//                        (MainActivity.context as MainActivity).tcpThread.cctv_center()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }) // Center 버튼 클릭 시 cctv 위치 중앙으로
//        cctvLeftButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Thread {
//                    try {
//                        (MainActivity.context as MainActivity).tcpThread.cctv_left()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }) // left 버튼 클릭 시 cctv 위치 왼쪽으로
//        cctvRightButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Thread {
//                    try {
//                        (MainActivity.context as MainActivity).tcpThread.cctv_right()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }) // Right 버튼 클릭 시 cctv 위치 오른쪽으로
//        cctvOnButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Thread {
//                    try {
//                        (MainActivity.context as MainActivity).tcpThread.cctvOn()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }) // On 버튼 클릭 시 cctv On 명령어 전송
//        cctvOffButton.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Thread {
//                    try {
//                        (MainActivity.context as MainActivity).tcpThread.cctvOff()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }.start()
//            }
//        }) // Off 버튼 클릭 시 cctv Off 명령어 전송
//        callText!!.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                val builder: AlertDialog.Builder = Builder(this@cctvActivity)
//                builder.setTitle("신고")
//                builder.setMessage("신고하시겠습니까?")
//                builder.setPositiveButton("Yes",
//                    DialogInterface.OnClickListener { dialog, which ->
//                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
//                        startActivity(intent)
//                    })
//                builder.setNegativeButton("No",
//                    DialogInterface.OnClickListener { dialog, which -> })
//                val alertDialog: AlertDialog = builder.create()
//                alertDialog.show()
//            }
//        }) // 신고하기 버튼 클릭 시 112 전화걸기로 이동
//    }
//}
//
//
//class MainActivity : AppCompatActivity() {
//    val TAG = "TAG+MainActivity"
//    var sensorData = arrayOf("0", "0", "0", "0")
//    var tcpThread: tcpThread? = null
//    var dustText: TextView? = null
//    var tempText: TextView? = null
//    var humText: TextView? = null
//    var lightButton: ImageButton? = null
//    var cctvButton: ImageButton? = null
//    var alarmButton: ImageButton? = null
//    var detectModeButton: ImageButton? = null
//    var alarmManager: AlarmManager? = null
//    var alarmIntent: Intent? = null
//    var alarmPendingIntent: PendingIntent? = null
//    var alarmCallPendingIntent: PendingIntent? = null
//    var detectPendingIntent: PendingIntent? = null
//    var dataPendingIntent: PendingIntent? = null
//    var alarmNotification: NotificationManager? = null
//    var detectNotification: NotificationManager? = null
//    var dataNotification: NotificationManager? = null
//    var alarmHour = 0
//    var alarmMinute = 0
//    var alarmCalendar: Calendar? = null
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(TAG, "onCreate")
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        MainActivity.Companion.context = this
//        dustText = findViewById<View>(R.id.dustText) as TextView
//        tempText = findViewById<View>(R.id.tempText) as TextView
//        humText = findViewById<View>(R.id.humText) as TextView
//        cctvButton = findViewById<View>(R.id.cctvButton) as ImageButton
//        alarmButton = findViewById<View>(R.id.alarmButton) as ImageButton
//        detectModeButton = findViewById<View>(R.id.detectModeButton) as ImageButton
//        val builder = NotificationCompat.Builder(applicationContext, "default")
//        val intent = Intent(applicationContext, notificationBroadcast::class.java)
//        dataPendingIntent = PendingIntent.getBroadcast(
//            applicationContext,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        builder.setSmallIcon(R.drawable.applogo)
//        builder.setContentTitle("실내 환경 데이터")
//        builder.setContentIntent(dataPendingIntent)
//        builder.setContentText("온도 = 0 습도 = 0 미세먼지 = ")
//        dataNotification =
//            getSystemService<Any>(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            dataNotification!!.createNotificationChannel(
//                NotificationChannel(
//                    "default",
//                    "SensorData",
//                    NotificationManager.IMPORTANCE_LOW
//                )
//            )
//        }
//        dataNotification!!.notify(0, builder.build())
//        // 센서 데이터 Notification 설정
//        val handler = Handler { msg ->
//            val tmp = msg.obj.toString()
//            sensorData = tmp.split(",").toTypedArray()
//            tempText!!.text = sensorData[0] + " ℃"
//            humText!!.text = sensorData[1] + " %"
//            val dust = sensorData[2].toInt()
//            if (dust >= 0 && dust <= 30) {
//                dustText.setTextColor(Color.BLUE)
//                dustText!!.text = """좋음
//(${sensorData[2]} ㎍/㎥)"""
//                builder.setContentText("온도 = " + sensorData[0] + " ℃ 습도 = " + sensorData[1] + " % 미세먼지 농도 = 좋음 (" + sensorData[2] + "㎍/㎥)")
//            } else if (dust >= 31 && dust <= 80) {
//                dustText.setTextColor(Color.GREEN)
//                dustText!!.text = """보통
//(${sensorData[2]} ㎍/㎥)"""
//                builder.setContentText("온도 = " + sensorData[0] + " ℃ 습도 = " + sensorData[1] + " % 미세먼지 농도 = 보통 (" + sensorData[2] + "㎍/㎥)")
//            } else if (dust >= 81 && dust <= 150) {
//                dustText.setTextColor(Color.parseColor("#FF7F00"))
//                dustText!!.text = """나쁨
//(${sensorData[2]} ㎍/㎥)"""
//                builder.setContentText("온도 = " + sensorData[0] + " ℃ 습도 = " + sensorData[1] + " % 미세먼지 농도 = 나쁨 (" + sensorData[2] + "㎍/㎥)")
//            } else if (dust >= 151) {
//                dustText.setTextColor(Color.RED)
//                dustText!!.text = """매우나쁨
//(${sensorData[2]} ㎍/㎥)"""
//                builder.setContentText("온도 = " + sensorData[0] + " ℃ 습도 = " + sensorData[1] + " % 미세먼지 농도 = 매우나쁨 (" + sensorData[2] + "㎍/㎥)")
//            } // 미세먼지 등급에 따라 등급과 글자색 변경
//            if (sensorData[3] == "1" && MainActivity.Companion.detectModeActive == true) {
//                detectModeButton!!.setBackgroundResource(R.drawable.oval)
//                MainActivity.Companion.detectModeActive = false
//                showDetectNotify()
//            } // 감시모드가 활성화 되어 있을 때 sensorData[3] 값이 1이면
//            builder.setWhen(System.currentTimeMillis()) // Notification의 시간을 실시간으로 설정
//            dataNotification!!.notify(0, builder.build())
//            false
//        } // tcpThread 클래스로부터 넘어오는 값을 받는 Handler
//
//        tcpThread = tcpThread(handler)
//        tcpThread!!.start()
//        // tcp 소켓 통신 수신 쓰레드
//        cctvButton!!.setOnClickListener {
//            val intent = Intent(applicationContext, cctvActivity::class.java)
//            startActivity(intent)
//        } // CCTV 확인 버튼 클릭 리스너
//        alarmButton!!.setOnClickListener {
//            if (MainActivity.Companion.alarmActive == false) {
//                val timePickerDialog =
//                    TimePickerDialog(
//                        this@MainActivity, TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,
//                        { view, hourOfDay, minute ->
//                            alarmHour = hourOfDay
//                            alarmMinute = minute
//                            showAlarmNotify()
//                            setAlarm()
//                            alarmButton!!.background =
//                                ContextCompat.getDrawable(this@MainActivity, R.drawable.activeoval)
//                            MainActivity.Companion.alarmActive = true
//                        }, alarmHour, alarmMinute, false
//                    )
//                timePickerDialog.show()
//            } else {
//                alarmButton!!.background =
//                    ContextCompat.getDrawable(this@MainActivity, R.drawable.oval)
//                alarmManager!!.cancel(alarmPendingIntent)
//                alarmNotification!!.cancel(2)
//                MainActivity.Companion.alarmActive = false
//            }
//        } // 알람 버튼 클릭 리스너
//        detectModeButton!!.setOnClickListener {
//            if (MainActivity.Companion.detectModeActive == false) {
//                MainActivity.Companion.detectModeActive = true
//                detectModeButton!!.background =
//                    ContextCompat.getDrawable(this@MainActivity, R.drawable.activeoval)
//                Toast.makeText(applicationContext, "감시모드가 활성화되었습니다.", Toast.LENGTH_LONG).show()
//            } else {
//                MainActivity.Companion.detectModeActive = false
//                detectModeButton!!.background =
//                    ContextCompat.getDrawable(this@MainActivity, R.drawable.oval)
//            }
//        } // 감시모드 버튼 클릭 리스너
//    }
//
//    override fun onBackPressed() {
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.addCategory(Intent.CATEGORY_HOME)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
//    } // 뒤로가기 버튼 클릭했을 때 홈으로 이동하기
//
//    fun showDetectNotify() {
//        val builder = NotificationCompat.Builder(this, "1")
//        detectPendingIntent = PendingIntent.getActivity(
//            applicationContext, 0, Intent(
//                applicationContext,
//                cctvActivity::class.java
//            ), PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        builder.setSmallIcon(R.drawable.applogo)
//        builder.setContentTitle("사람이 감지되었습니다.")
//        builder.setContentText("CCTV를 확인하시겠습니까?")
//        builder.setContentIntent(detectPendingIntent)
//        builder.setAutoCancel(true)
//        detectNotification =
//            getSystemService<Any>(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            detectNotification!!.createNotificationChannel(
//                NotificationChannel(
//                    "1",
//                    "감시모드",
//                    NotificationManager.IMPORTANCE_HIGH
//                )
//            )
//        }
//        detectNotification!!.notify(1, builder.build())
//    } // 감시모드 활성화일때 사람이 감지되면 Notification 생성
//
//    fun showAlarmNotify() {
//        val builder = NotificationCompat.Builder(this, "2")
//        val intent = Intent(applicationContext, notificationBroadcast::class.java)
//        alarmPendingIntent = PendingIntent.getBroadcast(
//            applicationContext,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        builder.setSmallIcon(R.drawable.alarm)
//        builder.setContentTitle("알람")
//        builder.setOngoing(true)
//        builder.setContentIntent(alarmPendingIntent)
//        if (alarmHour > 0 && alarmHour < 12) builder.setContentText("설정된 알람 시간은 오전 " + alarmHour + "시 " + alarmMinute + "분입니다.") else if (alarmHour == 12) builder.setContentText(
//            "설정된 알람 시간은 오후 " + alarmHour + "시 " + alarmMinute + "분입니다."
//        ) else if (alarmHour > 12 && alarmHour < 24) builder.setContentText("설정된 알람 시간은 오후 " + (alarmHour - 12) + "시 " + alarmMinute + "분입니다.") else if (alarmHour == 0) builder.setContentText(
//            "설정된 알람 시간은 오전 " + "0시 " + alarmMinute + "분입니다."
//        )
//        alarmNotification =
//            getSystemService<Any>(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            alarmNotification!!.createNotificationChannel(
//                NotificationChannel(
//                    "2",
//                    "알람",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//            )
//        }
//        alarmNotification!!.notify(2, builder.build())
//        Log.d(TAG, "show Alarm notify")
//    } // 알람 On 시켰을 때 Notification 생성
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    fun setAlarm() {
//        alarmCalendar = Calendar.getInstance()
//        alarmCalendar.setTimeInMillis(System.currentTimeMillis())
//        alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmHour)
//        alarmCalendar.set(Calendar.MINUTE, alarmMinute)
//        alarmCalendar.set(Calendar.SECOND, 0)
//        // TimePickerDialog 에서 설정한 시간을 알람 시간으로 설정
//        if (alarmCalendar.before(Calendar.getInstance())) alarmCalendar.add(
//            Calendar.DATE,
//            1
//        ) // 알람 시간이 현재시간보다 빠를 때 하루 뒤로 맞춤
//        alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
//        alarmManager = getSystemService<Any>(Context.ALARM_SERVICE) as AlarmManager
//        alarmIntent!!.action = AlarmReceiver.ACTION_RESTART_SERVICE
//        alarmCallPendingIntent = PendingIntent.getBroadcast(
//            this@MainActivity,
//            0,
//            alarmIntent,
//         PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        alarmManager!!.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            alarmCalendar.getTimeInMillis(),
//            alarmCallPendingIntent
//        )
//    } // 알람 설정
//
//    companion object {
//        var detectModeActive = false
//        var alarmActive = false
//        var context: Context? = null
//    }
//}
