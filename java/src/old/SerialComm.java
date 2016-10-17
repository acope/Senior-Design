/*
package old;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.SerialPort;
import static jssc.SerialPort.MASK_RXCHAR;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import odrive.SerialInterface;

public class SerialComm extends SerialInterface{
    
    private SerialPort arduinoPort = null;
    private SerialPort serialPort = null;
    private ObservableList<String> portList = FXCollections.observableArrayList();
    private String[] serialPortNames = SerialPortList.getPortNames();
    

    public void detectPort(){
        portList.addAll(Arrays.asList(serialPortNames));
    }
    
       
    public boolean connectArduino(String port){
        
        System.out.println("connectArduino");
        
        boolean success = false;
        serialPort = new SerialPort(port);
        
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            

            
            serialPort.setEventsMask(MASK_RXCHAR);
            //Listens to the Arduino port for information
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if(serialPortEvent.isRXCHAR()){
                    try {
                        String st = serialPort.readString(serialPortEvent.getEventValue());
                        System.out.println(st);    
                    } catch (SerialPortException ex) {
                        Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
            
            
            //arduinoPort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }

        return success;
    }
    
 
    public void disconnectArduino(){
        
        System.out.println("disconnectArduino()");
        if(arduinoPort != null){
            try {
                arduinoPort.removeEventListener();
                
                if(arduinoPort.isOpened()){
                    arduinoPort.closePort();
                }
                
            } catch (SerialPortException ex) {
                Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String[] getPorts(){
        return serialPortNames;
    }
    

    public int totalPorts(){
        String ports[] = getPorts();
        int totalPorts = 0;
        
        for(String temp : ports){
            totalPorts++;
        }
        
        return totalPorts;
    }
    
    //#TODO: Needs to get incoming parameters for reading from Arduino data
    public String readData(){
                    
        try {   
            serialPort.setEventsMask(MASK_RXCHAR);
            //Listens to the Arduino port for information
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if(serialPortEvent.isRXCHAR()){
                    try {
                        String st = serialPort.readString(serialPortEvent.getEventValue());
                        System.out.println(st);    
                    } catch (SerialPortException ex) {
                        Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
  
            arduinoPort = serialPort;
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialComm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
        }
       
        return null;    
    }
    

    public char startLogging(int speed, int amplitude){
        char serialResponse = ' ';
        
        return serialResponse;
    }
    

    public char stopLogging(){
        char serialResponse = ' ';
        
        return serialResponse;
    }
}
*/

