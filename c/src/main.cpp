/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University             *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

/**
 * @file main.c
 * @author Oakland University
 * @data October 10 2016
 * @brief Microcontroller main.c file
 *
 * More details explanation goes here!
 *
 *
 *
 *
 */

/* Include files */
#include <Arduino.h>
#include "typeDefinitions.h"
#include "initialization.h"

/* Global Variables */
volatile DeviceState state_ = initialization;     ///< device state
String error_msg_;

// Input information
volatile InputCondition input_condition_;
volatile unsigned int multiply_factor_;
volatile InputCondition new_input_condition_;

// Data to be written
volatile DataCollection collected_data_;
volatile unsigned long raw_motor_rpm_count_ = 0;   ///< Totoal number of pulse used by IRS
volatile unsigned long raw_motor_speed_ = 0;       ///< Plase to store pulse reading
volatile unsigned long raw_motor_feedback_count_ = 0;
volatile unsigned long raw_motor_feedback_ = 0;

// Timer related variables
volatile unsigned int p_data_collection_ = 10;  ///< data collection period in 100ms
volatile unsigned int p_motor_control_ = 2;     ///< motor control period in 100ms
volatile bool f_data_collection_ = false;       ///< data collection task flag
volatile bool f_motor_control_ = false;         ///< motor control task flag

// SD Card
char sd_card_dir_path_[DIR_PATH_LENGTH];
char sd_card_file_path_[FILE_PATH_LENGTH];
volatile unsigned int file_index_ = 0;           ///< index of file number

// Flags
volatile bool f_record_request_ = false;         ///< record request task flag
volatile bool f_pause_request_ = false;          ///< pause request task flag
volatile bool f_complete_request_ = false;       ///< complete request task flag
volatile bool f_motor_speed_request_ = false;    ///< change motor speed request task flag
volatile bool f_amplitude_request_ = false;      ///< change amplitude request task flag
volatile bool f_sampling_rate_request_ = false;  ///< sampling rate change request task flag

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
    // TODO: Error Handling
  }
  else if (state_ == done)
  {
    // TODO: Termination code
    termination();
  }
  else if (state_ == pause)
  {
    // Stop motor & recording
    analogWrite(PWM_PIN, 0);
  }
  else // recording
  {
  // Check if timed task needs to be performed
  checkTimerTasks();
  }
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
