/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University              *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

#include "initialization.h"

// Serial
const unsigned long WAIT_TIME_MS = 500;
const unsigned long UART_SPEED = 115200;
const unsigned long TIMER_US = 100000; // 100 ms

bool initializeBoard()
{
  bool isInitialized = true;

  // setup and test PC connection
  isInitialized &= initializeSerial(UART_SPEED);

  // setup and test SD card connection
 isInitialized &= initializeSD();

  // Make sure no interrupt happens
  noInterrupts();

  // initialize digital pin
  pinMode(MOTOR_RPM_PIN, INPUT);
  pinMode(INPUT_RPM_PIN, INPUT);
  pinMode(OUTPUT_RPM_PIN, INPUT);

  // setup interrupt for rpm reading pin
  attachInterrupt(digitalPinToInterrupt(MOTOR_RPM_PIN), MotorRpmCountISR, FALLING);
  attachInterrupt(digitalPinToInterrupt(INPUT_RPM_PIN), InputRpmCountISR, FALLING);
  attachInterrupt(digitalPinToInterrupt(OUTPUT_RPM_PIN), OutputRpmCountISR, FALLING);

  // setup PWM
  pinMode(PWM_PIN, OUTPUT);
  analogWrite(PWM_PIN, 0);

  // setup analog pin (Test read)
  analogReference(DEFAULT);
  analogRead(VOLTAGE_PIN);

  // set default input information
  input_condition_.amplitude = DEF_AMPLITUDE;
  input_condition_.frequency = DEF_FREQUENCY;
  input_condition_.sampling_rate = DEF_SAMPLE_RATE;
  multiply_factor_ = MAX_SAMPLE_RATE - input_condition_.sampling_rate;
  collected_data_.timestamp = 0;

  // Timer setup
  Timer1.initialize(TIMER_US);
  Timer1.attachInterrupt(timerCallback);

  interrupts();

  // Check result of initialization
  if (!isInitialized)
  {
    // let PC know it is error
    state_ = error;
    Serial.write('Z');
    Serial.println(error_msg_);
    Serial.write('E');
    Serial.write(255);
  }
  else
  {
  // Ready
  state_ = ready;
  Serial.write('G');
  Serial.write(255);
  }

  return isInitialized;
}


bool initializeSerial(unsigned long speed)
{
  Serial.begin(speed);

  while (!Serial);      // wait untill serial is connected.

  // test connection
  while (true)
  {
    Serial.write('T');
    Serial.write(255);

    if (Serial.available() > 0)
    {
      char inChar;
      inChar = (char)Serial.read();
      if (inChar == 'A')
        return true;
    }
  }
}

// Supporting function, not actively used
void printDirectory(File dir, int numTabs) {
  while (true) {

    File entry =  dir.openNextFile();
    if (! entry) {
      // no more files
      break;
    }
    for (uint8_t i = 0; i < numTabs; i++) {
      Serial.print('\t');
    }
    Serial.print(entry.name());
    if (entry.isDirectory()) {
      Serial.println("/");
      printDirectory(entry, numTabs + 1);
    } else {
      // files have sizes, directories do not
      Serial.print("\t\t");
      Serial.println(entry.size(), DEC);
    }
    entry.close();
  }
}

// NOTE: SD card will create new file every 50,000 records
bool initializeSD()
{
  if (!restart_)
  {
    // Setup connection
    if (!SD.begin(SD_CS_PIN))
    {
      error_msg_ = "SD card initialization failed.";
      return false;
    }
  }
  // Create new test folder
  unsigned int count = 1;
  File root;
  root = SD.open("/");
  char dirname[DIR_LENGTH];


  while (true)
  {
    sprintf(dirname, "TEST%03d", count);

    if (SD.exists(dirname))
      count++;
    else
      break;
  }

  //Serial.println(dirname);

  if (!SD.mkdir(dirname))
  {
    error_msg_ = "Cannot create directory in SD card.";
    return false;
  }
  sprintf(sd_card_dir_path_, "/%s", dirname);
  // @FIXME: Remove
  //printDirectory(root, 0);

  file_index_ = 1;
  //Serial.println(sd_card_dir_path_);
  sprintf(sd_card_file_path_, "%s/RUN%04d.csv", sd_card_dir_path_, file_index_);
  sprintf(sd_card_input_path_, "%s/INPUT.csv", sd_card_dir_path_);

  //Serial.println(sd_card_file_path_);
  //Serial.println(sd_card_input_path_);
  //Serial.println(sd_card_dir_path_);

  // write header to file
  sd_card_file_ = SD.open(sd_card_file_path_, FILE_WRITE);
  sd_card_file_.write("TIMESTAMP, MOTOR_RPM, INPUT_RPM, OUTPUT_RPM, VOLTAGE,\n");
  sd_card_file_.close();

  return true;
}
// TODO: Consider error handle method
void handleError()
{
  // stop motor, close SD card, then wait for restart_
  analogWrite(PWM_PIN, 0);
  sd_card_file_.close();
  sd_card_input_.close();
  // send restart message
  Serial.write('Z');
  Serial.print("Please check equipment and restart experiment...");
  Serial.write('E');
  Serial.write(255);
}
