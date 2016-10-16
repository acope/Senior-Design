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
 * @brief read motor pulse
 */
void MotorRpmCountISR();

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
 * @brief Send data to serial
 */
bool sendDataSerial();

/**
 * @brief Send input data to SPI
 */
bool sendInputSPI();

/**
 * @brief Send data to SPI
 */
bool sendDataSPI();

#endif /* SRC_DATACOLLECTION_H_ */
