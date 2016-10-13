package odrive;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.SerialPort;
import static jssc.SerialPort.MASK_RXCHAR;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * 
 * @author http://arduino-er.blogspot.com/2015/09/example-of-using-jssc-communicate.html
 */
public class SerialComm {
    
    private SerialPort arduinoPort = null;
    private ObservableList<String> portList = FXCollections.observableArrayList();
    private String[] serialPortNames = SerialPortList.getPortNames();
    
    /*
     *Detects the connected ports and stores in an obervable array list 
     *Does not recheck ports if it gets disconnected
     */ 
    public void detectPort(){
        for(String name : serialPortNames){
            portList.add(name);
        }
    }
    
           
    public boolean connectArduino(String port){
        
        System.out.println("connectArduino");
        
        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
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
            success = true;
        } catch (SerialPortException ex) {
            Logger.getLogger(SerialComm.class.getName())
                    .log(Level.SEVERE, null, ex);
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
    
}

