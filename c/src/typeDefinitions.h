#ifndef SRC_TYPEDEFINITIONS_H_
#define SRC_TYPEDEFINITIONS_H_

#include <Arduino.h>
#include <SD.h>

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

/* Global Variables */
extern volatile DeviceState state_;  //!< microcontroller state
extern String error_msg_;

// Input information
extern volatile InputCondition input_condition_;      //!< current input condition
extern volatile unsigned int multiply_factor_;        //!< multiply factor for calculating rpm
extern volatile InputCondition new_input_condition_;  //!< new input condition

// Data to be written
extern volatile DataCollection collected_data_;
extern volatile unsigned long r_motor_rpm_count_;       //!< total # of pulse by ISR for motor rpm
extern volatile unsigned long r_motor_rpm_;             //!< plase to store pulse reading for motor rpm
extern volatile unsigned long r_motor_feedback_count_;  //!< total # of pulse by ISR for feedback rpm
extern volatile unsigned long r_motor_feedback_rpm_;    //!< plase to store pulse reading for feedback rpm
extern volatile unsigned long r_input_rpm_count_;       //!< total # of pulse by ISR for input rpm
extern volatile unsigned long r_input_rpm_;             //!< plase to store pulse reading for input rpm
extern volatile unsigned long r_output_rpm_count_;      //!< total # of pulse by ISR for output rpm
extern volatile unsigned long r_output_rpm_;            //!< plase to store pulse reading for output rpm
// Timer related variables
extern volatile unsigned int p_data_collection_;   //!< data collection period in 100ms
extern volatile unsigned int p_motor_control_;     //!< motor control period in 100ms
extern volatile bool f_data_collection_;           //!< data collection task flag
extern volatile bool f_motor_control_;             //!< motor control task flag

// SD Card
extern char sd_card_dir_path_[DIR_PATH_LENGTH];       //!< store path to current directory
extern char sd_card_file_path_[FILE_PATH_LENGTH];     //!< store path to current file
extern volatile unsigned int file_index_;             //!< index of file number
extern File sd_card_file_;                            //!< File object

// Flags
extern volatile bool f_record_request_;         ///< record request task flag
extern volatile bool f_pause_request_;          ///< pause request task flag
extern volatile bool f_complete_request_;       ///< complete request task flag
extern volatile bool f_motor_speed_request_;    ///< change motor speed request task flag
extern volatile bool f_amplitude_request_;      ///< change amplitude request task flag
extern volatile bool f_sampling_rate_request_;  ///< sampling rate change request task flag

#endif /* SRC_TYPEDEFINITIONS_H_ */
