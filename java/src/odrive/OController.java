package odrive;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//Ardulink imports
import org.zu.ardulink.Link;
import org.zu.ardulink.RawDataListener;



public class OController{
    private OView view;
    
    private final Link link = Link.getDefaultInstance();
    
    private int motorFreq; //1RPM = 1/60Hz
    private int sampRate;
    private String arduinoSerialData; //Data recieved from Arduino serially
    
    public OController(){
        view = new OView();

        //Add action listeners     
        connectButtonActionListener();
        disconnectButtonActionListener();
        startButtonActionListener();
        stopButtonActionListener();
        ampComboBoxActionListener();
        freqSliderActionListener();
        sampRateSliderActionListener();
        rawDataListener();
    }
    
    
    private void connectButtonActionListener(){
        view.buttonConnect.addActionListener((ActionEvent e) -> {
            String comPort = view.serialConnectionPanel.getConnectionPort();
            String baudRateS = view.serialConnectionPanel.getBaudRate();
            
            if(comPort == null || "".equals(comPort)){
                view.errorJOptionPane("No COM port found");
            } else if (baudRateS == null || "".equals(baudRateS)){
                view.errorJOptionPane("Invalid baud rate set");
            }else{
                //Connect to Arduino board on com and baud
                try{
                    int baudRate = Integer.parseInt(baudRateS);
                    boolean connected = link.connect(comPort, baudRate);
                    
                    //If connected start listening to COM port and enable/disable GUI
                    if(connected) {                  
                        view.buttonConnect.setEnabled(false);
                        view.buttonDisconnect.setEnabled(true);
                        view.buttonStart.setEnabled(true);
                        view.buttonStop.setEnabled(false);                     
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    String message = ex.getMessage();
                    if(message == null || message.trim().equals(" ")){
                        message = "Generic Error on Connection!";
                    }
                    view.statusTextField.setText(message);
                    view.statusTextField.setBackground(Color.red);
                }
            }
        });    
    }
    
    private void disconnectButtonActionListener(){
        view.buttonDisconnect.addActionListener((ActionEvent e) -> {
            boolean disconnected = link.disconnect();
            if (disconnected) {
                view.buttonConnect.setEnabled(true);
                view.buttonDisconnect.setEnabled(false);
                view.buttonStart.setEnabled(false);
                view.buttonStop.setEnabled(false);
            }
        });
    }
        private void startButtonActionListener(){
        view.buttonStart.addActionListener((ActionEvent e) -> {
            try {
                //Allows Arduino to get ready
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(OController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String motorSpeed = Integer.toString(getMotorFreqSlider()*60); //Arduino takes RPM, 1RPM = 1/60Hz
            String amplitude = getAmplitudeComboBox();
            String sampRate1 = Integer.toString(getSampleRateSlider()*10); //Arduino takes in 600 for 60 seconds and 1 for 0.1 second
            
            //PC sends an R to initiate recording, Then sends other requests for other information
            String message = "R" + "M" + motorSpeed + "E" + "D" + amplitude+ "E" + "X" + sampRate1 + "E";
            link.writeSerial(message);
            //Enable/Disable GUI
            view.buttonStart.setEnabled(false);
            view.buttonStop.setEnabled(true);
            view.freqSlider.setEnabled(false);
            view.sampRateSlider.setEnabled(false);
            view.ampComboBox.setEnabled(false);
        });
    }
    
    private void stopButtonActionListener(){
        view.buttonStop.addActionListener((ActionEvent e) -> {
            //PC sends C for complete of testing
            link.writeSerial("C");
            //Enable/Disable GUI
            view.buttonStart.setEnabled(true);
            view.buttonStop.setEnabled(false);
            view.freqSlider.setEnabled(true);
            view.sampRateSlider.setEnabled(true);
            view.ampComboBox.setEnabled(true);
        }); 
    }
    
    private void freqSliderActionListener(){
        view.freqSlider.addChangeListener((ChangeEvent e) -> {
            motorFreq = view.freqSlider.getValue();
            view.freqTextField.setText(Integer.toString(motorFreq));
        });
    }
    
        private void sampRateSliderActionListener(){
        view.sampRateSlider.addChangeListener((ChangeEvent e) -> {
            sampRate = view.sampRateSlider.getValue();
            view.sampRateTextField.setText(Integer.toString(sampRate));
        });
    }
    
    private void ampComboBoxActionListener(){
        view.ampComboBox.addActionListener((ActionEvent e) -> {
            //Add code
        });
    }
    
    /**
     * Serial Listener
     * Listens for data on the serial port
     */
    public void rawDataListener(){
        link.addRawDataListener((String id, int numBytes, int[] message) -> {         
            StringBuilder build = new StringBuilder(numBytes + 1);
            for (int i = 0; i < numBytes; i++) {
                    build.append((char)message[i]);     
            }
            serialArduinoEvent(build.toString());
        });
    }   
    
    public int getMotorFreqSlider(){
        return view.freqSlider.getValue();
    }
    
    public String getAmplitudeComboBox(){
        String amp = view.ampComboBox.getSelectedItem().toString();
        int l = amp.length();     
        //Trim off degree symbol
        String newAmp = amp.substring(0, l-1);

        return newAmp;
    }
    
    public int getSampleRateSlider(){
        return view.sampRateSlider.getValue();
    }
    
    public void setArduinoSerialData(String data){
        arduinoSerialData = data;
    }
    
    /**
     * Cases for received state from Arduino
     * @param str 
     */
    private void serialArduinoEvent(String str){
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
