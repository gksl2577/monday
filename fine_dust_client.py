import socket

HOST = "host_ip" #put raspberry pi's ip here

PORT = 9999       

  
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)


client_socket.connect((HOST, PORT))

while True:

    
        try:
            data = client_socket.recv(1024)
            if (data != ''):
                print('Received', repr(data.decode()))
        except:
            pass

client_socket.close()
