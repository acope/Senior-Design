#include "initialization.h"

// Serial
const unsigned long WAIT_TIME_MS = 500;
const unsigned long UART_SPEED = 115200;
const unsigned long TIMER_US = 100000; // 100 ms

bool initializeBoard()
{
  bool isInitialized = true;

  // setup and test PC connection
  isInitialized &= initializeSerial(UART_SPEED);

  // setup and test SD card connection
 isInitialized &= initializeSD();

  // Make sure no interrupt happens
  noInterrupts();

  // initialize digital pin
  pinMode(MOTOR_RPM_PIN, INPUT);
  pinMode(INPUT_RPM_PIN, INPUT);
  pinMode(OUTPUT_RPM_PIN, INPUT);

  // setup interrupt for rpm reading pin
  attachInterrupt(digitalPinToInterrupt(MOTOR_RPM_PIN), MotorRpmCountISR, RISING);
  attachInterrupt(digitalPinToInterrupt(INPUT_RPM_PIN), InputRpmCountISR, RISING);
  attachInterrupt(digitalPinToInterrupt(OUTPUT_RPM_PIN), OutputRpmCountISR, RISING);

  // setup PWM
  pinMode(PWM_PIN, OUTPUT);
  analogWrite(PWM_PIN, 0);

  // setup analog pin (Test read)
  analogReference(DEFAULT);
  analogRead(VOLTAGE_PIN);

  // set default input information
  input_condition_.amplitude = 4;
  input_condition_.frequency = 10;
  input_condition_.sampling_rate = 10;
  multiply_factor_ = MAX_SAMPLE_RATE - input_condition_.sampling_rate;
  collected_data_.timestamp = 0;

  // Timer setup
  Timer1.initialize(TIMER_US);
  Timer1.attachInterrupt(timerCallback);

  // TODO: Test motor and read rom
//  isInitialized &= motorTestRun();

  // TODO: Watchdog (later)


  interrupts();

  // Check result of initialization
  if (!isInitialized)
  {
    // let PC know it is error
    state_ = error;
    Serial.write('Z');
    Serial.println(error_msg_);
  }
  else
  {
  // Ready
  state_ = ready;
  Serial.write('G');
  }

  return isInitialized;
}


bool initializeSerial(unsigned long speed)
{
  Serial.begin(speed);

  while (!Serial);      // wait untill serial is connected.

  // test connection
  while (true)
  {
    Serial.write('T');

    if (Serial.available() > 0)
    {
      char inChar;
      inChar = (char)Serial.read();
      if (inChar == 'A')
        return true;
    }
  }
}

void printDirectory(File dir, int numTabs) {
  while (true) {

    File entry =  dir.openNextFile();
    if (! entry) {
      // no more files
      break;
    }
    for (uint8_t i = 0; i < numTabs; i++) {
      Serial.print('\t');
    }
    Serial.print(entry.name());
    if (entry.isDirectory()) {
      Serial.println("/");
      printDirectory(entry, numTabs + 1);
    } else {
      // files have sizes, directories do not
      Serial.print("\t\t");
      Serial.println(entry.size(), DEC);
    }
    entry.close();
  }
}

// NOTE: SD card will create new file every 50,000 records
bool initializeSD()
{
  // Setup connection
  if (!SD.begin(SD_CS_PIN))
  {
    error_msg_ = "SD card initialization failed.";
    return false;
  }

  // Create new test folder
  unsigned int count = 1;
  File root;
  root = SD.open("/");
  char dirname[DIR_LENGTH];

  while (true)
  {
    sprintf(dirname, "TEST%03d", count);

    if (SD.exists(dirname))
      count++;
    else
      break;
  }

  Serial.println(dirname);

  if (!SD.mkdir(dirname))
  {
    error_msg_ = "Cannot create directory in SD card.";
    return false;
  }
  sprintf(sd_card_dir_path_, "/%s", dirname);
  // @TODO: Remove
  printDirectory(root, 0);

  file_index_ = 1;
  sprintf(sd_card_file_path_, "%s/RUN%04d.csv", sd_card_dir_path_, file_index_);

  return true;
}

bool motorTestRun()
{

}
