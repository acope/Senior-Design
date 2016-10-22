package odrive;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import javax.swing.Action;

import org.zu.ardulink.Link;

public class OController{
    private OView view;
    private OSerialComm serialComm;
    
    public OController(){
        view = new OView();
        serialComm = new OSerialComm();
        
        //Add action listeners
        connectButtonActionListener();
        disconnectButtonActionListener();
        startButtonActionListener();
        stopButtonActionListener();
    }
    
    //#TODO add handshaking
    private void connectButtonActionListener(){
        view.buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String comPort = view.serialConnectionPanel.getConnectionPort();
               String baudRateS = view.serialConnectionPanel.getBaudRate();
               
               try{
                   int baudRate = Integer.parseInt(baudRateS);
                   Link.getDefaultInstance().connect(comPort, baudRate);
               }
               catch(Exception ex){
                   ex.printStackTrace();
                   String message = ex.getMessage();
                   if(message == null || message.trim().equals(" ")){
                       message = "Generic Error on Connection!";
                   }
                   view.statusTextField.setText(message);
                   view.statusTextField.setBackground(Color.red);
               }
            }
        });
    }
    
    private void disconnectButtonActionListener(){
        view.buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Link.getDefaultInstance().disconnect();
            }
        });
    }
    
    private void startButtonActionListener(){
        view.buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });    
    }
    
    private void stopButtonActionListener(){
        view.buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }
    
    private void serialConnectionPanelActionListener(){
        
    }
    
    private void freqTextFieldActionListener(){
        
    }
    
    private void ampComboBoxActionListener(){
        
    }
    
}
