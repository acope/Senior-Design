#ifndef SRC_DATACOLLECTION_H_
#define SRC_DATACOLLECTION_H_

#include <Arduino.h>
#include <SPI.h>
#include <SD.h>
#include <TimerOne.h>
#include "typeDefinitions.h"

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
