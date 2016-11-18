package helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import org.zu.ardulink.Link;

/**
 *
 * @author Austin Copeman
 */
public class SafetyTimer implements ActionListener{
    private int time = 100;
    private String message;
    private final Link link;
    private Timer t;
    
    
    /**
     * Main constructor for safety timer
     * Default safety time is 100 milliseconds
     * @param link 
     * @param message 
     */
    public SafetyTimer(String link, String message) {
        this.link = Link.getInstance(link);
        this.message = message;
        init();
    }
    
    /**
     * Override constructor to set link and time for safety time
     * @param link
     * @param message
     * @param time 
     */
    public SafetyTimer(String link, String message, int time){
        this.link = Link.getInstance(link);
        this.message = message;
        this.time = time;
        init();
    }
    
    /**
     * Sends message to Arduino
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        link.writeSerial(message);
    }
    
    /**
     * Initialize the timer for safety message
     */
    private void init(){
        t = new Timer(time, this);
    }
    
    /**
     * Starts the timer
     */
    public void start(){
        t.start();
    }
    
    /**
     * Stops the timer
     */
    public void stop(){
        t.stop();
    }
    
    /**
     * Sets the time in milliseconds to perform action event for safety function
     * @param time 
     */
    public void setSendTime(int time){
        this.time = time;
    }
    
    /**
     * Gets the time for safety
     * @return time in milliseconds
     */
    public int getSendTime(){
        return time;
    }
    
    /**
     * Sets the message to be sent
     * @param message 
     */
    public void setMessage(String message){
        this.message = message;
    }
    
    /**
     * Gets the message to be sent
     * @return message
     */
    public String getMessage(){
        return message;
    }
}
