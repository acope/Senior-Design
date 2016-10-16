import serial

port = "/dev/ttyACM0"
ser = serial.Serial(port, 115200, timeout=0)

test_pass = False

while (test_pass == False):
    data = ser.read(1)
    if (len(data) > 0):
        print data

    if (data == 'T'):
        ser.write(b'A')

    if (data == 'G'):
        test_pass = True

    data = ""

ser.close()
