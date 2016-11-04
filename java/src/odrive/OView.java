package odrive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
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
    
    protected JComboBox<String> ampComboBox;
    
    private SerialConnectionPanel serialConnectionPanel;
    
    private ConnectionStatus connectionStatus;
    
    protected JSlider freqSlider;
    protected JSlider sampRateSlider;
    
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
        try {
            mainFrame.setIconImage(ImageIO.read(new File(System.getProperty("user.dir") + "/res/img/wwwImageIcon.png")));
            //mainFrame.setIconImage(ImageIO.read(new File("C:/Users/mr_co_000/Documents/NetBeansProjects/ODrive/src/res/img/wwwImageIcon.png")));
        } catch (IOException ex) {
            Logger.getLogger(OView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JPanel interfacePanel = new JPanel();
        interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.PAGE_AXIS));
        
        interfacePanel.add(imageLogoPanel());
        interfacePanel.add(connectionPanel());
        interfacePanel.add(inputPanel());
        interfacePanel.add(statusPanel());
        mainFrame.add(interfacePanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    /**
     * Creates connection panel
     * @return JPanel
     */
    private JPanel connectionPanel(){
        JPanel connectionPanel = new JPanel(new MigLayout("insets 10 10 10 10","","")); //"Layout","Column","Row"
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
        
        connectionPanel.add(serialConnectionPanel, "split 2, align center");
        connectionPanel.add(connectionStatus, "wrap");
        connectionPanel.add(buttonConnect, "split 2, align center");
        connectionPanel.add(buttonDisconnect, "wrap");
        
        return connectionPanel;
    }
    
    private JPanel inputPanel(){
        JPanel inputPanel = new JPanel(new MigLayout("insets 10 10 10 10","",""));//"Layout","Column","Row"
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
        
        inputPanel.add(ampLabel, "split 2");
        inputPanel.add(ampComboBox, "wrap");
        
        inputPanel.add(freqLabel, "split 4");
        inputPanel.add(freqSlider, "grow");
        inputPanel.add(freqTextField, "grow");
        inputPanel.add(freqUnitLabel, "grow,wrap");
        
        
        inputPanel.add(sampRateLabel, "split 4");
        inputPanel.add(sampRateSlider, "grow");
        inputPanel.add(sampRateTextField, "grow");
        inputPanel.add(sampUnitLabel, "grow, wrap");
        
        inputPanel.add(buttonStart, "split 2,align center,grow");
        inputPanel.add(buttonStop, "grow,wrap");
        
        return inputPanel;
    }
    
    private JPanel statusPanel(){
        JPanel statusPanel = new JPanel(new MigLayout("insets 10 10 10 10","",""));//"Layout","Column","Row"
        
        //Create border
        BevelBorder bevel = new BevelBorder(BevelBorder.LOWERED);
        EmptyBorder empty = new EmptyBorder(5, 10, 10, 10);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(empty, bevel));

        //statusPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        //statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
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
        JPanel imageLogoPanel = new JPanel(new MigLayout("insets 10 10 10 10","",""));
        imageLogoPanel.setBackground(Color.DARK_GRAY);
        try {
            wwwLogo = ImageIO.read(new File(System.getProperty("user.dir") + "/res/img/logo-wave-water-works-white.png"));
            //wwwLogo = ImageIO.read(new File("C:/Users/mr_co_000/Documents/NetBeansProjects/ODrive/src/res/img/logo-wave-water-works-white.png"));
            
            
            label = new JLabel(new ImageIcon(wwwLogo));
            imageLogoPanel.add(label,"grow,align center");
        } catch (IOException ex) {
            Logger.getLogger(OView.class.getName()).log(Level.WARNING, "Image not found", ex);
        }
        
        
        return imageLogoPanel;
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
    
}