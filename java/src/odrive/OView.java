package odrive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.JOptionPane;
import net.miginfocom.swing.MigLayout;
import org.zu.ardulink.gui.SerialConnectionPanel;
import org.zu.ardulink.gui.ConnectionStatus;

/**
 * Odrive GUI interface
 * @author Austin Copeman
 */
public class OView extends JFrame{
    private final int FREQ_MIN = 0; //Minimun motor frequency 
    private final int FREQ_MAX = 50; //Max motor frequency = 3000RPM * (1/60)Hz (1RPM = 1/60Hz)
    private final int FREQ_INIT = 25; //Initial motor speed
    private final int SAMPRATE_MIN = 0; //Minimun samping rate in seconds
    private final int SAMPRATE_MAX = 60; //Max samping rate in seconds
    private final int SAMPRATE_INIT = 60; //Initial samping rate
    private final String DEGREE = "\u00B0"; //Degree symbol Unicode
    private final String[] ampString = {"90"+DEGREE, "105"+DEGREE, "120"+DEGREE}; //String for constant amplitudes
    
    private JLabel ampLabel;
    private JLabel freqLabel;
    private JLabel statusLabel;
    private JLabel upTimeLabel;
    private JLabel sampRateLabel;
    private JLabel freqUnitLabel;
    private JLabel sampUnitLabel;
    
    protected JButton buttonStop;
    protected JButton buttonStart;
    protected JButton buttonConnect;
    protected JButton buttonDisconnect;
    
    private JTextField  freqTextField;
    private JTextField  statusTextField;
    private JTextField upTimeTextField;
    private JTextField sampRateTextField;
    
    protected JComboBox ampComboBox;
    
    private SerialConnectionPanel serialConnectionPanel;
    
    private ConnectionStatus connectionStatus;
    
    protected JSlider freqSlider;
    protected JSlider sampRateSlider;
    
    private final Dimension buttonSize = new Dimension(150,50);
    private final Font buttonFont = new Font("Dialog", Font.PLAIN, 20);
    
    public OView(){
        //initGUI();
        initGUI();
        //initallize objects to their starting values
        guiDefaults();
    }
    
    private void initGUI(){
        JFrame mainFrame = new JFrame("ODrive Data Logger"); //Creates new Frame with name at top
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
        mainFrame.setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        mainPanel.add(connectionPanel());
        mainPanel.add(inputPanel());
        mainPanel.add(statusPanel());
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    /**
     * Creates connection panel
     * @return JPanel
     */
    private JPanel connectionPanel(){
        JPanel connectionPanel = new JPanel(new MigLayout("","","")); //"Layout","Column","Row"
        connectionPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonConnect = new JButton("Connect");
        buttonConnect.setPreferredSize(buttonSize);
        buttonConnect.setFont(buttonFont);
        buttonDisconnect = new JButton("Disconnect");
        buttonDisconnect.setPreferredSize(buttonSize);
        buttonDisconnect.setFont(buttonFont);
        serialConnectionPanel = new SerialConnectionPanel(); //Ardulink Panel
        connectionStatus = new ConnectionStatus();
        
        connectionPanel.add(serialConnectionPanel, "split 2");
        connectionPanel.add(connectionStatus, "wrap");
        connectionPanel.add(buttonConnect, "split 2");
        connectionPanel.add(buttonDisconnect, "wrap");
        
        return connectionPanel;
    }
    
    private JPanel inputPanel(){
        JPanel inputPanel = new JPanel(new MigLayout("","",""));//"Layout","Column","Row"
        inputPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonStart = new JButton("Start");
        buttonStart.setPreferredSize(buttonSize);
        buttonStart.setFont(buttonFont);
        buttonStop = new JButton("Stop");
        buttonStop.setPreferredSize(buttonSize);
        buttonStop.setFont(buttonFont);
        
        ampLabel = new JLabel("Amplitude:");
        freqLabel = new JLabel("Frequency:");
        sampRateLabel = new JLabel("Sampling Rate:");
        freqUnitLabel = new JLabel("Hz");
        sampUnitLabel = new JLabel("Seconds");
        
        freqTextField = new JTextField(Integer.toString(FREQ_INIT));
        freqTextField.setEditable(false);
        sampRateTextField = new JTextField(Integer.toString(SAMPRATE_INIT));
        sampRateTextField.setEditable(false);
        
        ampComboBox = new JComboBox(ampString);
        
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
        
        inputPanel.add(ampLabel, "split 2");
        inputPanel.add(ampComboBox, "wrap");
        
        inputPanel.add(freqLabel, "split 3");
        inputPanel.add(freqSlider, "grow");
        inputPanel.add(freqTextField, "");
        inputPanel.add(freqUnitLabel, "wrap");
        
        
        inputPanel.add(sampRateLabel, "split 3");
        inputPanel.add(sampRateSlider, "grow");
        inputPanel.add(sampRateTextField, "");
        inputPanel.add(sampUnitLabel, "wrap");
        
        inputPanel.add(buttonStart, "split 2");
        inputPanel.add(buttonStop, "wrap");
        
        return inputPanel;
    }
    
    private JPanel statusPanel(){
        JPanel statusPanel = new JPanel(new MigLayout("insets 0 10 10 20","",""));//"Layout","Column","Row"
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Status:");
        upTimeLabel = new JLabel("Up Time:");
        statusTextField = new JTextField();
        upTimeTextField = new JTextField();
        
        statusTextField.setEditable(false);
        upTimeTextField.setEditable(false);
        
        statusPanel.add(statusLabel, "split 2");
        statusPanel.add(statusTextField, "width :400:, wrap");     
        statusPanel.add(upTimeLabel, "split 2");
        statusPanel.add(upTimeTextField, "width :400:");
        
        return statusPanel;
    }
    
    private void guiDefaults(){
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
    }
    
    public void errorJOptionPane(String errorString){
        JOptionPane.showMessageDialog(buttonConnect,errorString, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void setStatusBarText(String status){
        statusTextField.setText(status);
    }
    
    public void setStatusBarColor(Color color){
        statusTextField.setBackground(Color.red);
    }
    
    public void setUpTimeText(String time){
        upTimeTextField.setText(time);
    }
    
    public void setFrequencyTextField(String frequency){
        freqTextField.setText(frequency);
    }
    
    public void setSampleRate(String rate){
        sampRateTextField.setText(rate);
    }
    
    public String getConnectionPort(){
        return serialConnectionPanel.getConnectionPort();
    }
    
    public String getBaudRate(){
        return serialConnectionPanel.getBaudRate();
    }
    
}