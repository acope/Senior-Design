package odrive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class OView extends JFrame{
    private JFrame mainFrame;

    private final int MAIN_FRAME_HEIGHT = 768; //X
    private final int MAIN_FRAME_WIDTH = 1024; //Y
    
    public OView(){
        initGUI();
    }
    
    private void initGUI(){
        
        mainFrame = new JFrame("ODrive Test Simulation");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Need to change to disconnect from Arduino then close
       
        //Setting up Content Pane
        mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
        //mainFrame.setLayout(new GridLayout(MAIN_FRAME_ROWS, MAIN_FRAME_COLUMNS));
        
        
        mainFrame.setVisible(true);
    }
}


/*
    public static void addComponentsToPane(Container pane){
        JButton button;
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        
        button = new JButton("Button 1");
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("Button 2");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("Button 3");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        pane.add(button, c);

        button = new JButton("Long-Named Button 4");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 40;      //make this component tall
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(button, c);

        button = new JButton("5");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;       //reset to default
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        c.insets = new Insets(10,0,0,0);  //top padding
        c.gridx = 1;       //aligned with button 2
        c.gridwidth = 2;   //2 columns wide
        c.gridy = 2;       //third row
        pane.add(button, c);
    }
*/