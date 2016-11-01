/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University              *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

/**
 * @file main.cpp
 * @author Oakland University Senior Design Group 9/10
 * @date October 31 2016
 * @brief  Main File of Microcontroller Code.
 *
 * @details This program performs following functionalities:
 * 1. Receive command from PC via serial communication
 * 2. Control motor speed using PID controller
 * 3. Measure motor rpm, intput/output rpm and
 *    generated voltage at custom sample rate
 * 4. Send measurements to PC via serial communication
 * 5. Save input condition to SD card
 * 6. Save measurements to SD card
 *
 */

/* Include files */
#include <Arduino.h>
#include "typeDefinitions.h"
#include "initialization.h"
#include "dataCollection.h"

/* Global Variables */
volatile DeviceState state_ = initialization;
String error_msg_;

// Input information
volatile InputCondition input_condition_;
volatile unsigned int multiply_factor_;
volatile InputCondition new_input_condition_;
unsigned long sd_card_new_file_count_ = 0;

// Data to be written
volatile DataCollection collected_data_;
volatile unsigned long r_motor_rpm_count_ = 0;
volatile unsigned long r_motor_rpm_ = 0;
volatile unsigned long r_motor_feedback_count_ = 0;
volatile unsigned long r_motor_feedback_rpm_ = 0;
volatile unsigned long r_input_rpm_count_ = 0;
volatile unsigned long r_input_rpm_ = 0;
volatile unsigned long r_output_rpm_count_ = 0;
volatile unsigned long r_output_rpm_ = 0;

// Timer related variables
volatile unsigned int p_motor_control_ = 2;
volatile bool f_data_collection_ = false;
volatile bool f_motor_control_ = false;
volatile bool f_start_pid_ = false;

// Error Checking
volatile unsigned int p_error_check_ = 60;
volatile bool f_error_check_ = false;

// SD Card
char sd_card_dir_path_[DIR_PATH_LENGTH];
char sd_card_file_path_[FILE_PATH_LENGTH];
volatile unsigned int file_index_ = 1;
File sd_card_file_;
char sd_card_input_path_[FILE_PATH_LENGTH];
File sd_card_input_;
volatile bool restart_ = false;

// Flags
volatile bool f_record_request_ = false;
volatile bool f_pause_request_ = false;
volatile bool f_complete_request_ = false;
volatile bool f_motor_speed_request_ = false;
volatile bool f_amplitude_request_ = false;
volatile bool f_sampling_rate_request_ = false;

/**
 * @brief Setup code that runs once Arduino powers on
 */
void setup()
{
  //setup board
  bool status = false;
  status = initializeBoard();
}

/**
 * @brief Loop that runs code repeatedly.
 */
void loop()
{
  // Check commands from PC
  checkSerialInterrupt();

  if (state_ == error)
  {
    handleError();
  }
  else if (state_ == prepare)
  {
    checkTimerTasks();

    if (f_start_pid_)
    {
      collected_data_.timestamp = 0;
      state_ = recording;
    }
  }
  else if (state_ == recording)
  {
    checkTimerTasks();
  }
  else {}
}

/**
 * @brief Function called by serial interrupt
 */
void serialEvent()
{
  while (Serial.available())
  {
    char inChar = (char)Serial.read();

    if (inChar == 'R')
      f_record_request_ = true;

    if (inChar == 'P')
      f_pause_request_ = true;

    if (inChar == 'C')
      f_complete_request_ = true;

    if (inChar == 'M')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        unsigned int speed = *((unsigned int*)&data[0]);
        new_input_condition_.frequency = __builtin_bswap16(speed);
        f_motor_speed_request_ = true;
      }
      else
        Serial.write('F');
    }

    // amplitude change
    if (inChar == 'D')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        unsigned int amplitude = *((unsigned int*)&data[0]);
        new_input_condition_.amplitude = __builtin_bswap16(amplitude);
        f_amplitude_request_ = true;
      }
      else
        Serial.write('F');
    }

    // sample rate change
    if (inChar == 'X')
    {
      char data[5];
      if (Serial.readBytesUntil('E', data, 5) == 2)
      {
        unsigned int rate = *((unsigned int*)&data[0]);
        new_input_condition_.sampling_rate = __builtin_bswap16(rate);
        f_sampling_rate_request_ = true;
      }
      else
        Serial.write('F');
    }

  }
}
