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
 * @brief initialize and test SPI communication
 */
bool initializeSPI();

/**
 * @brief Test run motor and measure rpm
 */
bool motorTestRun();


#endif /* SRC_INITIALIZATION_H_ */
