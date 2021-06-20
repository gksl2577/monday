package com.hsj.SmartWindowdevice

//
import android.os.Handler
import android.util.Log
import java.io.*
import java.net.Socket


class tcpThread(var handler: Handler) : Thread() {

    var dataInputStream: InputStream? = null
    var dataOutputStream: OutputStream? = null
    private var socket: Socket? = null
    var ip = "192.168.196.69"
    private val port = 5555
    val TAG = "TAG+Thread"
    override fun run() {
        try {
            Log.d(TAG, "접속")
            socket = Socket(ip, port)
            dataOutputStream = socket!!.getOutputStream()
            dataInputStream = socket!!.getInputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val buffer = ByteArray(1024)
        var bytes: Int
        var tmp = ""
        Log.d(TAG, "수신 시작")
        while (true) {
            try {
                Log.d(TAG, "수신 대기")
                bytes = dataInputStream!!.read(buffer)
                Log.d(TAG, "byte = $bytes")
                if (bytes > 0) {
                    tmp = String(buffer, 0, bytes)
                } else {
                    Log.d(TAG, "재접속")
                    socket = Socket(ip, port)
                    dataOutputStream = socket!!.getOutputStream()
                    dataInputStream = socket!!.getInputStream()
                }
                Log.d(TAG, tmp)
                handler.obtainMessage(0, tmp).sendToTarget()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun cctv_center() {
        val inst = "center".toByteArray()
        dataOutputStream!!.write(inst)
    } // cctv 중앙으로 명령

    @Throws(IOException::class)
    fun cctv_right() {
        val inst = "right".toByteArray()
        dataOutputStream!!.write(inst)
    } // cctv 오른쪽으로 명령

    @Throws(IOException::class)
    fun cctv_left() {
        val inst = "left".toByteArray()
        dataOutputStream!!.write(inst)
    } // cctv 왼쪽으로 명령

    @Throws(IOException::class)
    fun cctvOn() {
        val inst = "cctvOn".toByteArray()
        dataOutputStream!!.write(inst)
    } // CCTV On 명령

    @Throws(IOException::class)
    fun cctvOff() {
        val inst = "cctvOff".toByteArray()
        dataOutputStream!!.write(inst)
    } // CCTV Off 명령
} // Tcp 소켓통신을 담당하는 클래스

//class Client(ip: String, order:String) {
//    private var msocket: Socket? = null
//    private var inputdata: BufferedReader? = null
//    private var outputdata: PrintWriter? = null
//
//    init {
//        try {
//            // 서버에 요청 보내기
//            msocket = Socket(ip, 5555)
//            println("$ip 연결됨")
//
//            // 통로 뚫기
//            inputdata = BufferedReader(
//                InputStreamReader(msocket!!.getInputStream())
//            )
//
//            outputdata = PrintWriter(msocket!!.getOutputStream())
//
//            // 메세지 전달
//            outputdata!!.println(order)
//            outputdata!!.flush()
//
//            // 응답 출력
//         println(inputdata!!.readLine())
//        } catch (e: IOException) {
//            println(e.message)
//        } finally {
//            // 소켓 닫기 (연결 끊기)
//            try {
//                msocket!!.close()
//            } catch (e: IOException) {
//                println(e.message)
//            }
//        }
//    }
//
//    fun recv(): BufferedReader? {
//        return inputdata
//    }
//}
//// fun main(){
////     val ip = "192.168.196.69"
////
////     Client(ip,"하이루")
////
//// }
//



