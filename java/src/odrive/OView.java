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
 * @author Austin Copeman
 * @version 1.1
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
    
    protected JComboBox<String> ampComboBox;
    
    private SerialConnectionPanel serialConnectionPanel;
    
    private ConnectionStatus connectionStatus;
    
    protected JSlider freqSlider;
    protected JSlider sampRateSlider;
    private OGraph graph;
    
    private BufferedImage wwwLogo;
    private JLabel label;
    private ImageIcon icon;
    
    private final Dimension buttonSize = new Dimension(225,50);
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
        mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/img/wwwImageIcon.png")));
        
        JPanel interfacePanel = new JPanel();
        JPanel graphPanel = new JPanel();
        
        mainFrame.setLayout(new BorderLayout());
        interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.PAGE_AXIS));  
        
        interfacePanel.add(imageLogoPanel());
        interfacePanel.add(connectionPanel());
        interfacePanel.add(inputPanel());
        interfacePanel.add(statusPanel());
        graphPanel.add(graphPanel());
        
        
        mainFrame.add(interfacePanel, BorderLayout.WEST);
        //******************************************
        //Removed Graph for Showing Purpose
        //*******************************************
        mainFrame.add(graphPanel, BorderLayout.EAST);
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
    
    
    private JPanel imageLogoPanel(){
        JPanel imageLogoPanel = new JPanel(new MigLayout("","",""));
        imageLogoPanel.setBackground(Color.DARK_GRAY);
        label = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/img/logo-wave-water-works-white.png"))));
        imageLogoPanel.add(label,"grow,align center");
  
        return imageLogoPanel;
    }
    
    private JPanel graphPanel(){
        JPanel graphPanel;
        OGraph ograph = new OGraph();      
        graphPanel = ograph.createGraphPanel("ODrive Real Time Data", "Time", "Range", 950, 550);
        return graphPanel;
    }
    
    private void guiDefaults(){
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
        freqSlider.setEnabled(false);
        sampRateSlider.setEnabled(false);
        ampComboBox.setEnabled(false);
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