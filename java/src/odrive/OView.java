package odrive;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import org.zu.ardulink.gui.SerialConnectionPanel;

public class OView extends JFrame{
    private final int MAIN_FRAME_HEIGHT = 768; //X
    private final int MAIN_FRAME_WIDTH = 1024; //Y
    
    public OView(){
        initGUI();
    }
    
    private void initGUI(){
        JFrame mainFrame = new JFrame("ODrive Test Simulation");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
        mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
        mainFrame.setResizable(false);
        
        
        // MiG Layout, Column and Row constraints as arguments.
        MigLayout layout = new MigLayout("debug");
        //Create a new panel with MiG Layout constraints
        JPanel mainPanel = new JPanel(layout); 
        //Create JButtons
        JButton buttonStart = new JButton("Start");
        JButton buttonStop = new JButton("Stop");
        //Create JLabels
        JLabel ampLabel = new JLabel("Amplitude:");
        JLabel freqLabel = new JLabel("Frequency:");
        JLabel statusLabel = new JLabel("Status:");
        JLabel upTimeLabel = new JLabel("Up Time:");
        //Create JTextfield
        JTextField  freqTextField = new JTextField();
        JTextField  statusTextField = new JTextField();
        JTextField upTimeTextField = new JTextField();
        //Create ComboBox
        String[] ampString = {"Amp1", "Amp2", "Amp3", "Amp4"};
        JComboBox ampComboBox = new JComboBox(ampString);
        
        //Create other needed panels
        SerialConnectionPanel serialConnectionPanel = new SerialConnectionPanel(); //Ardulink Panel
        
        //Define Text Field Attributes
        statusTextField.setEditable(false);
        upTimeTextField.setEditable(false);

        //Add objects to panel
        mainPanel.add(serialConnectionPanel, "wrap");
        
        mainPanel.add(ampLabel, "split 2");
        mainPanel.add(ampComboBox, "wrap");
        
        mainPanel.add(freqLabel, "split 2");
        mainPanel.add(freqTextField, "grow, wrap");
        
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
}