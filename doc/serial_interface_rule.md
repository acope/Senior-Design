## Serial Communication Interface Definition

This document defines the rules for serial communication between microcontroller and computer.

Revision: 1.0

### Microcontroller(MC) Initiated

#### Connection Test
- From MC, send **"T"** untill get response from PC.
- PC should response with **"A"** to acknowledge.

#### Ready to Collect Data
- From MC, send **"G"** to indicate ready to collect data
- PC should respond with **"A"** or **"F"** to indicate acknowledge or fail  

#### Sending Recorded Data
- From MC, send **"S"** to indicate start
- Order of data and length will be **TBD** based on sensor selection.
- From MC, send **"E"** to indicate end
- PC should respond with **"A"** or **"F"** to indicate acknowledge or fail  

#### Indicating ERROR STATE
- From MC, send **"Z"** to PC to indicate ERROR
- Action for PC will be **TBD**.

### Computer (PC) Initiated

#### Start Data Recording Request
- From PC, send **"R"** to start recording request
- MC should respond with **"A"** or **"F"** to indicate acknowledge or fail

#### Pause Data Recording Request
- From PC, send **"P"** to pause recording request
- MC should respond with **"A"** or **"F"** to indicate acknowledge or fail

#### Change Motor Speed Request
- From PC, send **"M"** to indicate motor change request is comming
- Details of data and length will be **TBD** based on motor selection
- From PC, send **"E"** to indicate end
- MC should respond with **"A"** or **"F"** to indicate acknowledge or fail

#### Change Amplitude Request (Optional)
- From PC, send **"D"** to indicate motor change request is comming
- Details of data and length will be **TBD** based on motor selection
- From PC, send **"E"** to indicate end
- MC should respond with **"A"** or **"F"** to indicate acknowledge or fail
