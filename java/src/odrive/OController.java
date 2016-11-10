package odrive;

import helper.UpTimeCounter;
import java.awt.Color;
import java.awt.EventQueue;
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
 * @version 1.1
 */
public final class OController implements Observer{
    private final OView view;
    private final OSerial serial;
    private final Link link = Link.getDefaultInstance();
    private final UpTimeCounter upTime;
    private final OFile file;
    private Timer t;
    private OGraph graph;
    
    private int motorFreq; //1RPM = 1/60Hz
    private int sampRate;
    
    
    public OController(){
        view = new OView();
        serial = new OSerial(link.getName());
        upTime = new UpTimeCounter();
        file = new OFile();
        //Thread 5 nullpointerexception need to fix
        /*
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                graph = new OGraph();
            }
        });
        */
                
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
        view.buttonDisconnect.addActionListener((ActionEvent e) -> {
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
        view.buttonStart.addActionListener((ActionEvent e) -> {
            startButton();
        });
    }
    
    /**Writes a "R" to serial to start data logging on Arduino
     * Sends Motor RPM, Amplitude and Sampling Rate to Arduino
     * Enables/Disables GUI items
     */
    public void startButton(){
        try {
                view.setStatusBarText("Setting up communication with Arduino. Please wait...");
                //Create a new Excel workbook for data logging
                file.CreateWBook();
                //Allows Arduino to get ready, figure out new way
                Thread.sleep(2000);
                view.setStatusBarText("Data logging in process. Please do not disconnect the Arduino");
                //Start up time counter
                upTime.start();
                //Start update up time timer
                t.start();
            } catch (InterruptedException ex) {
                Logger.getLogger(OController.class.getName()).log(Level.SEVERE, null, ex);
            }

            link.writeSerial("R"); //Send initiate recording
            serial.sendMotorRPM(getSetMotorRPM()); //Send motor speed
            serial.sendAmplitude(Integer.parseInt(getAmplitudeComboBox())); //Send amplitude 
            serial.sendSamplingRate(getSampleRateSlider()*10); //Send Sampling Rate
            
            //Enable/Disable GUI
            view.inputPanelEnabled(true);
    }
    
    /**
     * Listener for stop button
     */   
    private void stopButtonActionListener(){
        view.buttonStop.addActionListener((ActionEvent e) -> {
            stopButton();
        }); 
    }
    /**
     * 5Writes a "C" to serial for complete
     * Enables/Disables GUI items
     */
    public void stopButton(){
        upTime.stop();
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
    
    public void updateGraph(String rawData){
       String[] separated = rawData.split("[,]+"); 
        graph.addTimeItem(Integer.parseInt(separated[4]));

    }
    
    public void rawArduinoData(String rawData){
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
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Data: " + build.toString(), arg);
                String[] separated = build.toString().split("[,]+"); 
                view.setStatusBarText("Data sample " + separated[0] + " collected." + "Please do not disconnect the Arduino");
                
                //updateGraph(build.toString());
                rawArduinoData(build.toString());

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
                link.writeSerial("C"); //Stop logging if error
                break;
                //Indicate normal state
            case 'N':
                Logger.getLogger(OController.class.getName()).log(Level.INFO, "Arduino in normal state", arg);
                break;
            //Recieve random unknown data, do nothing
            default:
                break;
            }
    }

}
