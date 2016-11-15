/**************************************************************
 * Copyright (C) 2016 by Wave Water Works                     *
 *               Developed by Oakland University              *
 *                                                            *
 *  This file is part of Oscillo Drive Microcontroller        *
 *  Source code developed for Wave Water Works                *
 *                                                            *
 **************************************************************/

#include "dataCollection.h"

void timerCallback()
{
  static unsigned int data_collection_count = 0;
  static unsigned int motor_control_count = 0;
  static unsigned int error_check_count = 0;

  // increment counters
  data_collection_count++;
  motor_control_count++;
  error_check_count++;

  // time to check measurements
  if (data_collection_count >= input_condition_.sampling_rate)
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
    collected_data_.motor_rpm = (int)((float)(r_motor_rpm_) * (encoder_time_scale_ / motor_encoder_tooth_));
    collected_data_.input_rpm = (int)((float)(r_input_rpm_) * (encoder_time_scale_ / odrive_encoder_tooth_));
    collected_data_.output_rpm = (int)((float)(r_output_rpm_) * (encoder_time_scale_ / odrive_encoder_tooth_));
  }

  // time to check motor control
  if (motor_control_count >= p_motor_control_)
  {
    motor_control_count = 0;
    f_motor_control_ = true;
    r_motor_feedback_rpm_ = r_motor_feedback_count_;
    r_motor_feedback_count_ = 0;
  }

  // time to check error
  if (error_check_count >= (p_error_check_ * input_condition_.sampling_rate) )
  {
    error_check_count = 0;
    // FIXME: Test once all connected
    //f_error_check_ = true;
  }
}

void checkTimerTasks()
{
  if (f_data_collection_)
  {
    f_data_collection_ = false;
    if (state_ == recording)
    {
    sendDataSerial();
    sendDataSD();
    }
  }

  if (f_motor_control_)
  {
    f_motor_control_ = false;
    motorSpeedControlPID();
  }

  if (f_error_check_)
  {
    f_error_check_ = false;
    errorCheck();
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
    if (restart_init_)
    {
      initializeSD();
    }
    sendInputSD();
    OpenSDFile();
    Serial.write('A');
    Serial.write(255);
    state_ = prepare;
  }

  if (f_pause_request_)
  {
    // Not implemented as Computer side don't have this feature
    f_pause_request_ = false;
    Serial.write('F');
    Serial.write(255);
  }

  if (f_complete_request_)
  {
    f_complete_request_ = false;
    stopMotor();
    CloseSDFile();
    Serial.write('A');
    Serial.write(255);
    restart_pid_ = true;
    restart_sd_ = true;
    restart_init_ = true;
    sd_card_new_file_count_ = 0;
    state_ = done;
  }

  if(f_motor_speed_request_)
  {
    f_motor_speed_request_ = false;
    if (new_input_condition_.frequency <= MAX_FREQUENCY &&
        new_input_condition_.frequency >= MIN_FREQUENCY)
    {
      Serial.write('A');
      Serial.write(255);
      input_condition_.frequency = new_input_condition_.frequency;
      sendInputSD();
    }
    else
      Serial.write('F');
      Serial.write(255);
  }

  if (f_amplitude_request_)
  {
    f_amplitude_request_ = false;
    if (new_input_condition_.amplitude <= MAX_AMPLITUDE &&
        new_input_condition_.amplitude >= MIN_AMPLITUDE)
    {
      Serial.write('A');
      Serial.write(255);
      input_condition_.amplitude = new_input_condition_.amplitude;
      sendInputSD();
    }
    else
      Serial.write('F');
      Serial.write(255);
  }

  if (f_sampling_rate_request_)
  {
    f_sampling_rate_request_ = false;
    if (new_input_condition_.sampling_rate <= MAX_SAMPLE_RATE &&
        new_input_condition_.sampling_rate >= MIN_SAMPLE_RATE)
    {
      Serial.write('A');
      Serial.write(255);
      input_condition_.sampling_rate = new_input_condition_.sampling_rate;
      encoder_time_scale_ = 600.0 / (float)input_condition_.sampling_rate;
      sendInputSD();
    }
    Serial.write('A');
    Serial.write(255);
  }

}

bool startMotor()
{
  // no motor control here. Let PID control motor speed
  // for safety.
  analogWrite(PWM_PIN, 0);
  return true;
}

bool stopMotor()
{
  analogWrite(PWM_PIN, 0);
  return true;
}

void motorSpeedControlPID()
{
  // TODO: Increase max if system can handle
  static const float kp = 1.5;
  static const float ki = 1.8;
  static const float kd = 0.1;
  static const float out_min = 60.0;
  static const float out_max = 170.0;
  static const float max_rpm = 3000.0;
  static float prev_feedback = 0;
  float output;
  char output_cmd;
  static bool clear_PID = false;

  // sample rate is 200ms, so multiply by 5 * 60 then divide by tooth
  // 5 * 60 / 15 = 20
  float feedback = (float)r_motor_feedback_rpm_ * 20.0;

  // slow start up of motor untill reach near speed
  if (state_ == prepare)
  {
    // speed up motor slowly
    static unsigned int motor_speed_unit = 200;
    static unsigned int motor_speed = 300;

    // when restarting, clear static variables
    if (restart_pid_)
    {
      prev_feedback = 0;
      motor_speed = 300;
      restart_pid_ = false;
      f_start_pid_ = false;
      clear_PID = true;
    }

    // if motor is near target, move to pid
    if (abs(input_condition_.frequency - feedback) <= 150)
    {
      f_start_pid_ = true;
    }
    // if motor is near temp target, move to next temp target
    else if (abs(motor_speed - feedback) <= 150)
    {
      motor_speed += motor_speed_unit;
    }
    output = (float)motor_speed;
  }
  else // recording (PID Control)
  {
    // static Variables
    static float i_error = 0;
    static float d_error = 0;

    if (clear_PID)
    {
      i_error = 0;
      d_error = 0;
      clear_PID = false;
    }

    float error = (float)input_condition_.frequency - feedback;
    i_error += (ki * error);

    if (i_error > out_max)
    {
      i_error = (float)out_max;
    }
    else if (i_error < out_min)
    {
      i_error = (float)out_min;
    }

    d_error = (feedback - prev_feedback);

    // compute output
    output = (kp * error + i_error - kd * d_error) + (float)input_condition_.frequency;
  }

  // Scale
  //output = (out_max - out_min) / max_rpm * output + out_min + offset;
  output = (out_max - out_min) / max_rpm * output + out_min + 3;

  // check with limit
  if (output > out_max)
  {
    output = out_max;
  }
  else if (output < out_min)
  {
    output = out_min;
  }

  // convert type
  output_cmd = (char)output;

  // set output
  analogWrite(PWM_PIN, output_cmd);
}


bool sendDataSerial()
{
  Serial.write('S');
  Serial.print(collected_data_.timestamp, DEC);
  Serial.print(",");
  Serial.print(collected_data_.motor_rpm, DEC);
  Serial.print(",");
  Serial.print(collected_data_.input_rpm, DEC);
  Serial.print(",");
  Serial.print(collected_data_.output_rpm, DEC);
  Serial.print(",");
  Serial.print(collected_data_.generated_voltage, DEC);
  Serial.write('E');
  Serial.write(255);

  return true;
}

bool sendInputSD()
{
  static bool first_time = true;
  sd_card_input_ = SD.open(sd_card_input_path_, FILE_WRITE);

  if (first_time || restart_sd_)
  {
    first_time = false;
    restart_sd_ = false;
    sd_card_input_.write("TIMESTAMP, FREQUENCY, AMPLITUDE, SAMPLE_RATE,\n");
  }

  sd_card_input_.print(collected_data_.timestamp, DEC);
  sd_card_input_.write(",");
  sd_card_input_.print(input_condition_.frequency, DEC);
  sd_card_input_.write(",");
  sd_card_input_.print(input_condition_.amplitude, DEC);
  sd_card_input_.write(",");
  sd_card_input_.print(input_condition_.sampling_rate, DEC);
  sd_card_input_.println(",\n");
  sd_card_input_.close();
  return true;
}

bool sendDataSD()
{
  sd_card_new_file_count_++;
  if (sd_card_new_file_count_ == SD_CARD_RECORD_PER_FILE)
  {
   sd_card_new_file_count_ = 0;
   updateSDFile();
  }

  sd_card_file_.print(collected_data_.timestamp, DEC);
  sd_card_file_.write(",");
  sd_card_file_.print(collected_data_.motor_rpm, DEC);
  sd_card_file_.write(",");
  sd_card_file_.print(collected_data_.input_rpm, DEC);
  sd_card_file_.write(",");
  sd_card_file_.print(collected_data_.output_rpm, DEC);
  sd_card_file_.write(",");
  sd_card_file_.print(collected_data_.generated_voltage, DEC);
  sd_card_file_.write(",\n");

  return true;
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

  sd_card_file_ = SD.open(sd_card_file_path_, FILE_WRITE);
  sd_card_file_.write("TIMESTAMP, MOTOR_RPM, INPUT_RPM, OUTPUT_RPM, VOLTAGE,\n");
}

// When motor is spinning, and recording is not collected for error_max consecutive
// times, then send error message.
bool errorCheck()
{
  const int error_max = 10;
  static int motor_err_count = 0;
  static int input_err_count = 0;
  static int output_err_count = 0;
  static int voltage_err_count = 0;

  if (state_ == recording && input_condition_.frequency >= 300)
  {
    // motor encoder
    if (collected_data_.motor_rpm == 0)
    {
      motor_err_count++;
      if (motor_err_count >= error_max)
      {
      state_ = error;
      Serial.write('Z');
      Serial.print("motor is not spinning...");
      Serial.write('E');
      Serial.write(255);
      return false;
      }
    }
    else
    {
      motor_err_count = 0;
    }

    // input odrive
    if (collected_data_.input_rpm == 0)
    {
      input_err_count++;
      if (input_err_count >= error_max)
      {
      state_ = error;
      Serial.write('Z');
      Serial.print("input oscillo drive not spinnig...");
      Serial.write('E');
      Serial.write(255);
      return false;
      }
    }
    else
    {
      input_err_count = 0;
    }

    // output odrive
    if (collected_data_.output_rpm == 0)
    {
      output_err_count++;
      if (output_err_count >= error_max)
      {
      state_ = error;
      Serial.write('Z');
      Serial.print("input oscillo drive not spinnig...");
      Serial.write('E');
      Serial.write(255);
      return false;
      }
    }
    else
    {
      output_err_count = 0;
    }

    // voltage
    if (collected_data_.generated_voltage == 0)
    {
      voltage_err_count++;
      if (voltage_err_count == error_max)
      {
      state_ = error;
      Serial.write('Z');
      Serial.print("voltage not generated...");
      Serial.write('E');
      Serial.write(255);
      return false;
      }
    }
    else
    {
      voltage_err_count = 0;
    }
  }
  return true;
}
