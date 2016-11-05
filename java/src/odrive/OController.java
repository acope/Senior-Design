package odrive;

import helper.UpTimeCounter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import java.util.Observer;

import javax.swing.event.ChangeEvent;

//Ardulink imports
import org.zu.ardulink.Link;


/**
 * The conductor of the program
 * Built using Model/View/Controller Achitecture
 * Initializes the GUI and controls the serial input and file logging
 * @author Austin Copeman
 */
public final class OController implements Observer{
    private final OView view;
    private final OSerial serial;
    private final Link link = Link.getDefaultInstance();
    private final UpTimeCounter upTime;
    private final OFile file;
    private Timer t;
    
    private int motorFreq; //1RPM = 1/60Hz
    private int sampRate;
    
    
    public OController(){
        view = new OView();
        serial = new OSerial(link.getName());
        upTime = new UpTimeCounter();
        file = new OFile();
                
        serial.addObserver(OController.this);
        //Add action listeners     
        connectButtonActionListener();
        disconnectButtonActionListener();
        startButtonActionListener();
        stopButtonActionListener();
        ampComboBoxActionListener();
        freqSliderActionListener();
        sampRateSliderActionListener();
        rawDataListener();
        updateUpTime();
    }
    
    /**
     * Listener for connect button
     * Retrieves port and baud rate and establishes connection to Serial port
     */
    private void connectButtonActionListener(){
        view.buttonConnect.addActionListener((ActionEvent e) -> {
            String comPort = view.getConnectionPort();
            String baudRateS = view.getBaudRate();
            
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
                        view.setStatusBarText("Connected to Arduino on " + comPort + " at " + baudRateS + "bps");
                        view.buttonConnect.setEnabled(false);
                        view.buttonDisconnect.setEnabled(true);
                        view.buttonStart.setEnabled(true);
                        view.buttonStop.setEnabled(false);
                        view.freqSlider.setEnabled(true);
                        view.sampRateSlider.setEnabled(true);
                        view.ampComboBox.setEnabled(true);
                    }
                }
                catch(Exception ex){
                    String message = ex.getMessage();
                    if(message == null || message.trim().equals(" ")){
                        message = "Generic Error on Connection!";
                    }
                    view.setStatusBarText(message);
                    view.setStatusBarColor(Color.red);
                }
            }
        });    
    }
    
    /**
     * Listener for disconnect button
     * Calls link.disconnect which closes the serial port
     * Enables/Disables GUI items
     */
    private void disconnectButtonActionListener(){
        view.buttonDisconnect.addActionListener((ActionEvent e) -> {
            boolean disconnected = link.disconnect();
            if (disconnected) {
                view.setStatusBarText("Disconnected from Arduino");
                view.buttonConnect.setEnabled(true);
                view.buttonDisconnect.setEnabled(false);
                view.buttonStart.setEnabled(false);
                view.buttonStop.setEnabled(false);
                view.freqSlider.setEnabled(false);
                view.sampRateSlider.setEnabled(false);
                view.ampComboBox.setEnabled(false);
            }
        });
    }
    
    /**
     * Listener for start button
     * Writes a "R" to serial to start data logging on Arduino
     * Sends Motor RPM, Amplitude and Sampling Rate to Arduino
     * Enables/Disables GUI items
     */
    private void startButtonActionListener(){
        view.buttonStart.addActionListener((ActionEvent e) -> {
            try {
                //Create a new Excel workbook for data logging
                file.CreateWBook();
                //Allows Arduino to get ready
                view.setStatusBarText("Setting up communication with Arduino. Please wait...");
                Thread.sleep(2000);
                view.setStatusBarText("Data logging in process. Please do not disconnect the Arduino");
                //Start up time counter
                upTime.start();
                //Start update up time timer
                t.start();
                //#TODO Create new workbook here, Dana
            } catch (InterruptedException ex) {
                Logger.getLogger(OController.class.getName()).log(Level.SEVERE, null, ex);
            }

            link.writeSerial("R"); //Send initiate recording
            serial.sendMotorRPM(getSetMotorRPM()); //Send motor speed
            serial.sendAmplitude(Integer.parseInt(getAmplitudeComboBox())); //Send amplitude 
            serial.sendSamplingRate(getSampleRateSlider()*10); //Send Sampling Rate
            
            //Enable/Disable GUI
            view.buttonStart.setEnabled(false);
            view.buttonStop.setEnabled(true);
            view.freqSlider.setEnabled(false);
            view.sampRateSlider.setEnabled(false);
            view.ampComboBox.setEnabled(false);
        });
    }
    
    /**
     * Listener for stop button
     * Writes a "C" to serial for complete
     * Enables/Disables GUI items
     */   
    private void stopButtonActionListener(){
        view.buttonStop.addActionListener((ActionEvent e) -> {
            file.closeWorkBook();
            upTime.stop();
            view.setStatusBarText("Data logging has been stopped");
            //#TODO close workbook here
            
            //PC sends C for complete of testing
            link.writeSerial("C");
            //Stop file logging
            
            //Enable/Disable GUI
            view.buttonStart.setEnabled(true);
            view.buttonStop.setEnabled(false);
            view.freqSlider.setEnabled(true);
            view.sampRateSlider.setEnabled(true);
            view.ampComboBox.setEnabled(true);
        }); 
    }
    
    /**
     * Listener for frequency slider
     */
    private void freqSliderActionListener(){
        view.freqSlider.addChangeListener((ChangeEvent e) -> {
            motorFreq = view.freqSlider.getValue();
            view.setFrequencyTextField(Integer.toString(motorFreq));
        });
    }
    
    /**
     * Listener for sample rate slider
     */
    private void sampRateSliderActionListener(){
        view.sampRateSlider.addChangeListener((ChangeEvent e) -> {
            sampRate = view.sampRateSlider.getValue();
            view.setSampleRate(Integer.toString(sampRate));
        });
    }
    
    /**
     * Listener for amplitude combo box
     */
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
            serial.serialArduinoEvent(build.toString());
        });
    }   
    
    /**
     * Updates up time text field
     * Updates every half second
     */
    private void updateUpTime(){
        t = new Timer(500, (ActionEvent e) -> {
            view.setUpTimeText(upTime.getUpTime());
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
    
    public void rawArduinoData(String rawData){
        file.ExcelWrite(rawData);
        //System.out.println("Observer: " + rawData);
    }

    /**
     * Observer for incoming Arduino rawData
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        rawArduinoData((String)arg);
    }

}
