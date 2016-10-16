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
DeviceState state_ = initialization;     ///< device state

// Input information
volatile InputCondition input_condition_;
volatile unsigned int multiply_factor_;

// Data to be written
volatile DataCollection collected_data_;

// Data Collection
volatile unsigned long raw_motor_rpm_count_ = 0;   ///< Totoal number of pulse used by IRS
volatile unsigned long raw_motor_speed_ = 0;       ///< Plase to store pulse reading
volatile unsigned int raw_voltage_ = 0;            ///< measured voltage from ATC


// Timer related variables
volatile unsigned int p_data_collection_ = 10;  ///< data collection period in 100ms
volatile unsigned int p_motor_control_ = 2;     ///< motor control period in 100ms
volatile bool f_data_collection_ = false;       ///< data collection task flag
volatile bool f_motor_control_ = false;         ///< motor control task flag

// SD card
Sd2Card sd_card_;
SdVolume sd_volume_;
SdFile sd_root_;

// Flags
volatile bool f_record_request_ = false;         ///< record request task flag
volatile bool f_pause_request_ = false;          ///< pause request task flag
volatile bool f_complete_request_ = false;       ///< complete request task flag
volatile bool f_motor_speed_request_ = false;    ///< change motor speed request task flag
volatile bool f_amplitude_request_ = false;      ///< change amplitude request task flag
volatile bool f_sampling_rate_request_ = false;  ///< sampling rate change request task flag

// Motor target speed
String target_motor_speed_rpm_ = "";    ///< Motor target speed in rpm


/**
 * @brief Setup code that runs once Arduino powers on
 */
void setup()
{
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);

  // setup board
  bool status = false;
  status = initializeBoard();

  if (status)
  {
    digitalWrite(13, HIGH);
  }
}

/**
 * @brief Loop that runs code repeatedly.
 */
void loop()
{
  // Check commands from PC
  checkSerialInterrupt();

  // TODO: Check if timed task needs to be performed

}

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
        input_condition_.frequency = __builtin_bswap16(speed);
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
        input_condition_.amplitude = __builtin_bswap16(amplitude);
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
        input_condition_.sampling_rate = __builtin_bswap16(rate);
        f_sampling_rate_request_ = true;
      }
      else
        Serial.write('F');
    }

  }
}
