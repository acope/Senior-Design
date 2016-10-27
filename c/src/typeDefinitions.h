#ifndef SRC_TYPEDEFINITIONS_H_
#define SRC_TYPEDEFINITIONS_H_

#include <Arduino.h>

// indication of current state
typedef enum deviceState{
  initialization,  // device in initializing
  ready,           // done initialization
  prepare,         // start motor and prepare to record
  recording,       // recording
  pause,           // stop recording while motor is running
  done,            // stop recording, stop motor
  error            // error
} DeviceState;

// structure to store all sensor data
typedef struct {
  unsigned long timestamp;
  unsigned int motor_rpm;
  unsigned int input_rpm;
  unsigned int output_rpm;
  unsigned int generated_voltage;
} DataCollection;

// structure to store input information
typedef struct {
  unsigned int frequency;
  unsigned int amplitude;
  unsigned int sampling_rate;
} InputCondition;

// Digital input pin (Must be digital pin with interrupt)
#define MOTOR_RPM_PIN  18
#define INPUT_RPM_PIN  19
#define OUTPUT_RPM_PIN 20

// analog input pins
#define VOLTAGE_PIN 4

// analog output pin
#define PWM_PIN 7

// sd card pin
#define SD_CS_PIN 4

// Encoder tooth number @TODO: Add tooth of other encoder
#define MOTOR_ENCODER_TOOTH 15
#define ODRIVE_ENCODER_TOOTH 360

#define ERROR_LENGTH 100

// Input Restriction @TODO: Ask EE/ME
#define MAX_FREQUENCY   4000
#define MIN_FREQUENCY   2000
#define MAX_AMPLITUDE   12
#define MIN_AMPLITUDE   8
#define MAX_SAMPLE_RATE 600
#define MIN_SAMPLE_RATE 1

// File name length @TODO
#define DIR_LENGTH 7
#define FILE_LENGTH 10
#define DIR_PATH_LENGTH 8
#define FILE_PATH_LENGTH 20

#endif /* SRC_TYPEDEFINITIONS_H_ */
