import serial
from time import sleep

port = "/dev/ttyACM0"
ser = serial.Serial(port, 115200, timeout=0)

while True:
    data = ser.read(9999)
    if len(data) > 0:
        print 'Get:', data

    count = 0;

    if (count == 0):
        x = ser.write('R')
        count = count + 1

    if (count == 1):
        x = ser.write('P')
        count = count + 1

    if (count == 2):
        x = ser.write('C')
        count = count + 1

    if (count == 3):
        x = ser.write('M')
        x = ser.write('3000')
        x = ser.write('E')
        count = 0


    sleep(0.5)
    print 'not blocking'

ser.close()
