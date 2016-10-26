package odrive;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.zu.ardulink.gui.SerialConnectionPanel;
import org.zu.ardulink.gui.ConnectionStatus;

public class OView extends JFrame{
    private final int MAIN_FRAME_HEIGHT = 600; //X
    private final int MAIN_FRAME_WIDTH = 600; //Y
    private final int FREQ_MIN = 0; //Minimun motor speed
    private final int FREQ_MAX = 1023; //Max motor speed
    private final int FREQ_INIT = 0; //Initial motor speed
    private final int SAMPRATE_MIN = 0; //Minimun samping rate
    private final int SAMPRATE_MAX = 60; //Max samping rate
    private final int SAMPRATE_INIT = 60; //Initial samping rate
    
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
    
    public OView(){
        initGUI();
        //initallize objects to their starting values
        guiDefaults();
    }
    
    private void initGUI(){
        JFrame mainFrame = new JFrame("ODrive Test Simulation");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
        mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
        mainFrame.setResizable(false);
        
        
        // MiG Layout, Column and Row constraints as arguments.
        MigLayout layout = new MigLayout();
        //Create a new panel with MiG Layout constraints
        JPanel mainPanel = new JPanel(layout); 
        //Create JButtons
        buttonStart = new JButton("Start");
        buttonStop = new JButton("Stop");
        buttonConnect = new JButton("Connect");
        buttonDisconnect = new JButton("Disconnect");
        
        //Create JLabels
        JLabel ampLabel = new JLabel("Amplitude:");
        JLabel freqLabel = new JLabel("Frequency:");
        JLabel statusLabel = new JLabel("Status:");
        JLabel upTimeLabel = new JLabel("Up Time:");
        JLabel sampRateLabel = new JLabel("Sampling Rate:");
        
        //Create JTextfield
        freqTextField = new JTextField(Integer.toString(FREQ_INIT));
        statusTextField = new JTextField();
        upTimeTextField = new JTextField();
        sampRateTextField = new JTextField(Integer.toString(SAMPRATE_INIT));
        
        //Create ComboBox
        String[] ampString = {"Amp1", "Amp2", "Amp3", "Amp4"};
        ampComboBox = new JComboBox(ampString);
        
        //Create JSlider
        
        freqSlider = new JSlider(JSlider.HORIZONTAL,FREQ_MIN,FREQ_MAX,FREQ_INIT);
        freqSlider.setMajorTickSpacing(FREQ_MAX/4);
        freqSlider.setMinorTickSpacing(FREQ_MAX/12);
        freqSlider.setPaintTicks(true);
        freqSlider.setPaintLabels(true);
           
        sampRateSlider = new JSlider(JSlider.HORIZONTAL,SAMPRATE_MIN,SAMPRATE_MAX,SAMPRATE_INIT);
        sampRateSlider.setMajorTickSpacing(SAMPRATE_MAX/5);
        sampRateSlider.setMinorTickSpacing(SAMPRATE_MAX/10);
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
        mainPanel.add(serialConnectionPanel);
        mainPanel.add(buttonConnect,"split 3");
        mainPanel.add(buttonDisconnect);
        mainPanel.add(connectionStatus,"wrap");
        
        mainPanel.add(ampLabel, "split 2");
        mainPanel.add(ampComboBox, "wrap");
        
        mainPanel.add(freqLabel, "split 2");
        mainPanel.add(freqSlider, "grow");
        mainPanel.add(freqTextField, "grow, wrap");
        
        
        mainPanel.add(sampRateLabel, "split 2");
        mainPanel.add(sampRateSlider, "");
        mainPanel.add(sampRateTextField,"grow, wrap");
        
        mainPanel.add(buttonStart, "split 2");
        mainPanel.add(buttonStop, "wrap");
        
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
}