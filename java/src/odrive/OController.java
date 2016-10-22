package odrive;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.zu.ardulink.Link;
import org.zu.ardulink.RawDataListener;

public class OController{
    private OView view;
    private OSerialComm serialComm;
    
    private final Link link = Link.getDefaultInstance();
    
    private String rawToString;
    
    public OController(){
        view = new OView();
        serialComm = new OSerialComm();
        
        //Add action listeners
        connectButtonActionListener();
        disconnectButtonActionListener();
        startButtonActionListener();
        stopButtonActionListener();
        freqTextFieldActionListener();
        ampComboBoxActionListener();
        rawDataListener();
    }
    
    //#TODO add handshaking
    private void connectButtonActionListener(){
        view.buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String comPort = view.serialConnectionPanel.getConnectionPort();
               String baudRateS = view.serialConnectionPanel.getBaudRate();
               
               if(comPort == null || "".equals(comPort)){
                   JOptionPane.showMessageDialog(view.buttonConnect,"Invalid COM PORT set", "Error", JOptionPane.ERROR_MESSAGE);
               } else if (baudRateS == null || "".equals(baudRateS)){
                   JOptionPane.showMessageDialog(view.buttonConnect,"Invalid baud rate set", "Error", JOptionPane.ERROR_MESSAGE);
               }else{
               
                try{
                    int baudRate = Integer.parseInt(baudRateS);
                    boolean connected = link.connect(comPort, baudRate);
                    if (connected) {
                             view.buttonConnect.setEnabled(false);
                             view.buttonDisconnect.setEnabled(true);
                             view.buttonStart.setEnabled(true);
                             view.buttonStop.setEnabled(true);
                         }

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
            }
        });
    }
    
    private void disconnectButtonActionListener(){
        view.buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean disconnected = link.disconnect();
                if (disconnected) {
                    view.buttonConnect.setEnabled(true);
                    view.buttonDisconnect.setEnabled(false);
                    view.buttonStart.setEnabled(false);
                    view.buttonStop.setEnabled(false);
                }
            }
        });
    }
    
    private void startButtonActionListener(){
        view.buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Add code
            }
        });    
    }
    
    private void stopButtonActionListener(){
        view.buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Add code
            }
        });
    }
    
    private void freqTextFieldActionListener(){
        view.freqTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add code
            }
        });
    }
    
    private void ampComboBoxActionListener(){
        view.ampComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Add code
            }
        });
    }
    
        public String rawDataListener(){
        link.addRawDataListener(new RawDataListener() {
			@Override
			public void parseInput(String id, int numBytes, int[] message) {
				StringBuilder build = new StringBuilder(numBytes + 1);
				for (int i = 0; i < numBytes; i++) {
					build.append((char)message[i]);
				}
				rawToString = build.toString();
                                System.out.println(rawToString);
			}
		});
        
        return rawToString;
    }
    
}
