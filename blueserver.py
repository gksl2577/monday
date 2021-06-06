from bluetooth import *
from time import *

# open_xxx return value
do_open = 1
do_fine = 2
do_close = 3
must_open = 4

def co2():
    open_co2 = 1
    return open_co2

def ppm():
    open_ppm = 1
    return open_ppm

open = 0

client_socket = BluetoothSocket(RFCOMM)
client_socket.connect(("98:D3:51:FE:2A:59",1))

while(True):
    open_co2 = co2()
    open_ppm = ppm()


    if (open_ppm == must_open):
        if not open:
            client_socket.send('1')
            sleep(10)
            open = 1    
    else:
        if not open:
            if open_co2 == do_open or (open_co2==do_fine and open_ppm == do_open):
                client_socket.send('1')
                sleep(10)
                open = 1
        else:
            if open_co2 == do_close or (open_co2==do_fine and (open_ppm != do_open)):
                client_socket.send('2')
                sleep(10)
                open = 0

client_socket.close()