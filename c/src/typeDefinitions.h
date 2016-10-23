#ifndef SRC_TYPEDEFINITIONS_H_
#define SRC_TYPEDEFINITIONS_H_

#include <Arduino.h>

// indication of current state
typedef enum deviceState{
  initialization,
  ready,
  recording,
  pause,
  done,
  error
} DeviceState;

// structure to store all sensor data
typedef struct {
  unsigned long timestamp;
  unsigned int motor_rpm;
  unsigned int generated_voltage;
} DataCollection;

// structure to store input information
typedef struct {
  unsigned int frequency;
  unsigned int amplitude;
  unsigned int sampling_rate;
} InputCondition;

// digital input pins
#define MOTOR_RPM_PIN 2   // Must be digital pin with interrupt
// analog input pins
#define VOLTAGE_PIN 4
// analog output pin
#define PWM_PIN 7
// sd card pin
#define SD_CS_PIN 4     // TODO: Understand which pin is this

#define MAX_SAMPLING 600
#define ENCODER_TOOTH 15

#define ERROR_LENGTH 100

#define MAX_FREQUENCY   4000
#define MIN_FREQUENCY   2000
#define MAX_AMPLITUDE   12
#define MIN_AMPLITUDE   8
#define MAX_SAMPLE_RATE 600
#define MIN_SAMPLE_RATE 1

#define DIR_LENGTH 10
#define FILE_LENGTH 13
#define DIR_PATH_LENGTH 11
#define FILE_PATH_LENGTH 25

#endif /* SRC_TYPEDEFINITIONS_H_ */
