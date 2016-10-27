#include "dataCollection.h"

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

    r_motor_rpm_ = r_motor_rpm_count_;
    r_motor_rpm_count_ = 0;
    r_input_rpm_ = r_input_rpm_count_;
    r_input_rpm_count_ = 0;
    r_output_rpm_ = r_output_rpm_count_;
    r_output_rpm_count_ = 0;

    collected_data_.timestamp++;
    collected_data_.generated_voltage = analogRead(VOLTAGE_PIN);
    collected_data_.motor_rpm = (r_motor_rpm_ * multiply_factor_) / MOTOR_ENCODER_TOOTH;
    collected_data_.input_rpm = (r_input_rpm_ * multiply_factor_) / ODRIVE_ENCODER_TOOTH;
    collected_data_.output_rpm = (r_output_rpm_ * multiply_factor_) / ODRIVE_ENCODER_TOOTH;
  }

  if (motor_control_count == p_motor_control_)
  {
    motor_control_count = 0;
    f_motor_control_ = true;
    r_motor_feedback_rpm_ = r_motor_feedback_count_;
    r_motor_feedback_count_ = 0;
  }
}

void checkTimerTasks()
{
  if (f_data_collection_ && state_ != ready)
  {
    f_data_collection_ = false;
    sendDataSerial();
    sendDataSD();
  }

  if (f_motor_control_)
  {
    f_motor_control_ = false;
    motorSpeedControlPID();
  }
}

void MotorRpmCountISR()
{
  r_motor_rpm_count_++;
  r_motor_feedback_count_++;
}

void InputRpmCountISR()
{
  r_input_rpm_count_++;
}

void OutputRpmCountISR()
{
  r_output_rpm_count_++;
}

void checkSerialInterrupt()
{
  if (f_record_request_)
  {
    f_record_request_ = false;
    startMotor();
    OpenSDFile();
    Serial.write('A');
    state_ = prepare;
  }

  if (f_pause_request_)
  {
    // Not implemented as Computer side don't have this feature
    f_pause_request_ = false;
    Serial.write('F');
  }

  if (f_complete_request_)
  {
    f_complete_request_ = false;
    stopMotor();
    CloseSDFile();
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
      multiply_factor_ = MAX_SAMPLE_RATE - input_condition_.sampling_rate;
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
  byte buf[12];
  buf[0] = collected_data_.timestamp & 255;
  buf[1] = (collected_data_.timestamp >> 8)  & 255;
  buf[2] = (collected_data_.timestamp >> 16) & 255;
  buf[3] = (collected_data_.timestamp >> 24) & 255;
  buf[4] = (collected_data_.motor_rpm) & 255;
  buf[5] = (collected_data_.motor_rpm >> 8) & 255;
  buf[6] = (collected_data_.input_rpm) & 255;
  buf[7] = (collected_data_.input_rpm >> 8) & 255;
  buf[8] = (collected_data_.output_rpm) & 255;
  buf[9] = (collected_data_.output_rpm >> 8) & 255;
  buf[10] = (collected_data_.generated_voltage) & 255;
  buf[11] = (collected_data_.generated_voltage >> 6) && 255;

  Serial.write('S');
  Serial.write(buf, 12);
  Serial.write('E');
  return true;
}

bool sendInputSD()
{
}


bool sendDataSD()
{
}

void OpenSDFile()
{
  sd_card_file_ = SD.open(sd_card_file_path_, FILE_WRITE);
}

void CloseSDFile()
{
  sd_card_file_.close();
}

void updateSDFile()
{
  sd_card_file_.close();
  file_index_++;
  sprintf(sd_card_file_path_, "%s/RUN%04d.csv", sd_card_dir_path_, file_index_);
  sd_card_file_ = SD.open(sd_card_dir_path_, FILE_WRITE);
}
