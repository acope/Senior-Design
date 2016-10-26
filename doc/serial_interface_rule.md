## Serial Communication Interface Definition

This document defines the rules for serial communication between microcontroller and computer.

Revision: 1.3

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
- MC shall send *time stamp* in unsigned long format (4 bytes).
- MC shall send *motor rpm* reading in unsigned int format (2 bytes).
- MC shall send *input rpm* reading in unsigned int format (2 bytes).
- MC shall send *output rpm* reading in unsigned int format (2 bytes).
- MC shall send *measured voltage* in unsigned int format (2 bytes).
- MC shall send **"E"** to indicate end of data transmission.
- PC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Indicating ERROR State
- MC shall send **"Z"** to PC to indicate ERROR state.
- MC shall send error message to PC in ASC2 string format.
- MC shall send **"E"** to indicate end of message.
- PC shall perform **TBD**

#### Indicating Normal State
- MC shall send **"N"** to PC to indicate MC is back to normal state.
- PC shall acknowledge with **"A"**.

### Computer (PC) Initiated

#### Start Data Recording Request
- PC shall send **"R"** to start recording request.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Pause Data Recording Request
- PC shall send **"P"** to pause recording request.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Complete Testing Request
- PC shall send **"C"** to request end of testing.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Sampling Rate Request
- PC shall send **"X"** to indicate sampling rate request.
- Sampling rate can be 100ms to 60 sec (interval of every 100ms).
- PC shall send in value between 1 (100ms) to 600 (60 sec) in unsigned int format (2 bytes).
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Motor Speed Request
- PC shall send **"M"** to indicate motor change request.
- PC shall send value in unsigned int format (2 bytes).
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Amplitude Request
- PC shall send **"D"** to indicate motor change request.
- PC shall send value in unsigned int format (2 bytes).
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

### Note
- If additional definitions are needed, create a issue or email to discuss.
- Due to Ardulink Library used by computer side code, microcontroller must send **255** or **0xFF** at end of serial TX to [prevent Ardulink from getting into buffer overflow](http://stackoverflow.com/questions/27654046/confirming-message-received-from-ardulinks-sendcustommessage-through-serial-r). 

