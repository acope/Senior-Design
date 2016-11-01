/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University              *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

#ifndef SRC_INITIALIZATION_H_
#define SRC_INITIALIZATION_H_

#include <Arduino.h>
#include <SPI.h>
#include <SD.h>
#include <TimerOne.h>
#include "typeDefinitions.h"
#include "dataCollection.h"

/**
 * @file initialization.h
 * @author Oakland University Senior Design Group 9/10
 * @date October 31 2016
 * @brief  Initialization and Error Handling.
 *
 * @details This program performs following functionalities:
 * 1. Initialize I/Os
 * 2. Initialize digital pin interrupts
 * 3. Initialize Timer
 * 4. Initialize serial communication
 * 5. Initialzie SD card and create directory structure.
 * 6. Safely stop any running functionalities
 *    in case of error.
 *
 */

/**
 * @brief initialization of all features
 */
bool initializeBoard();

/**
 * @brief initialize and test serial communication
 */
bool initializeSerial(unsigned long speed);

/**
 * @brief initialize and test SD card
 */
bool initializeSD();

/**
 * @brief Print Directory Structure
 */
void printDirectory(File dir, int numTabs);

/**
 * @brief Handle Error
 */
void handleError();

#endif /* SRC_INITIALIZATION_H_ */
