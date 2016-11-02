package odrive;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;

//Ardulink imports
import org.zu.ardulink.Link;



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

            link.writeSerial("R"); //Send initiate recording
            sendMotorRPM(getSetMotorRPM()); //Send motor speed
            sendAmplitude(Integer.parseInt(getAmplitudeComboBox())); //Send amplitude
            sendSamplingRate(getSampleRateSlider()*10); //Send Sampling Rate
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
     * Listens for data on serial port and sends to serialArduinoEvent
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
    
    /**
     * Returns the set motor RPM
     * 1RPM = 1/60Hz
     * @return 
     */
    public int getSetMotorRPM(){
        return getMotorFreqSlider()*60;
    }
    
    /**
     * Retrieves Amplitude from combo box and removes degree symbol
     * @return 
     */
    public String getAmplitudeComboBox(){
        String amp = view.ampComboBox.getSelectedItem().toString();
        int l = amp.length();     
        //Trim off degree symbol
        String newAmp = amp.substring(0, l-1);
        
        return newAmp;
    }
    
    /**
     * Retrieves the sample rate from GUI slider
     * @return 
     */
    public int getSampleRateSlider(){
        return view.sampRateSlider.getValue();
    }
    
    /**
     * Calls the file writing to store incoming serial data
     * @param data 
     */
    public void recordIncomingSerialData(String data){
        //Send to Dana
        //#TODO
        arduinoSerialData = data;
    }
    
    /**
     * Sends motor RPM to serial
     * @param rpm
     * @return 
     */
    public boolean sendMotorRPM(int rpm){      
        String str = Integer.toString(rpm);
        int[] motorRPMArray = new int[str.length()];
        
        for(int i = 0; i < str.length(); i++){
            motorRPMArray[i] = str.charAt(i)- '0';
        }
        
        link.writeSerial("M");
        link.writeSerial(str.length()-1, motorRPMArray);
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
        link.writeSerial(str.length()-1, ampArray);
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
        link.writeSerial(str.length()-1, samplingRateArray);
        link.writeSerial("E");
        return true;
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
                recordIncomingSerialData(arduinoData);
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
