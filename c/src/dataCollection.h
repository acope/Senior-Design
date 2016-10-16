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
 * @brief read motor pulse
 */
void MotorRpmCountISR();

/**
 * @brief Change target motor speed
 */
bool changeMotorTargetSpeed(unsigned int target_speed);

/**
 * @brief Start motor
 */
bool startMotor();

/**
 * @brief Stop motor
 */
bool stopMotor();

/**
 * @brief PID motor control
 */
void motorSpeedControlPID();

/**
 * @brief get current motor rotation speed in rpm.
 */
unsigned int getMotorRPM();

/**
 * @brief get current generated voltage in V
 */
unsigned int getVoltage();

/**
 * @brief Send data to serial
 */
bool sendDataSerial();

/**
 * @brief Send data to SPI
 */
bool sendDataSPI();

#endif /* SRC_DATACOLLECTION_H_ */
