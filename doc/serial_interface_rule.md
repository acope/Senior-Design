## Serial Communication Interface Definition

This document defines the rules for serial communication between microcontroller and computer.

Revision: 1.1

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
- Order of data and length will be **TBD** based on sensor selection.
- MC shall send **"E"** to indicate end of data transmission.
- PC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Indicating ERROR State
- MC shall send **"Z"** to PC to indicate ERROR state.
- Action for PC will be **TBD**.

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

#### Change Motor Speed Request
- PC shall send **"M"** to indicate motor change request.
- Details of data and length will be **TBD** based on motor selection.
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

#### Change Amplitude Request (Optional)
- PC shall send **"D"** to indicate motor change request.
- Details of data and length will be **TBD** based on motor selection
- PC shall send **"E"** to indicate end of transmission.
- MC shall respond with **"A"** or **"F"** to indicate acknowledge or fail.

### Note
If additional definitions are needed, create a issue or email to discuss.
