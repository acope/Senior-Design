#ifndef SRC_INITIALIZATION_H_
#define SRC_INITIALIZATION_H_

#include <Arduino.h>
#include <SPI.h>
#include <SD.h>
#include <TimerOne.h>
#include "typeDefinitions.h"
#include "dataCollection.h"


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
 * @berif Error Handle
 */
bool errorCheck();

#endif /* SRC_INITIALIZATION_H_ */
