package helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

import org.zu.ardulink.Link;

/**
 * Used for safety purposes, sends a predetermined message to Arduino at a specified rate to acknowledge serial connection is still available
 * @author Austin Copeman
 */
public class SafetyTimer implements ActionListener{
    private int time = 100;
    private String message;
    private final Link link;
    private Timer t;

    /**
     * Main constructor
     * Sets link name and message sent is null
     * @param link 
     */
    public SafetyTimer(String link) {
        message = null;
        this.link = Link.getInstance(link);
        init();
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety timer constructor {0}", message + ", " + this.link.getName() + ", " + time);
    }
    
    /**
     * Override constructor for safety timer
     * Default safety time is 100 milliseconds
     * @param link 
     * @param message 
     */
    public SafetyTimer(String link, String message) {
        this.link = Link.getInstance(link);
        this.message = message;
        init();
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety timer constructor {0}", message + ", " + this.link.getName() + ", " + time );
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
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety timer constructor {0}", message + ", " + this.link.getName() + ", " + time );
    }
    
    /**
     * Sends message to Arduino
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean sent = link.writeSerial(message);
        if (sent == false){
            Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety not sent to Arduino");
        }
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
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety timer started");
    }
    
    /**
     * Stops the timer
     */
    public void stop(){
        t.stop();
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety timer stopped");
    }
    
    /**
     * Sets the time in milliseconds to perform action event for safety function
     * @param time 
     */
    public void setSendTime(int time){
        this.time = time;
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "New safety time: {0}", this.time );
    }
    
    /**
     * Gets the time for safety
     * @return time in milliseconds
     */
    public int getSendTime(){
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety time: {0}", this.time );
        return time;
    }
    
    /**
     * Sets the message to be sent
     * @param message 
     */
    public void setMessage(String message){
        this.message = message;
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "New safety message: {0}", this.message );
    }
    
    /**
     * Gets the message to be sent
     * @return message
     */
    public String getMessage(){
        Logger.getLogger(SafetyTimer.class.getName()).log(Level.INFO, "Safety message: {0}", this.message );
        return message;
    }
}
