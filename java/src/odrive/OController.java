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

//Ardulink import v0.6.1 (Need to update to v2.0.1)
import org.zu.ardulink.Link;


/**
 * The conductor of the program
 * Built using Model/View/Controller Achitecture
 * Initializes the GUI and controls the serial input and file logging
 * @author Austin Copeman
 * @version 1.3
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
    
    /**
     * Main constructor
     * Creates all main constructors linked to OController
     * Starts action listeners and observers
     */
    public OController(){
        view = new OView();
        serial = new OSerial(link.getName());
        upTime = new UpTimeCounter();
        file = new OFile();
        
        //Add observer for serial data        
        serial.addObserver(OController.this);
        //Add action listeners     
        connectButtonActionListener();
        disconnectButtonActionListener();
        startButtonActionListener();
        stopButtonActionListener();
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
        view.getConnectButton().addActionListener((ActionEvent e) -> {
            connectButton();
        });    
    }
    
    /**
     * Connect button action when pressed
     * Retrieves baud rate and com port and checks for null
     * initializes serial connection 
     * sets status bar status and enables/disables GUI
     */
    private void connectButton(){
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
                    view.connectionPanelEnabled(true);
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
    }
    
    /**
     * Listener for disconnect button
     */
    private void disconnectButtonActionListener(){
        view.getDisconnectButton().addActionListener((ActionEvent e) -> {
            disconnectButton();      
        });
    }
    
    /**
     * Calls link.disconnect which closes the serial port
     * Enables/Disables GUI items
     */
    private void disconnectButton(){
        boolean disconnected = link.disconnect();
        if (disconnected) {
            view.setStatusBarText("Disconnected from Arduino");
            view.connectionPanelEnabled(false);
        }
    }
    
    /**
     * Listener for start button
     */
    private void startButtonActionListener(){
        view.getStartButton().addActionListener((ActionEvent e) -> {
            startDataLogging();
        });
    }
    
    /**
     * Starts data serial logging
     * Writes a "R" to serial to start data logging on Arduino
     * Sends Motor RPM, Amplitude and Sampling Rate to Arduino
     * Enables/Disables GUI items
     */
    private void startDataLogging(){
        try {
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Start button selected");
                view.setStatusBarText("Setting up communication with Arduino. Please wait...");
                //Create a new Excel workbook for data logging
                file.CreateWBook();
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "New Excel workbook created");
                //Allows Arduino to get ready, figure out new way
                Thread.sleep(2000);
                view.setStatusBarText("Data logging in process. Please do not disconnect the Arduino");
                //Start up time counter
                upTime.start();
                //Start update up time timer
                t.start();
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Up time counter started");
            } catch (InterruptedException ex) {
                Logger.getLogger(OController.class.getName()).log(Level.SEVERE, null, ex);
            }

            
            serial.sendMotorRPM(getSetMotorRPM()); //Send motor speed
            Logger.getLogger(OController.class.getName()).log(Level.INFO, "Motor RPM: {0}, sent to Arduino", getSetMotorRPM());
            serial.sendAmplitude(Integer.parseInt(getAmplitudeComboBox())); //Send amplitude 
            Logger.getLogger(OController.class.getName()).log(Level.INFO, "Motor amplitude: {0}, sent to Arduino", getAmplitudeComboBox());
            serial.sendSamplingRate(getSampleRateSlider()*10); //Send Sampling Rate
            Logger.getLogger(OController.class.getName()).log(Level.INFO, "Sampling rate: {0}, sent to Arduino", getSampleRateSlider());
            link.writeSerial("R"); //Send initiate recording
            //Enable/Disable GUI
            view.inputPanelEnabled(true);
    }
    
    /**
     * Listener for stop button
     */   
    private void stopButtonActionListener(){
        view.getStopButton().addActionListener((ActionEvent e) -> {
            stopDataLogging();
        }); 
    }
    /**
     * Stop data serial logging
     * Writes a "C" to serial for complete
     * Enables/Disables GUI items
     */
    private void stopDataLogging(){
        upTime.stop();
        Logger.getLogger(OController.class.getName()).log(Level.INFO, "Stop button selected");
        view.setStatusBarText("Data logging has been stopped");         
        //PC sends C for complete of testing
        link.writeSerial("C");
        //Enable/Disable GUI
        view.inputPanelEnabled(false);
    }
    
    /**
     * Listener for frequency slider
     */
    private void freqSliderActionListener(){
        view.getFreqSlider().addChangeListener((ChangeEvent e) -> {
            motorFreq = view.getFreqSlider().getValue();
            view.setFrequencyTextField(Integer.toString(motorFreq));
        });
    }
    
    /**
     * Listener for sample rate slider
     */
    private void sampRateSliderActionListener(){
        view.getSampRateSlider().addChangeListener((ChangeEvent e) -> {
            sampRate = view.getSampRateSlider().getValue();
            view.setSampleRate(Integer.toString(sampRate));
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
    
    /**
     * Get the motor frequency from the slider
     * @return Motor Frequency
     */
    public int getMotorFreqSlider(){
        return view.getFreqSlider().getValue();
    }
    
    /**
     * Returns the set motor RPM
     * 1RPM = 1/60Hz
     * @return Motor PRM
     */
    public int getSetMotorRPM(){
        return getMotorFreqSlider()*60;
    }
    
    /**
     * Retrieves Amplitude from combo box and removes degree symbol
     * @return Amplitude
     */
    public String getAmplitudeComboBox(){
        String amp = view.getAmpComboBox().getSelectedItem().toString();
        int l = amp.length();     
        //Trim off degree symbol
        String newAmp = amp.substring(0, l-1);
        return newAmp;
    }
    
    /**
     * Retrieves the sample rate from GUI slider
     * @return Sampling Rate
     */
    public int getSampleRateSlider(){
        return view.getSampRateSlider().getValue();
    }
    

    /**
     * Sends raw serial data from Arduino to Excel file
     * @param rawData 
     */
    public void writeToExcel(String rawData){
        file.ExcelWrite(rawData);
    }

    /**
     * Observer for incoming Arduino rawData
     * Handles Arduino conditions
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        String str = (String)arg;
        //String builder for recieved data
        StringBuilder build = new StringBuilder(str.length()+1);
        //Retrieve first character from sting
        //First char is always event notification
        char event = str.charAt(0);

        switch (event){
            //Acknowledge
            case 'A':
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Acknowledge Serial Event", arg);
                break;               
            //Fail
            case 'F':
                Logger.getLogger(OController.class.getName()).log(Level.WARNING, "Failed Serial Event", arg);
                break;                
            //Connection Test
            case 'T':
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Arduino ready to transmit", arg);
                break;                
            //Read to collect data
            case 'G':
                break;           
            //Sending recorded data(raw data recieved from Arduino)
            case 'S':               
                //Retrieve information and remove start and end char
                for(int i=1; i<str.length()-1; i++){
                    char c = str.charAt(i);
                    build.append(c);
                }                
                String[] separated = build.toString().split("[,]+"); 
                view.setStatusBarText("Data sample " + separated[0] + " collected." + "Please do not disconnect the Arduino");
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Data: " + build.toString(), arg);
                
                writeToExcel(build.toString());
                break;
            //Error state
            case 'Z':
                //Retrieve information and remove start and end char
                for(int i=1; i<str.length()-1; i++){
                    char c = str.charAt(i);
                    build.append(c);
                }
                Logger.getLogger(OController.class.getName()).log(Level.WARNING, "Error on Arduino: " + build.toString(), arg);
                view.setStatusBarText(build.toString());
                //Stop Motor
                stopDataLogging();
                break;
                //Indicate normal state
            case 'N':
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Arduino in normal state", arg);
                break;
            //Recieve random unknown data, do nothing
            default:
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Unknown Arduino State", arg);
                //Stop motor 
                stopDataLogging();
                break;
            }
    }

}
