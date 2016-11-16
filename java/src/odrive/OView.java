package odrive;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import org.jfree.ui.RefineryUtilities;
import org.zu.ardulink.gui.SerialConnectionPanel;
import org.zu.ardulink.gui.ConnectionStatus;

/**
 * Odrive GUI interface
 * Built with AWT and Swing components
 * Future Updates: Design with JavaFX
 * @author Austin Copeman
 * @version 1.3
 */
public class OView extends JFrame{
    private final int FREQ_MIN = 0; //Minimun motor frequency 
    private final int FREQ_MAX = 50; //Max motor frequency = 3000RPM * (1/60)Hz (1RPM = 1/60Hz)
    private final int FREQ_INIT = 20; //Initial motor speed
    private final int SAMPRATE_MIN = 0; //Minimun samping rate in seconds
    private final int SAMPRATE_MAX = 60; //Max samping rate in seconds
    private final int SAMPRATE_INIT = 1; //Initial samping rate
    private final String DEGREE = "\u00B0"; //Degree symbol Unicode
    private final String[] ampString = {"90"+DEGREE, "105"+DEGREE, "120"+DEGREE}; //String for constant amplitudes
    
    private JLabel ampLabel;
    private JLabel freqLabel;
    private JLabel statusLabel;
    private JLabel upTimeLabel;
    private JLabel sampRateLabel;
    private JLabel freqUnitLabel;
    private JLabel sampUnitLabel;
    
    private JButton buttonStop;
    private JButton buttonStart;
    private JButton buttonConnect;
    private JButton buttonDisconnect;
    
    private JTextField  freqTextField;
    private JTextField  statusTextField;
    private JTextField upTimeTextField;
    private JTextField sampRateTextField;
    
    private JComboBox<String> ampComboBox;
    
    private SerialConnectionPanel serialConnectionPanel;
    
    private ConnectionStatus connectionStatus;
    
    private JSlider freqSlider;
    private JSlider sampRateSlider;
    
    private BufferedImage wwwLogo;
    private JLabel label;
    private ImageIcon icon;
    
    private final Dimension buttonSize = new Dimension(225,50);
    private final Font buttonFont = new Font("Dialog", Font.PLAIN, 20);
    
    /**
     * Constructor
     * Creates the GUI for ODrive and sets the initial defaults
     */
    public OView(){
        initGUI();
        //initallize objects to their starting values
        guiDefaults();
    }
    
    /**
     * Initiates the GUI
     * Creates the main frame for panels to be set
     * Panels are laid out in BoxLayout form for the main frame and allows for multiple panels to be added
     * Inner panels are nested to allow multiple functionality
     */
    private void initGUI(){
        JFrame mainFrame = new JFrame("ODrive Data Logger"); //Creates new Frame with name at top        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
        mainFrame.setResizable(false);
        mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/img/wwwImageIcon.png")));
        
        JPanel interfacePanel = new JPanel();
        
        mainFrame.setLayout(new BorderLayout());
        interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.PAGE_AXIS));  
        
        interfacePanel.add(imageLogoPanel());
        interfacePanel.add(connectionPanel());
        interfacePanel.add(inputPanel());
        interfacePanel.add(statusPanel());
        
        
        mainFrame.add(interfacePanel, BorderLayout.WEST);
        mainFrame.pack();
        mainFrame.setVisible(true);

        //Centers on center of screen
        RefineryUtilities.centerFrameOnScreen(mainFrame);
    }
    
    /**
     * Creates connection panel
     * @return JPanel
     */
    private JPanel connectionPanel(){
        JPanel connectionPanel = new JPanel(); //"Layout","Column","Row"
        JPanel buttonPanel = new JPanel(new MigLayout());
        JPanel serialPanel = new JPanel(new MigLayout());
        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.PAGE_AXIS));
        
        BevelBorder bevel = new BevelBorder(BevelBorder.LOWERED);
        EmptyBorder empty = new EmptyBorder(2, 10, 5, 10);
        connectionPanel.setBorder(BorderFactory.createCompoundBorder(empty, bevel));
        
        buttonConnect = new JButton("Connect");
        buttonConnect.setPreferredSize(buttonSize);
        buttonConnect.setFont(buttonFont);
        buttonDisconnect = new JButton("Disconnect");
        buttonDisconnect.setPreferredSize(buttonSize);
        buttonDisconnect.setFont(buttonFont);
        serialConnectionPanel = new SerialConnectionPanel(); //Ardulink Panel
        connectionStatus = new ConnectionStatus();
        
        serialPanel.add(serialConnectionPanel, "split 2, align center");
        serialPanel.add(connectionStatus, "wrap");
        buttonPanel.add(buttonConnect, "split 2, align center");
        buttonPanel.add(buttonDisconnect, "wrap");
        connectionPanel.add(serialPanel);
        connectionPanel.add(buttonPanel);
        
        return connectionPanel;
    }
    
    /**
     * Creates input panel
     * Input panel sends parameters to Arduino
     * @return JPanel
     */
    private JPanel inputPanel(){
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
        JPanel buttonPanel = new JPanel(new MigLayout(""));
        JPanel valuePanel = new JPanel(new MigLayout(""));
        BevelBorder bevel = new BevelBorder(BevelBorder.LOWERED);
        EmptyBorder empty = new EmptyBorder(5, 10, 5, 10);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(empty, bevel));
        
        buttonStart = new JButton("Start");
        buttonStart.setPreferredSize(buttonSize);
        buttonStart.setFont(buttonFont);
        buttonStop = new JButton("Stop");
        buttonStop.setPreferredSize(buttonSize);
        buttonStop.setFont(buttonFont);
        
        ampLabel = new JLabel("Amplitude:");
        freqLabel = new JLabel("Motor Frequency:");
        sampRateLabel = new JLabel("Sampling Rate:");
        freqUnitLabel = new JLabel("Hz");
        sampUnitLabel = new JLabel("Sec");
        
        freqTextField = new JTextField(Integer.toString(FREQ_INIT));
        freqTextField.setEditable(false);
        sampRateTextField = new JTextField(Integer.toString(SAMPRATE_INIT));
        sampRateTextField.setEditable(false);
        
        ampComboBox = new JComboBox<>(ampString);
        
        freqSlider = new JSlider(JSlider.HORIZONTAL,FREQ_MIN,FREQ_MAX,FREQ_INIT);
        freqSlider.setMajorTickSpacing(FREQ_MAX/5);
        freqSlider.setMinorTickSpacing(FREQ_MAX/10);
        freqSlider.setPaintTicks(true);
        freqSlider.setPaintLabels(true);
           
        sampRateSlider = new JSlider(JSlider.HORIZONTAL,SAMPRATE_MIN,SAMPRATE_MAX,SAMPRATE_INIT);
        sampRateSlider.setMajorTickSpacing(SAMPRATE_MAX/5);
        sampRateSlider.setMinorTickSpacing(SAMPRATE_MAX/10);
        sampRateSlider.setPaintTicks(true);
        sampRateSlider.setPaintLabels(true);
        
        valuePanel.add(ampLabel, "align right");
        valuePanel.add(ampComboBox, "wrap");
        
        valuePanel.add(freqLabel, "align right");
        valuePanel.add(freqSlider, "grow, width :300:");
        valuePanel.add(freqTextField, "grow");
        valuePanel.add(freqUnitLabel, "grow,wrap");
        
        
        valuePanel.add(sampRateLabel, "align right");
        valuePanel.add(sampRateSlider, "grow");
        valuePanel.add(sampRateTextField, "grow");
        valuePanel.add(sampUnitLabel, "grow, wrap");
        
        buttonPanel.add(buttonStart, "align center");
        buttonPanel.add(buttonStop, "align center,wrap");
        
        inputPanel.add(valuePanel);
        inputPanel.add(buttonPanel);
        
        return inputPanel;
    }
    
    /**
     * Creates status panel
     * Status panel shows the status of the Arduino and Program as well as the up time since start has been selected
     * @return JPanel
     */
    private JPanel statusPanel(){
        JPanel statusPanel = new JPanel(new MigLayout("","",""));//"Layout","Column","Row"
        
        //Create border
        BevelBorder bevel = new BevelBorder(BevelBorder.LOWERED);
        EmptyBorder empty = new EmptyBorder(5, 10, 10, 10);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(empty, bevel));

        statusLabel = new JLabel("Status:");
        upTimeLabel = new JLabel("Up Time:");
        statusTextField = new JTextField();
        upTimeTextField = new JTextField();
        
        statusTextField.setEditable(false);
        upTimeTextField.setEditable(false);
        
        statusPanel.add(statusLabel, "split 2, align right");
        statusPanel.add(statusTextField, "width :400:,wrap");     
        statusPanel.add(upTimeLabel, "split 2, align right");
        statusPanel.add(upTimeTextField, "width :400:");
        
        return statusPanel;
    }
    
    /**
     * Creates the image logo panel
     * Logo is of wave water works
     * @return JPanel
     */
    private JPanel imageLogoPanel(){
        JPanel imageLogoPanel = new JPanel(new MigLayout("","",""));
        imageLogoPanel.setBackground(Color.DARK_GRAY);
        label = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/img/logo-wave-water-works-white.png"))));
        imageLogoPanel.add(label,"grow,align center");
  
        return imageLogoPanel;
    }
    
    /**
     * initializes GUI defaults when applications starts
     */
    private void guiDefaults(){
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
        freqSlider.setEnabled(false);
        sampRateSlider.setEnabled(false);
        ampComboBox.setEnabled(false);
    }
    
    /**
     * Creates a error pop up message to alert user
     * @param errorString 
     */
    public void errorJOptionPane(String errorString){
        JOptionPane.showMessageDialog(buttonConnect,errorString, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Sets the status of the status text field 
     * @param status 
     */
    public void setStatusBarText(String status){
        statusTextField.setText(status);
    }
    
    /**
     * Sets the color of the status text field
     * @param color 
     */
    public void setStatusBarColor(Color color){
        statusTextField.setBackground(Color.red);
    }
    
    /**
     * Sets the up time counter text of the up time text field
     * @param time 
     */
    public void setUpTimeText(String time){
        upTimeTextField.setText(time);
    }
    
    /**
     * Sets the frequency of the frequency text field
     * @param frequency 
     */
    public void setFrequencyTextField(String frequency){
        freqTextField.setText(frequency);
    }
    
    /**
     * Sets the sampling rate of the sampling rate text field
     * @param rate 
     */
    public void setSampleRate(String rate){
        sampRateTextField.setText(rate);
    }
    
    /**
     * Gets the connection port for connected Arduino
     * @return connection port
     */
    public String getConnectionPort(){
        return serialConnectionPanel.getConnectionPort();
    }
    
    /**
     * Gets the baud rate for connected Arduino
     * @return baud rate
     */
    public String getBaudRate(){
        return serialConnectionPanel.getBaudRate();
    }
    
    /**
     * Gets stop button JButton initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JButton
     */
    public JButton getStopButton(){
        return buttonStop;
    }
    
     /**
     * Gets start button JButton initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JButton
     */
    public JButton getStartButton(){
        return buttonStart;
    }
    
    /**
     * Gets connect button JButton initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JButton
     */    
    public JButton getConnectButton(){
        return buttonConnect;
    }
    
    /**
     * Gets disconnect button JButton initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JButton
     */    
    public JButton getDisconnectButton(){
        return buttonDisconnect;
    }
    
    /**
     * Gets amp combo box JComboBox initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JComboBox
     */    
    public JComboBox getAmpComboBox(){
        return ampComboBox;
    }
    
    /**
     * Gets frequency slider JSlider initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JSlider
     */    
    public JSlider getFreqSlider(){
        return freqSlider;
    }
    
    /**
     * Gets sample rate slider JSlider initialization
     * WARNING! Not the best way of coding, need to figure out better way for getting to OController
     * @return JSlider
     */    
    public JSlider getSampRateSlider(){
        return sampRateSlider;
    }
    
    /**
     * Enables/Disables logging buttons and inputs for input panel
     * When enable is true -> start button, frequency slider, sample rate slider, amp combo bBox = disabled and stop button = enabled
     * When enable is false -> start button, frequency slider, sample rate slider, amp combo bBox = enabled and stop button = disabled
     * @param enable 
     */
    public void inputPanelEnabled(boolean enable){
            buttonStart.setEnabled(!enable);
            buttonStop.setEnabled(enable);
            freqSlider.setEnabled(!enable);
            sampRateSlider.setEnabled(!enable);
            ampComboBox.setEnabled(!enable);
    }
    /**
     * Enables/Disables connection buttons and connection panel
     * When enable is true -> disconnect button, button start, button stop, frequency slider, sample rate slider and amp combo box = enabled and connect button = disabled
     * When enable is false -> disconnect button, button start, button stop, frequency slider, sample rate slider and amp combo box = disabled and connect button = enabled
     * @param enable 
     */
    public void connectionPanelEnabled(boolean enable){
                buttonConnect.setEnabled(!enable);
                buttonDisconnect.setEnabled(enable);
                buttonStart.setEnabled(enable);
                buttonStop.setEnabled(!enable);
                freqSlider.setEnabled(enable);
                sampRateSlider.setEnabled(enable);
                ampComboBox.setEnabled(enable);
    }
}