#-*-coding:utf8-*-

import serial
import struct
import time
from datetime import datetime
from time import sleep
from socket import *
import sys
sys.path.append("/home/pi/.local/lib/python3.7/site-packages") #to import mhz19
import mh_z19
import queue

from bluetooth import *
from urllib.request import urlopen
from urllib.parse import urlencode, unquote, quote_plus, quote
import urllib
import xml.etree.ElementTree as elemTree


recv_data = ''
dust_data = ""

co2_read_succeed = 0
co2_fail_count = 0

co2_mean = 0
co2_datas = []
co2_threshold = 800
co2_mean_list = []

pm25_data = 0
pm25_datas = []
pm10_data = 0
pm10_datas = []

dust_read_succeed = 0
dust_fail_count = 0

dust_pm25_mean = 0
dust_pm25_mean_list = []
dust_pm10_mean = 0
dust_pm10_mean_list = []

max_samples = 10 #calculating average for 10 secs


window_pm10_open = 0
window_pm25_open = 0
window_co2_open = 0
window_manual_open = 0

is_automode = 0

motor_measure_flag = 0
measure_data = open("measure_motor.txt",'r').readline()
if measure_data != '':
    motor_measure_time = measure_data
# UART
UART = '/dev/ttyAMA1'

# USE PORT
SERIAL_PORT = UART

# Baud Rate
Speed = 9600

# Host ip 
HOST = "0.0.0.0"

#Port
PORT = 8888

#server setup
server_socket = socket(AF_INET, SOCK_STREAM)

server_socket.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)

server_socket.bind((HOST, PORT))

server_socket.listen(1)

server_socket.setblocking(0)
is_connected = 0


#bluetooth server setup
bt_socket = BluetoothSocket(RFCOMM)
bt_connected = 0

#weather data parsing
url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"


station_location = "홍릉로" #defualt location
station_name = quote(station_location)

queryParams = '?' + urlencode({ quote_plus('servicekey') : 'XndVTn3yY4Rt8uzteBr%2FfcbYDAq9LiyBJPqIth%2F5oRKh6X23URynAib7Cqj03SWYjejetd5T940tylw%2BFGjAgg%3D%3D',
    quote_plus('stationName') : station_name,
    quote_plus('dataTerm') : "DAILY",
    quote_plus('ver') : 1.0})

api_requested_time = 0



class PMS7003(object): #fine dust sensor class

    # PMS7003 protocol data (HEADER 2byte + 30byte)
    PMS_7003_PROTOCOL_SIZE = 32

    # PMS7003 data list
    HEADER_HIGH            = 0  # 0x42
    HEADER_LOW             = 1  # 0x4d
    FRAME_LENGTH           = 2  # 2x13+2(data+check bytes) 
    DUST_PM1_0_CF1         = 3  # PM1.0 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM2_5_CF1         = 4  # PM2.5 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM10_0_CF1        = 5  # PM10 concentration unit μ g/m3（CF=1，standard particle）
    DUST_PM1_0_ATM         = 6  # PM1.0 concentration unit μ g/m3（under atmospheric environment）
    DUST_PM2_5_ATM         = 7  # PM2.5 concentration unit μ g/m3（under atmospheric environment）
    DUST_PM10_0_ATM        = 8  # PM10 concentration unit μ g/m3  (under atmospheric environment) 
    DUST_AIR_0_3           = 9  # indicates the number of particles with diameter beyond 0.3 um in 0.1 L of air. 
    DUST_AIR_0_5           = 10 # indicates the number of particles with diameter beyond 0.5 um in 0.1 L of air. 
    DUST_AIR_1_0           = 11 # indicates the number of particles with diameter beyond 1.0 um in 0.1 L of air. 
    DUST_AIR_2_5           = 12 # indicates the number of particles with diameter beyond 2.5 um in 0.1 L of air. 
    DUST_AIR_5_0           = 13 # indicates the number of particles with diameter beyond 5.0 um in 0.1 L of air. 
    DUST_AIR_10_0          = 14 # indicates the number of particles with diameter beyond 10 um in 0.1 L of air. 
    RESERVEDF              = 15 # Data13 Reserved high 8 bits
    RESERVEDB              = 16 # Data13 Reserved low 8 bits
    CHECKSUM               = 17 # Checksum code


    # header check 
    def header_chk(self, buffer):

        if (buffer[self.HEADER_HIGH] == 66 and buffer[self.HEADER_LOW] == 77):
            return True

        else:
            return False

    # chksum value calculation
    def chksum_cal(self, buffer):

        buffer = buffer[0:self.PMS_7003_PROTOCOL_SIZE]

        # data unpack (Byte -> Tuple (30 x unsigned char <B> + unsigned short <H>))
        chksum_data = struct.unpack('!30BH', buffer)

        chksum = 0

        for i in range(30):
            chksum = chksum + chksum_data[i]

        return chksum

    # checksum check
    def chksum_chk(self, buffer):   
        
        chk_result = self.chksum_cal(buffer)
        
        chksum_buffer = buffer[30:self.PMS_7003_PROTOCOL_SIZE]
        chksum = struct.unpack('!H', chksum_buffer)
        
        if (chk_result == chksum[0]):
            return True

        else:
            return False

    # protocol size(small) check
    def protocol_size_chk(self, buffer):

        if(self.PMS_7003_PROTOCOL_SIZE <= len(buffer)):
            return True

        else:
            return False

    # protocol check
    def protocol_chk(self, buffer):
        
        if(self.protocol_size_chk(buffer)):
            
            if(self.header_chk(buffer)):
                
                if(self.chksum_chk(buffer)):
                    
                    return True
                else:
                    print("Chksum err")
            else:
                print("Header err")
        else:
            print("Protol err")

        return False 

    # unpack data 
    # <Tuple (13 x unsigned short <H> + 2 x unsigned char <B> + unsigned short <H>)>
    def unpack_data(self, buffer):
        
        buffer = buffer[0:self.PMS_7003_PROTOCOL_SIZE]

        # data unpack (Byte -> Tuple (13 x unsigned short <H> + 2 x unsigned char <B> + unsigned short <H>))
        data = struct.unpack('!2B13H2BH', buffer)

        return data


    def print_serial(self, buffer):
        
        chksum = self.chksum_cal(buffer)
        data = self.unpack_data(buffer)

        print ("============================================================================")
        print ("Header : %c %c \t\t | Frame length : %s" % (data[self.HEADER_HIGH], data[self.HEADER_LOW], data[self.FRAME_LENGTH]))
        print ("PM 1.0 (CF=1) : %s\t | PM 1.0 : %s" % (data[self.DUST_PM1_0_CF1], data[self.DUST_PM1_0_ATM]))
        print ("PM 2.5 (CF=1) : %s\t | PM 2.5 : %s" % (data[self.DUST_PM2_5_CF1], data[self.DUST_PM2_5_ATM]))
        print ("PM 10.0 (CF=1) : %s\t | PM 10.0 : %s" % (data[self.DUST_PM10_0_CF1], data[self.DUST_PM10_0_ATM]))
        print ("0.3um in 0.1L of air : %s" % (data[self.DUST_AIR_0_3]))
        print ("0.5um in 0.1L of air : %s" % (data[self.DUST_AIR_0_5]))
        print ("1.0um in 0.1L of air : %s" % (data[self.DUST_AIR_1_0]))
        print ("2.5um in 0.1L of air : %s" % (data[self.DUST_AIR_2_5]))
        print ("5.0um in 0.1L of air : %s" % (data[self.DUST_AIR_5_0]))
        print ("10.0um in 0.1L of air : %s" % (data[self.DUST_AIR_10_0]))
        print ("Reserved F : %s | Reserved B : %s" % (data[self.RESERVEDF],data[self.RESERVEDB]))
        print ("CHKSUM : %s | read CHKSUM : %s | CHKSUM result : %s" % (chksum, data[self.CHECKSUM], chksum == data[self.CHECKSUM]))
        print ("============================================================================")

    def save_data(self,buffer):

        global dust_data
        global pm25_data, pm10_data

        chksum = self.chksum_cal(buffer)
        data = self.unpack_data(buffer)


        pm25_data = data[self.DUST_PM2_5_ATM]
        pm10_data = data[self.DUST_PM10_0_ATM]


def automode(bt_socket):
    global is_automode
    is_automode = 1
    try:
        bt_socket.send('1\n')
        print ("do auto mode")
    except:
        print('bluetooth send fail')
    
    pass

def automodeoff(bt_socket):
    global is_automode
    try:
        bt_socket.send('2\n')
        print ("shut down auto mode")
    except:
        print('bluetooth send fail')
    is_automode = 0
    

def pdlc_on(bt_socket):
    try:
        bt_socket.send('5\n')
        print ("make pdlc on")
    except:
        print('bluetooth send fail')
    pass

def pdlc_off(bt_socket):
    try:
        bt_socket.send('6\n')
        print ("make pdlc off")
    except:
        print('bluetooth send fail')
    pass

def open_window(bt_socket):
    try:
        bt_socket.send('3\n')
        print ("opening window")
    except:
        print('bluetooth send fail')
    
    pass
def close_window(bt_socket):
    try:
        bt_socket.send('4\n')
        print ("close window")
    except:
        print('send fail')
    pass

def open_window_manual(bt_socket):
    global window_manual_open
    try:
        bt_socket.send('7\n')
        window_manual_open = 1
        print ("opening window")
    except:
        print('bluetooth send fail')
    
    pass
def close_window_manual(bt_socket):
    global window_manual_open
    global window_pm10_open, window_pm25_open, window_co2_open
    
    try:
        bt_socket.send('8\n')
        window_manual_open = 0
        window_pm10_open = 0
        window_pm25_open = 0
        window_co2_open = 0
        print ("close window")
    except:
        print('send fail')
    pass

def stop(bt_socket):
    try:
        bt_socket.send('9\n')
        print('stop window')        
    except:
        print('send fail')

def measure_motor_rotation(bt_socket):
    global motor_measure_time, motor_measure_flag
    try:
        motor_measure_time = time.time()
        bt_socket.send('7\n')
        print('open window to measure')
        motor_measure_flag = 1        
    except:
        print('motor measure fail')

def measure_motor_rotation_off(bt_socket):
    global motor_measure_time, motor_measure_flag
    try:
        motor_measure_time = time.time() - motor_measure_time
        bt_socket.send('9\n')
        print('stopping window measuring')
        #save measured data to txt file
        measure_file = open("measure_motor.txt",'w')
        measure_file.write(str(motor_measure_time))
        print(motor_measure_time, "motor_measure_time")
        measure_file.close()
        motor_measure_flag = 1        

    except Exception as e:
        print(e)
        print('motor measure stop fail')

def check_already_open():
    global window_pm10_open,window_pm25_open,window_co2_open
    print ("debug:=============",window_co2_open,window_pm10_open,window_pm25_open)
    if window_co2_open or window_pm25_open or window_pm10_open:
        print("window already opened!")
        return 1 #window already opened
    else:
        return 0 # window not open


def mean(nums):
    return float(sum(nums)) / max(len(nums),1)



if __name__ == '__main__':

    ser = serial.Serial(SERIAL_PORT, Speed, timeout = 1)

    dust = PMS7003()

    try:

        while(1):

             #read serial input
            ser.flushInput()
            buffer = ser.read(1024)
            print("================================")

            #===========fine dust sensor=====
            if(dust.protocol_chk(buffer)):
            
                print("fine dust read success", flush = True)
                dust.save_data(buffer)
                dust_read_succeed = 1
                dust_fail_count=  0

                #print ("PM 2.5 : %s" % pm25_data, flush = True)
                #print ("PM 10.0 : %s" % pm10_data, flush = True)

            else:
                dust_read_succeed = 0
                dust_fail_count += 1
                print("finedust read fail", flush = True) 

            #==============co2 sensor=====
            try:
                co2_data = mh_z19.read()['co2']
                print("co2 read success")
                #print("co2: ",co2_data)
                co2_read_succeed= 1
                co2_fail_count = 0
                
            except:
                print("co2 read fail")
                co2_read_succeed  = 0
                co2_fail_count += 1

            #moving average filter
            pm25_datas.append(pm25_data)
            dust_pm25_mean = round(mean(pm25_datas))
            print ("pm 2.5 average: ",dust_pm25_mean)
            dust_pm25_mean_list.append(dust_pm25_mean)

            if len(pm25_datas) == max_samples:
                pm25_datas.pop(0)
            if len(dust_pm25_mean_list) == 300:
                dust_pm25_mean_list.pop(0)
            

            pm10_datas.append(pm10_data)
            dust_pm10_mean = round(mean(pm10_datas))
            dust_pm10_mean_list.append(dust_pm10_mean)
            print ("pm 10 average: ",dust_pm10_mean)

            if len(pm10_datas) == max_samples:
                pm10_datas.pop(0)
            if len(dust_pm10_mean_list) == 300:
                dust_pm10_mean_list.pop(0)


            co2_datas.append(co2_data)
            co2_mean = round(mean(co2_datas))
            co2_mean_list.append(co2_mean)
            print ("co2 average: ",co2_mean)

            if len(co2_datas) == max_samples:
                co2_datas.pop(0)
            if len(co2_mean_list) == 300:
                co2_mean_list.pop(0)

            
            #merge sensor datas 

            dust_data = str(dust_pm25_mean) + "," + str(dust_pm10_mean)
            data_send = dust_data + ","+str(co2_mean)+'\n'


            #print sensor fail warning
            if co2_fail_count >= 600:
                print ("please check co2 sensor connection")
            if dust_fail_count >= 600:
                print("please check fine dust sensor connection")


            
            #get weather info
            
            if time.time() - api_requested_time >= 3600:
                api_requested = 0
            
            if api_requested != 1:
                try:
                    request = urllib.request.Request(url+unquote(queryParams))
                    request.get_method = lambda: 'GET'
                    response_body = urlopen(request).read().decode()
                    xmlStr = response_body
                    tree = elemTree.fromstring(xmlStr)
                    pm10value_outer = int(tree.find('./body/items/item/pm10Value').text)
                    pm25value_outer = int(tree.find('./body/items/item/pm25Value').text)
                    
                    print("api newly requested")

                    api_requested_time = time.time()
                    api_requested = 1
                except:
                    print("parsing fail")

            print("Outer pm 2.5 value: ",pm25value_outer)
            print("Outer pm 10 value: ", pm10value_outer)

            #check connection 
            if (is_connected == 0):
                try:
                    client_socket,addr = server_socket.accept()
                    if client_socket:
                        client_socket.setblocking(0)
                        print ('----------------connected by', addr,'-----------------')
                        is_connected = 1                     
                except:
                    pass


            #send merged data
            if (is_connected == 1):
                try:
                    client_socket.sendall(data_send.encode())
                except:
                    is_connected = 0
                    print(addr," Disconnected")
                try:
                    recv_data = client_socket.recv(1024)
                    recv_data = recv_data.decode()
                except:
                    pass


            #bluetooth connection with arduino
            
            if bt_connected != 1:
                try:
                    bt_socket.connect(("98:D3:51:FE:2A:59",1))
                    if bt_socket:
                        bt_connected = 1
                except:
                    print("bluetooth connect fail")
                    
            
            if recv_data != '':

                command = recv_data
                print('==========',command,'===========')
                check_command = command.find("111") #station name check
                recv_data = ''
                
                if check_command != -1:
                    station_location = command[4:]

                elif command == "automodeon": 
                    automode(bt_socket)
                elif command == "automodeoff":
                    automodeoff(bt_socket)
                elif command == "pdlcon":
                    pdlc_on(bt_socket)
                elif command == "pdlcoff":
                    pdlc_off(bt_socket)

                if is_automode == 0:

                    if command == "windowopen":
                        open_window_manual(bt_socket)
                    elif command == "windowclose":
                        close_window_manual(bt_socket)

                    elif command == "windowstop":
                        stop(bt_socket)
                    elif command =="measureon":
                        measure_motor_rotation(bt_socket)
                    elif command == "measureoff":
                        measure_motor_rotation_off(bt_socket)

                if is_automode == 1:

                    if command == "windowopen":
                        print('please turn off auto mode!')                        
                        #open_window(bt_socket)
                    elif command == "windowclose":
                        print('please turn off auto mode!')                        
                        #close_window(bt_socket)

                    elif command == "windowstop":
                        print('please turn off auto mode!')                        
                        #stop()
                    elif command =="measureon":
                        print('please turn off auto mode!')
                        #reset_window()
                    elif command == "measureoff":
                        print('please turn off auto mode!')                        
                        #measure_motor_rotation()

            if motor_measure_flag ==1: #if measured data exists
                try:
                    bt_socket.send(str(motor_measure_time*2363)+'\n') #2363 is motor calculated ratio
                    motor_measure_flag = 0
                except:
                    print('bluetooth send fail')

                
     
            
            #do main algorithm
            if is_automode: #in automode
                if dust_pm10_mean > 30:
                    if dust_pm10_mean > pm10value_outer:
                        if check_already_open() != 1:
                            open_window(bt_socket)
                        window_pm10_open = 1
                        print('pm10 open because inner fine dust ppm is higher')

                    if len(dust_pm10_mean_list) > 120:
                        if (((dust_pm10_mean_list[-1] + (dust_pm10_mean_list[-1] - dust_pm10_mean_list[120])*180)) > pm10value_outer):
                            if check_already_open() != 1:
                                open_window(bt_socket)
                            window_pm10_open = 1
                            print('pm10 open because inner fine dust velocity is higher')
                    else:
                        if (((dust_pm10_mean_list[-1] + (dust_pm10_mean_list[-1] - dust_pm10_mean_list[0])*180)) > pm10value_outer):
                            if check_already_open() != 1:
                                open_window(bt_socket)
                            window_pm10_open = 1
                            print('pm10 open because inner fine dust velocity is higher')

                if dust_pm25_mean > 30:
                    if dust_pm25_mean > pm25value_outer:
                        if check_already_open() != 1:
                            open_window(bt_socket)
                        window_pm25_open = 1
                        print('pm25 open because inner fine dust ppm is higher')
                    
                    if len(dust_pm10_mean_list) >120:
                        if (((dust_pm25_mean_list[-1] + (dust_pm25_mean_list[-1] - dust_pm25_mean_list[120])*180)) > pm25value_outer):
                            if check_already_open() != 1:
                                open_window(bt_socket)
                            window_pm25_open = 1
                            print('pm25 open because inner fine dust velocity is higher')
                    else:
                        if (((dust_pm25_mean_list[-1] + (dust_pm25_mean_list[-1] - dust_pm25_mean_list[0])*180)) > pm25value_outer):
                            if check_already_open() != 1:
                                open_window(bt_socket)
                            window_pm25_open = 1
                            print('pm25 open because inner fine dust velocity is higher')

                if co2_mean >= co2_threshold:
                    open_window(bt_socket)
                    window_co2_open = 1
                
                if window_pm10_open:
                    if abs((dust_pm10_mean_list[-1] - dust_pm10_mean_list[0])) < 10 :

                        window_pm10_open = 0
                        if (window_co2_open == 0 and window_pm10_open == 0 and window_pm25_open == 0):
                    
                            close_window(bt_socket)

                if window_pm25_open:
                    if abs((dust_pm25_mean_list[-1] - dust_pm25_mean_list[0])) < 10 :
  
                        window_pm25_open = 0
                        if (window_co2_open == 0 and window_pm10_open == 0 and window_pm25_open == 0):
                    
                            close_window(bt_socket)

                if window_co2_open:
                    if abs((co2_mean_list[-1] - co2_mean_list[0])) < 10 :
   
                        window_co2_open = 0
                        if (window_co2_open == 0 and window_pm10_open == 0 and window_pm25_open == 0):
                    
                            close_window(bt_socket)

                

        client_socket.close()
        server_socket.close()
        bt_socket.close()
        ser.close()

    except KeyboardInterrupt:
        #Ctrl C end

        bt_socket.close()
        client_socket.close()
        server_socket.close()
        ser.close()
        sys.exit()
        
