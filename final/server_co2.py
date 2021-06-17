import mh_z19
import time
import socket 
from datetime import datetime
import sys

HOST = '0.0.0.0'

PORT = 9997

# ============setting up socket server============
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)


server_socket.bind((HOST, PORT))

server_socket.listen(1)

server_socket.setblocking(0)

is_connected = 0

client_socket, addr = server_socket.accept()

#print('Connected by', addr)
if __name__ == '__main__':

    try:
        while True:
            try:
                co2_data = mh_z19.read()['co2']
                print(co2_data)
            except:
                pass
            #test_data = "test msg"

            now = datetime.now()
            date_string = str(now.year) + "." + str(now.month) + "." + str(now.day) + " " + str(now.hour) + ":" + str(now.minute) + ":" + str(now.second)
            
                #check connection 
            if (is_connected == 0):
                try:
                    #client_socket, addr = server_socket.accept()
                    if client_socket:
                        print('Connected by', addr)
                        is_connected = 1
                except:
                    pass

            #send data
            if (is_connected == 1):
                try:
                    client_socket.sendall(co2_data.encode())
                    #client_socket.sendall(test_data.encode())
                except:
                    client_socket.close()
                    server_socket.close()
                    is_connected = 0

            
            #client_socket.sendall(co2_data)

            #sleep(1)
            sys.stdout.flush()
        #=======
        #client_socket.close()
        #server_socket.close()
    except KeyboardInterrupt:
        sys.exit()
