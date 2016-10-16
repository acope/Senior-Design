#ifndef SRC_TYPEDEFINITIONS_H_
#define SRC_TYPEDEFINITIONS_H_

#include <Arduino.h>

// indication of current state
typedef enum deviceState{
  initialization,
  ready,
  recording,
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
#define PWM_PIN 6
// sd card pin
#define SD_PIN 10         // Adafruit SD shields

#define MAX_SAMPLING 600
#define ENCODER_TOOTH 15

#endif /* SRC_TYPEDEFINITIONS_H_ */
