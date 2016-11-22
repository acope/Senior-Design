package odrive.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
//Ardulink imports
//https://github.com/marcomauro/Arduino-SerialRead-Java
import org.zu.ardulink.Link;

/**
 * Controls the transmitting and receiving from serial port
 * @author Austin Copeman
 */
//Need to find a better way than to extend to Controller for passing data back
public class OSerial extends Observable{
    private final Link link;
    private final NumberFormat formatter = new DecimalFormat("0000"); //For sending 4 digits to Arduino
    /**
     * Constructor, takes in link instance to communicate with serial
     * @param linkName name of link instance
     */
    public OSerial(String linkName){
        link = Link.getInstance(linkName);
    }
    
    
    /**
    * Sends motor RPM to serial
    * @param rpm input motor rpm
    * @return true
    */
    public boolean sendMotorRPM(int rpm){      
        link.writeSerial("M");
        link.writeSerial(formatter.format(rpm));
        link.writeSerial("E");
        return true;
    }
     
    /**
     * Sends amplitude to serial
     * @param amp input amplitude
     * @return true
     */
    public boolean sendAmplitude(int amp){          
        link.writeSerial("D");
        link.writeSerial(formatter.format(amp));
        link.writeSerial("E");
        return true;
    }
    
        /**
     * Sends sampling rate to serial
     * @param samplingRate input sampling rate
     * @return true
     */
    public boolean sendSamplingRate(int samplingRate){    
        link.writeSerial("X");
        link.writeSerial(formatter.format(samplingRate));
        link.writeSerial("E");
        return true;
    }
    
    /**
     * Cases for received state from Arduino
     * @param str String of Arduino Event
     */
    public void serialArduinoEvent(String str){
        //Retrieve first character from sting
        //First char is always event notification
        char event = str.charAt(0);

        switch (event){
            //Acknowledge
            case 'A':
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Acknowledge event recieved from Arduino", str);
                break;               
            //Fail
            case 'F':
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Fail event recieved from Arduino", str);
                break;            
            //Connection Test
            case 'T':
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Connection Test", str);
                link.writeSerial("A");
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: 'A' sent to Arduino");
                break;              
            //Read to collect data
            case 'G':
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Ready to collect data", str);
                link.writeSerial("A");
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: 'A' sent to Arduino");
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
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Normal state event recieved from Arduino", str);
                link.writeSerial("A");
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: 'A' sent to Arduino");
                break;
            //Recieve random unknown data, do nothing
            default:
                Logger.getLogger(OSerial.class.getName()).log(Level.INFO, "Arduino Serial Protocol: {0} -- Uknown event recieved from Arduino", str);
                break;
            }
        notifyObservers(str);
        }
    }
