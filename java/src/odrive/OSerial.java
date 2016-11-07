package odrive;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
//Ardulink imports
//https://github.com/marcomauro/Arduino-SerialRead-Java
import org.zu.ardulink.Link;

/**
 * Controls the transmitting and receiving from serial port
 * @author Austin Copeman
 * @version 1.1
 */
//Need to find a better way than to extend to Controller for passing data back
public class OSerial extends Observable{
    private final Link link;
    private final NumberFormat formatter = new DecimalFormat("0000"); //For sending 4 digits to Arduino
    /**
     * Constructor, takes in link instance to communicate with serial
     * @param linkName 
     */
    public OSerial(String linkName){
        link = Link.getInstance(linkName);
    }
    
    /**
    * Sends motor RPM to serial
    * @param rpm
    * @return 
    */
    public boolean sendMotorRPM(int rpm){      
        link.writeSerial("M");
        link.writeSerial(formatter.format(rpm));
        link.writeSerial("E");
        return true;
    }
     
    /**
     * Sends amplitude to serial
     * @param amp
     * @return 
     */
    public boolean sendAmplitude(int amp){          
        link.writeSerial("D");
        link.writeSerial(formatter.format(amp));
        link.writeSerial("E");
        return true;
    }
    
        /**
     * Sends sampling rate to serial
     * @param samplingRate
     * @return 
     */
    public boolean sendSamplingRate(int samplingRate){    
        link.writeSerial("X");
        link.writeSerial(formatter.format(samplingRate));
        link.writeSerial("E");
        return true;
    }
    
    /**
     * Cases for received state from Arduino
     * 
     * @param str 
     */
    public void serialArduinoEvent(String str){
        //Retrieve first character from sting
        //First char is always event notification
        char event = str.charAt(0);

        switch (event){
            //Acknowledge
            case 'A':
                break;               
            //Fail
            case 'F':
                break;            
            //Connection Test
            case 'T':
                link.writeSerial("A");
                break;              
            //Read to collect data
            case 'G':
                link.writeSerial("A");
                break;          
            //Sending recorded data(raw data recieved from Arduino)
            case 'S':               
                //Flag for oberserver notifying that there was a change
                setChanged();
                //Tells that there was a change
                notifyObservers(str);
                //rawArduinoData(arduinoData); 
                break;
            //Error state
            case 'Z':
                setChanged();
                break;
                //Indicate normal state
            case 'N':
                link.writeSerial("A");
                break;
            //Recieve random unknown data, do nothing
            default:
                break;
            }
        notifyObservers(str);
        }
    }
