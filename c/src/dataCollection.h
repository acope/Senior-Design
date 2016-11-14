/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University              *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

#ifndef SRC_DATACOLLECTION_H_
#define SRC_DATACOLLECTION_H_

#include <Arduino.h>
#include <SPI.h>
#include <SD.h>
#include <TimerOne.h>
#include "typeDefinitions.h"
#include "initialization.h"

/**
 * @file dataCollection.h
 * @author Oakland University Senior Design Group 9/10
 * @date October 31 2016
 * @brief  Perform Motor Control and Data Collection.
 *
 * @details This program performs following functionalities:
 * 1. Measure sensor data
 * 2. Send measurements to PC via serial communication
 * 3. Manage SD card file access and updates
 * 4. Save input information to SD card
 * 5. Save measurements to SD card
 * 6. Control motor speed using PID controller
 *
 */

/**
 * @brief check flags
 */
void checkSerialInterrupt();

/**
 * @brief trigger timed tasks
 */
void timerCallback();

/**
 * @brief check timer flag
 */
void checkTimerTasks();

/**
 * @brief count motor pulse
 */
void MotorRpmCountISR();

/**
 * @brief count input pulse
 */
void InputRpmCountISR();

/**
 * @brief count output pulse
 */
void OutputRpmCountISR();

/**
 * @brief Start motor
 */
bool startMotor();

/**
 * @brief Stop motor
 */
bool stopMotor();

/**
 * @brief Motor Control PID
 */
void motorSpeedControlPID();

/**
 * @brief Open SD card file
 */
void OpenSDFile();

/**
 * @brief Close SD card file
 */
void CloseSDFile();

/**
 * @brief Send data to serial
 */
bool sendDataSerial();

/**
 * @brief Send input data to SD
 */
bool sendInputSD();

/**
 * @brief Send data to SD
 */
bool sendDataSD();
/**
 * @brief Update SD card file to new one
 */
void updateSDFile();

/**
 * @berif Error Handle
 */
bool errorCheck();

#endif /* SRC_DATACOLLECTION_H_ */
