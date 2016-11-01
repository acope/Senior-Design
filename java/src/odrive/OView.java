package odrive;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JOptionPane;
import net.miginfocom.swing.MigLayout;
import org.zu.ardulink.gui.SerialConnectionPanel;
import org.zu.ardulink.gui.ConnectionStatus;


public class OView extends JFrame{
    private final int MAIN_FRAME_HEIGHT = 768; //X
    private final int MAIN_FRAME_WIDTH = 1024; //Y
    private final int FREQ_MIN = 0; //Minimun motor frequency 
    private final int FREQ_MAX = 50; //Max motor frequency = 3000RPM * (1/60)Hz (1RPM = 1/60Hz)
    private final int FREQ_INIT = 0; //Initial motor speed
    private final int SAMPRATE_MIN = 1; //Minimun samping rate in seconds
    private final int SAMPRATE_MAX = 60; //Max samping rate in seconds
    private final int SAMPRATE_INIT = 60; //Initial samping rate
    private final String DEGREE = "\u00B0"; //Degree symbol Unicode
    private final String[] ampString = {"90"+DEGREE, "105"+DEGREE, "120"+DEGREE}; //String for constant amplitudes
    
    protected JFrame mainFrame;
    protected JPanel mainPanel;
    protected JLabel ampLabel;
    protected JLabel freqLabel;
    protected JLabel statusLabel;
    protected JLabel upTimeLabel;
    protected JLabel sampRateLabel;
    protected JLabel freqUnitLabel;
    protected JLabel sampUnitLabel;
    protected JButton buttonStop;
    protected JButton buttonStart;
    protected JButton buttonConnect;
    protected JButton buttonDisconnect;
    protected JTextField  freqTextField;
    protected JTextField  statusTextField;
    protected JTextField upTimeTextField;
    protected JTextField sampRateTextField;
    protected JComboBox ampComboBox;
    protected SerialConnectionPanel serialConnectionPanel;
    protected ConnectionStatus connectionStatus;
    protected JSlider freqSlider;
    protected JSlider sampRateSlider;
    protected MigLayout layout;
    protected GroupLayout gPanel;
    
    public OView(){
        initGUI();
        //initallize objects to their starting values
        guiDefaults();
    }
    
    private void initGUI(){
        mainFrame = new JFrame("ODrive Test Simulation"); //Creates new Frame with name at top
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
        mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
        mainFrame.setResizable(false);
        
        // MiG Layout, Column and Row constraints as arguments.
        layout = new MigLayout();
        //Create a new panel with MiG Layout constraints
        mainPanel = new JPanel(layout); 
        //Create JButtons
        buttonStart = new JButton("Start");
        buttonStop = new JButton("Stop");
        buttonConnect = new JButton("Connect");
        buttonDisconnect = new JButton("Disconnect");
        
        //Create JLabels
        ampLabel = new JLabel("Amplitude:");
        freqLabel = new JLabel("Frequency:");
        statusLabel = new JLabel("Status:");
        upTimeLabel = new JLabel("Up Time:");
        sampRateLabel = new JLabel("Sampling Rate:");
        freqUnitLabel = new JLabel("Hz");
        sampUnitLabel = new JLabel("Seconds");
        
        //Create JTextfield
        freqTextField = new JTextField(Integer.toString(FREQ_INIT));
        statusTextField = new JTextField();
        upTimeTextField = new JTextField();
        sampRateTextField = new JTextField(Integer.toString(SAMPRATE_INIT));
        
        //Create ComboBox
        ampComboBox = new JComboBox(ampString);
        
        //Create JSlider
        
        freqSlider = new JSlider(JSlider.HORIZONTAL,FREQ_MIN,FREQ_MAX,FREQ_INIT);
        freqSlider.setMajorTickSpacing(FREQ_MAX/5);
        freqSlider.setMinorTickSpacing(FREQ_MAX/10);
        freqSlider.setPaintTicks(true);
        freqSlider.setPaintLabels(true);
           
        sampRateSlider = new JSlider(JSlider.HORIZONTAL,SAMPRATE_MIN,SAMPRATE_MAX,SAMPRATE_INIT);
        sampRateSlider.setMajorTickSpacing((SAMPRATE_MAX+1)/5);
        sampRateSlider.setMinorTickSpacing((SAMPRATE_MAX+1)/10);
        sampRateSlider.setPaintTicks(true);
        sampRateSlider.setPaintLabels(true);
        
        //Create other needed panels
        serialConnectionPanel = new SerialConnectionPanel(); //Ardulink Panel
        connectionStatus = new ConnectionStatus();
        
        //Define Text Field Attributes
        statusTextField.setEditable(false);
        upTimeTextField.setEditable(false);
        freqTextField.setEditable(false);
        sampRateTextField.setEditable(false);
        

        //Add objects to panel
        
        //Connection section
        mainPanel.add(serialConnectionPanel);
        mainPanel.add(buttonConnect,"split 3");
        mainPanel.add(buttonDisconnect);
        mainPanel.add(connectionStatus,"wrap");
        
        //Input section
        mainPanel.add(ampLabel, "split 2");
        mainPanel.add(ampComboBox, "wrap");
        
        mainPanel.add(freqLabel, "split 3");
        mainPanel.add(freqSlider, "grow");
        mainPanel.add(freqTextField, "");
        mainPanel.add(freqUnitLabel, "wrap");
        
        
        mainPanel.add(sampRateLabel, "split 3");
        mainPanel.add(sampRateSlider, "");
        mainPanel.add(sampRateTextField,"");
        mainPanel.add(sampUnitLabel, "wrap");
        
        mainPanel.add(buttonStart, "split 2");
        mainPanel.add(buttonStop, "wrap");
        
        //Status section
        mainPanel.add(statusLabel, "split 2");
        mainPanel.add(statusTextField, "grow, wrap");     
        mainPanel.add(upTimeLabel, "split 2");
        mainPanel.add(upTimeTextField, "grow");
        

        //Add panel to the frame
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }
    
    public void guiDefaults(){
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
    }
    
    public void errorJOptionPane(String errorString){
        JOptionPane.showMessageDialog(buttonConnect,errorString, "Error", JOptionPane.ERROR_MESSAGE);
    }
}