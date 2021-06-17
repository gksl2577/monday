#-*-coding:utf8-*-

import serial
import struct
import time
from datetime import datetime
from time import sleep
from socket import *
import sys
import mh_z19
#import fine_dust

recv_data = ''
dust_data = ""

# UART / USB Serial : 'dmesg | grep ttyUSB'
USB0 = '/dev/ttyUSB0'
#UART = '/dev/ttyS0'
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

server_socket.bind(("", PORT))

server_socket.listen(1)

server_socket.setblocking(0)
is_connected = 0

class PMS7003(object):

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

        chksum = self.chksum_cal(buffer)
        data = self.unpack_data(buffer)

        print ("PM 1.0 : %s" % ( data[self.DUST_PM1_0_ATM]), flush = True)
        print ("PM 2.5 : %s" % ( data[self.DUST_PM2_5_ATM]), flush = True)
        print ("PM 10.0 : %s" % ( data[self.DUST_PM10_0_ATM]), flush = True)

        dust_data = date_string + "," + str(data[self.DUST_PM1_0_ATM]) + "," + str(data[self.DUST_PM2_5_ATM]) + "," + str(data[self.DUST_PM10_0_ATM])+'\n'
       # f.write(dust_data)


def automode():
    print ("do auto mode")
    pass
def sensordata():
    print ("return sensor data")
    pass
def pdlc_on():
    print ("make pdlc on")
    pass
def pdlc_off():
    print ("make pdlc off")
    pass
def open_window():
    print ("open window")
    pass
def close_window():
    print ("close window")
    pass
def stop():
    print ("stop ")
    pass
def reset_window():
    print ("resetting window")
    pass
def measure_motor_rotation():
    print ("measuring motor rotation")
    pass



if __name__ == '__main__':

    ser = serial.Serial(SERIAL_PORT, Speed, timeout = 1)

    dust = PMS7003()

    try:

        while(1):
            #check time
            now = datetime.now()
            date_string = str(now.year) + "." + str(now.month) + "." + str(now.day) + " " + str(now.hour) + ":" + str(now.minute) + ":" + str(now.second)

             #read serial input
            ser.flushInput()
            buffer = ser.read(1024)


            if(dust.protocol_chk(buffer)):
            
                print("DATA read success", flush = True)
            
                #print data
                #dust.print_serial(buffer)
                dust.save_data(buffer)
            else:

                print("DATA read fail...", flush = True) 

            #check connection 
            if (is_connected == 0):
                try:
                    client_socket,addr = server_socket.accept()
                    if client_socket:
                        client_socket.setblocking(0)
                        print ('connected by', addr)
                        is_connected = 1
                    try:
                        pass
                        #client_socket.sendall("test message")
                    except:
                        print ("send fail")
                        
                except:
                    pass


            #send dust data
            if (is_connected == 1):
                try:

                    client_socket.sendall(dust_data.encode())
                    try:
                        recv_data = client_socket.recv(1024)
                        recv_data = recv_data.decode()
                    except:
                        pass
                    #print(recv_data)
                    
                except:
                    is_connected = 0
                    print(addr," Disconnected")

            if recv_data != '':
                #print ('Received Data : ' + str(recv_data))
                command = recv_data
                recv_data = ''

                if command == "automode": 
                    automode()
                elif command == "sensordata":
                    sensordata()
                elif command == "pdlcon":
                    pdlc_on()
                elif command == "pdlcoff":
                    pdlc_off()
                elif command == "open":
                    open_window()
                elif command == "close":
                    close_window()
                elif command == "stop":
                    stop()
                elif command =="reset":
                    reset_window()
                elif command == " measuremotorrotation":
                    measure_motor_rotation()
                else:
                    print ('Operation not assigend : ' + str(recv_data))
            #else:
             #   print ('err, received data : ' + str(recv_data))

        #print 'teset'
        client_socket.close()
        server_socket.close()
        ser.close()

    except KeyboardInterrupt:
        #Ctrl C end
        sys.exit()
        