## Serial Communication Interface Definition

This document defines the rules for serial communication between microcontroller and computer.

Revision: 2.0

### Serial Communication Specification
- Communication Speed: 115200 bps
- Data bit: 8
- Parity bit: none
- Stop bit: 1
- Format: 8N1 (typically, this is default settings)

### Microcontroller(MC) Initiated

#### Connection Test
- MC shall send **"T"** untill get response from PC.
- PC shall respond with **"A"** to acknowledge.

#### Ready to Collect Data
- MC shall send **"G"** to indicate ready to collect data.
- PC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Sending Recorded Data
- MC shall send **"S"** to indicate start of data transmission.
- MC shall send following data in **ASC2** format with **comma separation**.
	1. Time stamp
	2. Motor rpm
	3. Input rpm
	4. Output rpm
	5. Measured voltage
- MC shall send **"E"** to indicate end of data transmission.
- PC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

##### Data Format
- Timestamp doesn't need any conversion
- Motor RPM doesn't need any conversion (converted at Arduino)
- Input RPM needs conversion: RPM = raw_data * (60 / sample_rate) / 360
- Output RPM needs conversion: RPM = raw_data * (60 / sample_rate) / 360
- Voltage needs conversion: Voltage = raw_reading * 5 * 14.5 / 1024

#### Indicating ERROR State
- MC shall send **"Z"** to PC to indicate ERROR state.
- MC shall send error message to PC in ASC2 string format.
- MC shall send **"E"** to indicate end of message.

#### Indicating Normal State
- MC shall send **"N"** to PC to indicate MC is back to normal state.
- PC shall acknowledge with **"A"**.

### Computer (PC) Initiated

#### Start Data Recording Request
- PC shall send **"R"** to start recording request.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Pause Data Recording Request (NOT IMPLEMENTED)
- PC shall send **"P"** to pause recording request.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Complete Testing Request
- PC shall send **"C"** to request end of testing.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Sampling Rate Request
- PC shall send **"X"** to indicate sampling rate request.
- Sampling rate can be 100ms to 60 sec (interval of every 100ms).
- PC shall send in value between 1 (100ms) to 600 (60 sec) in **4 digit ASC2 string**.
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Motor Speed Request
- PC shall send **"M"** to indicate motor change request.
- PC shall send in value between 0 RPM and 3000 RPM in **4 digit ASC2 string**.
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Amplitude Request
- PC shall send **"D"** to indicate motor change request.
- PC shall send in value between 90 degree and 120 degree in **4 digit ASC2 string**.
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Alive Checking Signal
- PC shall send **"Q"** periodically to Arduino to inform that GUI is working.
- MC shall monitor this message, and stop motor if MC doesn't receive signal for 5 consecutive times.
- MC shall enter error state and sends error message to PC indicates the aliva signal check fail.

### Note
- If additional definitions are needed, create a issue or email to discuss.
- Due to Ardulink Library used by computer side code, microcontroller must send **255** or **0xFF** at end of serial TX to [prevent Ardulink from getting into buffer overflow](http://stackoverflow.com/questions/27654046/confirming-message-received-from-ardulinks-sendcustommessage-through-serial-r). 
- ASC2 will be used for all data transmission as Ardulink Library only implements sending data as string.
