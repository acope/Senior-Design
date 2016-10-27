/*
 * Arduino Test Program to validate the functionality
 * of Computer Side Code.
 * 
 * 1. Code will setup Serial at 115200 and send 'T'(255)
 * 2. PC should respond with 'A'
 * 3. Code sends 'G'(255) to indicate ready
 * 4. PC should send 'R' to start recording mode
 * 5. Code will send measurements periodically
 * 6. PC may send 'C' to stop
 * 
 * For more, look at source code.
 * 
 * Note: If PC sends 'M' or 'D', code will respond 
 *       but nothing will happen. 'X' will actually
 *       change sampling rate.
 * 
 * Created by Kazumi Malhan
 */

#include <Arduino.h>
#include <TimerOne.h>

void timerCallback();

volatile unsigned int measurement_period = 10;
volatile unsigned int measurement_count = 0;
volatile bool f_send_data = false;
volatile bool f_record = false;
volatile unsigned long timestamp = 0;
volatile unsigned int motor_rpm = 3000;
volatile unsigned int input_rpm = 10;
volatile unsigned int output_rpm = 20;
volatile unsigned int voltage = 500;

void setup() {
 
  Serial.begin(115200);
  while(!Serial);

  while(true)
  {
    Serial.write('T');
    Serial.write(255); // termination

    if (Serial.available() > 0)
    {
      char inChar;
      inChar = (char)Serial.read();
      if (inChar == 'A')
        break;
    }
  }
  Timer1.initialize(100000);
  Timer1.attachInterrupt(timerCallback);
  Serial.write('G');
  Serial.write(255);
}

void loop() {
  if (f_send_data && f_record)
  {
    f_send_data = false;
    timestamp++;
    motor_rpm++;
    input_rpm++;
    output_rpm++;
    voltage++;
    if (motor_rpm == 3500) motor_rpm = 2500;
    if (input_rpm == 15) input_rpm = 8;
    if (output_rpm == 30) input_rpm = 15;
    if (voltage == 1020) voltage = 5;
  
    byte buf[12];
    buf[0] = timestamp & 255;
    buf[1] = (timestamp >> 8)  & 255;
    buf[2] = (timestamp >> 16) & 255;
    buf[3] = (timestamp >> 24) & 255;
    buf[4] = (motor_rpm) & 255;
    buf[5] = (motor_rpm >> 8) & 255;
    buf[6] = (input_rpm) & 255;
    buf[7] = (input_rpm >> 8) & 255;
    buf[8] = (output_rpm) & 255;
    buf[9] = (output_rpm >> 8) & 255;
    buf[10] = (voltage) & 255;
    buf[11] = (voltage >> 8) && 255;

    Serial.write('S');
    Serial.write(buf, 12);
    Serial.write('E');
    Serial.write(255);
  }
}

void timerCallback(){
  if (measurement_count == measurement_period)
  {
    f_send_data = true;
    measurement_count = 0;
  }
  else
  {
    measurement_count++;
  }
}

void serialEvent()
{
  while (Serial.available())
  {
    char inChar = (char)Serial.read();

    if (inChar == 'R')
    {
      f_record = true;
      Serial.write('A');
      Serial.write(255);
    }

    if (inChar == 'P')
    {
      Serial.write('F');
      Serial.write(255);
    }

    if (inChar == 'C')
    {
      f_record = false;
      Serial.write('A');
      Serial.write(255);
    }

    if (inChar == 'M')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        Serial.write('A');
        Serial.write(255);
      }
      else
        Serial.write('F');
    }

    if (inChar == 'D')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        Serial.write('A');
        Serial.write(255);
      }
      else
        Serial.write('F');
    }

    if (inChar == 'X')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        unsigned int rate = *((unsigned int*)&data[0]);
        measurement_period = __builtin_bswap16(rate);
        Serial.write('A');
        Serial.write(255);
      }
      else
        Serial.write('F');
    }
  }
}
