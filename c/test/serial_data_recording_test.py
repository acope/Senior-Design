import serial
import struct

port = "/dev/ttyACM0"
ser = serial.Serial(port, 115200, timeout=0)

setup = False
record_request = False

#Setup
while (setup == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'T'):
        ser.write(b'A')

    if (data == 'G'):
        print "Setup completed"
        setup = True;

    data = ""

#Start
ser.write(b'R')
while (record_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Record Request Completed"
        record_request = True

    data = ""

while(True):
    data = ser.read(1)

    if (len(data) > 0):
        print data.encode('hex')

    if (data == 'E'):
        ser.write(b'A')

    data = ""
