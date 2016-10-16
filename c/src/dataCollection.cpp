#include "dataCollection.h"

extern volatile unsigned long raw_motor_rpm_count_;
extern volatile unsigned long raw_motor_speed_;
extern volatile unsigned int raw_voltage_;

extern volatile bool f_record_request_;
extern volatile bool f_pause_request_;
extern volatile bool f_complete_request_;
extern volatile bool f_motor_speed_request_;
extern volatile bool f_amplitude_request_;
extern volatile bool f_sampling_rate_request_;

extern volatile unsigned int p_data_collection_;
extern volatile unsigned int p_motor_control_;
extern volatile bool f_data_collection_;
extern volatile bool f_motor_control_;
extern volatile InputCondition input_condition_;
extern volatile unsigned int multiply_factor_;
extern volatile DataCollection collected_data_;

// TEMP
extern String motor_speed_rpm;


void timerCallback()
{
  static unsigned int data_collection_count = 0;
  static unsigned int motor_control_count = 0;

  data_collection_count++;
  motor_control_count++;

  if (data_collection_count == p_data_collection_)
  {
    data_collection_count = 0;
    f_data_collection_ = true;

    raw_motor_speed_ = raw_motor_rpm_count_;
    raw_motor_rpm_count_ = 0;

    collected_data_.timestamp++;
    collected_data_.generated_voltage = analogRead(VOLTAGE_PIN);
    collected_data_.motor_rpm = (raw_motor_speed_ * multiply_factor_) / ENCODER_TOOTH;
  }

  if (motor_control_count == p_motor_control_)
  {
    motor_control_count = 0;
    f_motor_control_ = true;
  }
}

void MotorRpmCountISR()
{
  raw_motor_rpm_count_++;
}



unsigned int getMotorRPM()
{

}



unsigned int getVoltage()
{

}



void checkSerialInterrupt()
{
  if (f_record_request_)
  {
    f_record_request_ = false;
    Serial.write('A');
  }

  if (f_pause_request_)
  {
    f_pause_request_ = false;
    Serial.write('A');
  }

  if (f_complete_request_)
  {
    f_complete_request_ = false;
    Serial.write('A');
  }

  if(f_motor_speed_request_)
  {
    f_motor_speed_request_ = false;
    Serial.write('A');
  }

  if (f_amplitude_request_)
  {
    f_amplitude_request_ = false;
    Serial.write('A');
  }

  if (f_sampling_rate_request_)
  {
    f_sampling_rate_request_ = false;
    Serial.write('A');
  }

}



bool changeMotorTargetSpeed(unsigned int target_speed)
{
}

bool startMotor()
{
}

bool stopMotor()
{
}

void motorSpeedControlPID()
{
}


