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

    // FIXME: Put Back
    collected_data_.timestamp++;
    collected_data_.generated_voltage = analogRead(VOLTAGE_PIN);
    collected_data_.motor_rpm = r_motor_rpm_;//(r_motor_rpm_ * multiply_factor_) / MOTOR_ENCODER_TOOTH;
    collected_data_.input_rpm = r_input_rpm_;//(r_input_rpm_ * multiply_factor_) / ODRIVE_ENCODER_TOOTH;
    collected_data_.output_rpm = r_output_rpm_;//(r_output_rpm_ * multiply_factor_) / ODRIVE_ENCODER_TOOTH;
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
    sendInputSD();
    OpenSDFile();
    Serial.write('A');
    // FIXME: Put Back
    state_ = recording;
    //state_ = prepare;
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
    restart_ = true;
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
      sendInputSD();
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
      sendInputSD();
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
      sendInputSD();
    }
    Serial.write('A');
  }

}

bool startMotor()
{
  analogWrite(PWM_PIN, input_condition_.frequency);
  return true;
}

bool stopMotor()
{
  analogWrite(PWM_PIN, 0);
  return true;
}

/// TODO: 5. Motor Control Implementation
void motorSpeedControlPID()
{

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
  //Serial.write(255);

  return true;
}

bool sendInputSD()
{
  static bool first_time = true;
  sd_card_input_ = SD.open(sd_card_input_path_, FILE_WRITE);

  if (first_time)
  {
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
  // FIXME: Remove
  Serial.println("INPUT TO SD CARD");
  return true;
}

bool sendDataSD()
{
  sd_card_new_file_count_++;
  // FIXME: Back the number
  if (sd_card_new_file_count_ == 20)//SD_CARD_RECORD_PER_FILE)
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
  // FIXME: Remove
  Serial.println("SD CARD written");

  return true;
}

// FIXME: All Serial from following functions.
void OpenSDFile()
{
  sd_card_file_ = SD.open(sd_card_file_path_, FILE_WRITE);
  Serial.println("SD FILE OPEN");
}

void CloseSDFile()
{
  sd_card_file_.close();
  Serial.println("SD FILE CLOSE");
}

void updateSDFile()
{
  sd_card_file_.close();
  file_index_++;
  sprintf(sd_card_file_path_, "%s/RUN%04d.csv", sd_card_dir_path_, file_index_);

  Serial.println(sd_card_dir_path_);
  Serial.println(sd_card_file_path_);

  sd_card_file_ = SD.open(sd_card_file_path_, FILE_WRITE);
  sd_card_file_.write("TIMESTAMP, MOTOR_RPM, INPUT_RPM, OUTPUT_RPM, VOLTAGE,\n");
  Serial.println("SD CARD FILE NEW ONE!");
}
