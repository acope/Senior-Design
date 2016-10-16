#include "initialization.h"

// Serial
const unsigned long WAIT_TIME_MS = 500;
const unsigned long UART_SPEED = 115200;
const unsigned long TIMER_US = 100000; // 100 ms

volatile extern DeviceState state_;

extern Sd2Card sd_card_;
extern SdVolume sd_volume_;
extern SdFile sd_root_;
extern String target_motor_speed_rpm_;
extern volatile InputCondition input_condition_;
extern volatile unsigned int multiply_factor_;
extern volatile DataCollection collected_data_;

bool initializeBoard()
{
  bool isInitialized = true;

  // setup and test PC connection
  isInitialized &= initializeSerial(UART_SPEED);

  // Make sure no interrupt happens
  noInterrupts();

  // initialize digital pin
  pinMode(MOTOR_RPM_PIN, INPUT);

  // setup interrupt for rpm reading pin
  attachInterrupt(digitalPinToInterrupt(MOTOR_RPM_PIN), MotorRpmCountISR, RISING);

  // setup PWM
  pinMode(PWM_PIN, OUTPUT);
  analogWrite(PWM_PIN, 0);

  // setup analog pin (Test read)
  analogReference(DEFAULT);
  analogRead(VOLTAGE_PIN);

  // set default input information
  input_condition_.amplitude = 4;
  input_condition_.frequency = 10;
  input_condition_.sampling_rate = 10;
  multiply_factor_ = MAX_SAMPLING - input_condition_.sampling_rate;
  collected_data_.timestamp = 0;

  // TODO: setup and test SD card connection
//  isInitialized &= initializeSPI();

  // TODO: Timer setup
  Timer1.initialize(TIMER_US);
  Timer1.attachInterrupt(timerCallback);

  // TODO: Test motor and read rom
//  isInitialized &= motorTestRun();

  // TODO: Watchdog (later)


  // Check result of initialization
  if (!isInitialized)
  {
    // let PC know it is error
    Serial.write('Z');
    state_ = error;
  }
  else
  {
  // TODO: change state to ready and send ready signal
  state_ = ready;

  // Send ready signal to PC
  Serial.write('G');

  // Enable Interrupts
  interrupts();
  }

  return isInitialized;
}


bool initializeSerial(unsigned long speed)
{
  // Note that serial.write is non-blocking code (writes to buffer)
  int timeout = 10;

  Serial.begin(speed);

  while (!Serial);      // wait untill serial is connected.

  // test connection
  while (true)
  {
    Serial.write('T');

    if (Serial.available() > 0)
    {
      char inChar;
      inChar = (char)Serial.read();
      if (inChar == 'A')
        return true;
    }
    else
    {
      delay(WAIT_TIME_MS);
      timeout--;
      if (!timeout)
        return false;
    }
  }
}

bool initializeSPI()
{
  if (!sd_card_.init(SPI_FULL_SPEED, SD_PIN))
  {
    // indicate sd card error
    Serial.write('Z');
    Serial.println("Fail to initialize SD card.");
    state_ = error;
    return false;
  }

  if (!sd_volume_.init(sd_card_))
  {
    Serial.write('Z');
    Serial.println("Fail to find FAT16/FAT32 partition.");
  }
  // TODO: Check following code
  sd_root_.openRoot(sd_volume_);

  return true;
}

bool motorTestRun()
{

}

bool termination()
{
  // TODO: Stop data recording

  // TODO: Stop Motor

  // TODO: Close SD card file system

  // TODO: Close SPI connection

  // TODO: Close any port

  // TODO: Close Timer

  // TODO: Close Serial

}
