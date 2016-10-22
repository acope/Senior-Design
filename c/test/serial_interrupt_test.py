import serial
import struct

port = "/dev/ttyACM0"
ser = serial.Serial(port, 115200, timeout=0)

setup = False
record_request = False
pause_request = False
complete_request = False
motor_change_request = False
amplitude_change_request = False
sample_rate_change_request = False

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

#Pause
ser.write(b'P')
while (pause_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Pause Request Completed"
        pause_request = True

    data = ""

#Complete
ser.write(b'C')
while (complete_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Complete Request Completed"
        complete_request = True

    data = ""

#Motor Speed
ser.write(b'M')
# send 2007
ser.write(struct.pack('>2B', 7, 215))
ser.write(b'E')

while (motor_change_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Motor Speed Change Request Completed"
        motor_change_request = True

    data = ""

#Amplitude
ser.write(b'D')
# send 8
ser.write(struct.pack('>2B', 0, 8))
ser.write(b'E')

while (amplitude_change_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Amplitude Change Request Completed"
        amplitude_change_request = True

    data = ""


#Sample Rate
ser.write(b'X')
# send 10
ser.write(struct.pack('>2B', 0, 10))
ser.write(b'E')

while (sample_rate_change_request == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'A'):
        print "Sample Rate Change Request Completed"
        sample_rate_change_request = True

    data = ""

print "All Test has passed !!"
ser.close()
