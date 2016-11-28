# Wave Water Works Oscillo Drive Test Unit

This repository hosts source code of the software programs used in Wave Water Works Oscillo Drive Test Unit as part of 2016 Fall Semester Senior Design Course at Oakland University. Target of this project is to test the durability of Oscillo Drive. The test unit is capable of controlling frequency and amplitude of input signal, and collect motor RPM, input & output RPM of Oscillo Drive, generator RPM, generated voltage, generated current, and generated power. For robustness, collected data is saved in two distict location.

- **Relase Version:** v1.5
- **Authers:** Oakland Univeristy 2016 Fall Senior Design Group 9 and 10
- **License:** Proprietary to Wave Water Works

|   Program         |  Platform         |   Programming Language |  Folder   |
|:-----------------:|:-----------------:|:----------------------:|:---------:|
| Test Unit Control | Arduino Mega      |        C               |     c     |
| User Interface    | Computer (Windows)|       Java             |    java   |

## Features

- Easy Graphical User Interface to Connect and Control Microcontroller
- Accurate Motor Control utilizing PID feedback controller
- Collects Motor RPM, Input RPM, Output RPM, Voltage, Current, and Power
- User Defined Sampling Rate between 100 ms to 60 seconds
- Backup Data Saved on On-Board SD Card with Post-Process Script

## How-to Guide

### How to Conduct Experiment
1. Connect USB cable from Test Unit to the computer
2. Power on Test Unit power
3. Open graphical user interface on computer
4. Search for Arduino COM port and connect
5. Select frequency of input
6. Select amplitude of input
7. Select sampling rate of measurements
8. Click "Start" to begin experiment
9. Once experiment is completed, click "Stop"

### How to View Collected data
##### Data 1
First copy of measurements data is saved on same folder where graphical user interface is located. The file is in Microsoft Excel format. User can opne the file and start analyzing the data. This is primary source of data.

##### Data 2
Backup copy of measurements data is saved on SD card attached to Arduino Mega. Please take SD card, and attach to Windows computer. There will be Microsoft Excel file included in SD card, which will guide through how to perform post-processing of data. Please periodically delete the test results.

## Setup Instruction

#### Arduino Mega Software Development Environment

The development of Arduino Mesga software code is done using ```Atom``` Editor and ```Platform IO```. The reason of using these tools instead of default Arduino IDE is listed below.

- Easy to manage multiple source files and header files
- Take advantage of auto-completion
- Able to utilize useful Atom plugins for development

The following section walk you through how to create development environment.

1. Go to [Platform IO Website](http://platformio.org/platformio-ide) and click ```Download``` button
2. Select Operating System (OS)
3. Follow the instruction of website to install

Next, Set up environment to use Arduino Mesga ADK

1. Go to ```Platform IO Home``` shown below, and select ```New Project```

  ![PlatformID Home](c/doc/img/platformio-home.png)

2. Select ```Arduino Mega ADK``` as ```platform``` and choose development folder

  ![Initialize](c/doc/img/initialize.png)

3. New folder structure will be created and you are ready to develop

  ![Folder Structure](c/doc/img/created.png)


#### Desktop Software Development Environment

The Java code was originally developed with the IDE Netbeans v8.1 using Java JDK 1.8 32bit.
The continuation of the Java code can be continued from from any prefered IDE.

The following external libraries are needed for running and compilation of the code. They can be found in the respository under the ```Java``` section ```lib```
1. Ardulink-v0.6.1
2. dom4j-1.6.1
3. jfreechart-1.0.19
4. miglayout-4.0
5. poi-3.15
6. rxtx-2.1-7-bins-r2
7. xmlbeans-2.3.0

## How to Compile from Source

#### Arduino Mega Code

1. Clone ```sd_www_odtu``` repository into local folder.
2. Open Atom with Platform IO
3. Open ```Platform IO Home``` and select ```Open Project```
4. Select ```c``` folder inside repository.
5. Go to ```PlatformIO``` -> ```Build```
6. Go to ```PlatformIO``` -> ```Upload```
7. Arduino is ready to be used

For the reference ```PlatformIO``` option looks like following

![PlatformIO Tab](c/doc/img/tab.png)

#### Java Code

To compile the code the easiest method is with an IDE. 
To build in Netbeans
1. Make sure no errors exist within the project or else the project will not build
2. Press ```Clean and Build Project```, either from the toolbar or under the ```Run```
3. If the project has been built with no errors in the folder that the project was created under in Netbeans and new folder called ```dist``` should appear with the jar file and dependencies.

## Troubleshooting
The Java code has a built in logging function that is used for debugging when running. Currently this function is only available during real time operation, hopefully in future imporvements a log file can be created. 
The logs can only be seen when the program is started with a command window
To run the code with the command window perform the following:
1. Open a command window and CD to location of the ODrive.jar file or open a file browser to the location of the ODrive.jar and hold down shift and right mouse click on an empty area within the window and press ```Open command window here```
2. In the command window type ```Java -jar ODrive.jar```

## Known Issues
There is one know issue is the code, when the Java programming is running on a 64bit JVM system when the disconnect button is pressed the program freezes. No known way to fix this issue believe it is withing the DLL files included in the program. To prevent this issue simply close the GUI without pressing disconnect, the program will automatically disconnect from the Arduino
