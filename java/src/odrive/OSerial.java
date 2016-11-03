package odrive;

//Ardulink imports
//https://github.com/marcomauro/Arduino-SerialRead-Java
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.zu.ardulink.Link;

/**
 * Controls the transmitting and receiving from serial port
 * @author Austin Copeman
 */
public class OSerial {
    private final Link link;
    private NumberFormat formatter = new DecimalFormat("0000");
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
        /*
        String str = Integer.toString(rpm);
        int[] motorRPMArray = new int[str.length()];
        
        for(int i = 0; i < str.length(); i++){
            motorRPMArray[i] = str.charAt(i)- '0';
        }
        
        */
        //str = String.format("%04d", rpm);
        //System.out.println(formatter.format(rpm));
        link.writeSerial("M");
        link.writeSerial(formatter.format(rpm));
        //link.writeSerial(str.length()-1, motorRPMArray);
        link.writeSerial("E");
        return true;
    }
     
    /**
     * Sends amplitude to serial
     * @param amp
     * @return 
     */
    public boolean sendAmplitude(int amp){
        String str = Integer.toString(amp);
        int[] ampArray = new int[str.length()];
        
        for(int i = 0; i < str.length(); i++){
            ampArray[i] = str.charAt(i) - '0';
        }
        
        
        link.writeSerial("D");
        link.writeSerial(formatter.format(amp));
        //link.writeSerial(str.length()-1, ampArray);
        link.writeSerial("E");
        return true;
    }
    
        /**
     * Sends sampling rate to serial
     * @param samplingRate
     * @return 
     */
    public boolean sendSamplingRate(int samplingRate){
        String str = Integer.toString(samplingRate);
        int[] samplingRateArray = new int[str.length()];
        
        for(int i = 0; i < str.length(); i++){
            samplingRateArray[i] = str.charAt(i) - '0';
        }
        
        link.writeSerial("X");
        link.writeSerial(formatter.format(samplingRate));
        //link.writeSerial(str.length()-1, samplingRateArray);
        link.writeSerial("E");
        return true;
    }
    
    /**
     * Cases for received state from Arduino
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
            
            //Sending recorded data(data sent from Arduino)Total 14 bytes
            case 'S':
                //add in code for recieving data
                /*
                    MC shall send "S" to indicate start of data transmission.
                    MC shall send time stamp in unsigned long format (4 bytes).
                    MC shall send motor rpm reading in unsigned int format (2 bytes).
                    MC shall send input rpm reading in unsigned int format (2 bytes).
                    MC shall send output rpm reading in unsigned int format (2 bytes).
                    MC shall send measured voltage in unsigned int format (2 bytes).
                    MC shall send "E" to indicate end of data transmission.
                    PC shall respond with "A" or "F" to indicate acknowledge or fail.
                */
                StringBuilder build = new StringBuilder(str.length()+1);
                //Convert the string back into bytes and removes start and end char
                for(int i=1; i<str.length()-1; i++){
                    char c = str.charAt(i);
                    build.append(c);
                   // build.append(Integer.parseInt(Character.toString(c)));
                }
                String arduinoData = build.toString();
                //Send to ExcelWrite in OFile
                System.out.println(arduinoData);
                break;
                
            //Error state
            case 'Z':
                //add in code for error state handling
                /*
                    MC shall send "Z" to PC to indicate ERROR state.
                    MC shall send error message to PC in ASC2 string format.
                    MC shall send "E" to indicate end of message.
                    PC shall perform TBD
                */
                break;
                
            //Indicate normal state
            case 'N':
                link.writeSerial("A");
                break;
            
            default:
                break;
        }
    }
}
