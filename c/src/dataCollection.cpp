#include "dataCollection.h"

extern volatile DeviceState state_;

extern volatile unsigned long raw_motor_rpm_count_;
extern volatile unsigned long raw_motor_speed_;
extern volatile unsigned long raw_motor_feedback_count_;
extern volatile unsigned long raw_motor_feedback_;

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
extern volatile InputCondition new_input_condition_;
extern volatile unsigned int multiply_factor_;
extern volatile DataCollection collected_data_;


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

void checkTimerTasks()
{
  if (f_data_collection_)
  {
    f_data_collection_ = false;
    sendDataSerial();
    sendDataSPI();
  }

  if (f_motor_control_)
  {
    f_motor_control_ = false;
    raw_motor_feedback_ = raw_motor_feedback_count_;
    raw_motor_feedback_count_ = 0;
    motorSpeedControlPID();
  }
}

void MotorRpmCountISR()
{
  raw_motor_rpm_count_++;
  raw_motor_feedback_count_++;
}

void checkSerialInterrupt()
{
  if (f_record_request_)
  {
    f_record_request_ = false;
    Serial.write('A');
    state_ = recording;
  }

  if (f_pause_request_)
  {
    f_pause_request_ = false;
    Serial.write('A');
    state_ = ready;
  }

  if (f_complete_request_)
  {
    f_complete_request_ = false;
    Serial.write('A');
    state_ = done;
  }

  if(f_motor_speed_request_)
  {
    f_motor_speed_request_ = false;
    if (new_input_condition_.frequency <= MAX_FREQUENCY &&
        new_input_condition_.frequency >= MIN_FREQUENCY)
    {
      Serial.write('A');
      input_condition_.frequency = new_input_condition_.frequency;
//      sendInputSPI();
    }
    else
      Serial.write('F');
  }

  if (f_amplitude_request_)
  {
    f_amplitude_request_ = false;
    if (new_input_condition_.amplitude <= MAX_AMPLITUDE &&
        new_input_condition_.amplitude >= MIN_AMPLITUDE)
    {
      Serial.write('A');
      input_condition_.amplitude = new_input_condition_.amplitude;
//      sendInputSPI();
    }
    else
      Serial.write('F');
  }

  if (f_sampling_rate_request_)
  {
    f_sampling_rate_request_ = false;
    if (new_input_condition_.sampling_rate <= MAX_SAMPLE_RATE &&
        new_input_condition_.sampling_rate >= MIN_SAMPLE_RATE)
    {
      Serial.write('A');
      input_condition_.sampling_rate = new_input_condition_.sampling_rate;
//      sendInputSPI();
    }
    Serial.write('A');
  }

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


bool sendDataSerial()
{
  byte buf[8];
  buf[0] = collected_data_.timestamp & 255;
  buf[1] = (collected_data_.timestamp >> 8)  & 255;
  buf[2] = (collected_data_.timestamp >> 16) & 255;
  buf[3] = (collected_data_.timestamp >> 24) & 255;
  buf[4] = (collected_data_.motor_rpm) & 255;
  buf[5] = (collected_data_.motor_rpm >> 8) & 255;
  buf[6] = (collected_data_.generated_voltage) & 255;
  buf[7] = (collected_data_.generated_voltage >> 6) && 255;

  Serial.write('S');
  Serial.write(buf, 8);
  Serial.write('E');
  return true;
}

bool sendInputSPI()
{
}


bool sendDataSPI()
{
}
