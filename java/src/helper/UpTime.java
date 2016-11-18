/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;

/**
 *
 * @author mr_co_000
 */
public class UpTime implements ActionListener{
    
    private int COUNT_TIME = 1000;
    private long startTime = System.currentTimeMillis();
    private long days;
    private long hours;
    private long minutes;
    private long seconds;
    private String upTime;
    private Timer t;
    
    public UpTime() {
        init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getDurationBreakdown(System.currentTimeMillis()-startTime);
    }
    
    /**
     * Initialize the timer
     */
    private void init(){
        t = new Timer(COUNT_TIME, this);
    }
    
     /**
     * Starts the timer
     */
    public void start(){
        startTime = System.currentTimeMillis();
        t.start();
        getDurationBreakdown(0); //Used to prevent null
    }
    
    /**
     * Stops the timer
     */
    public void stop(){
        t.stop();
    }
    
        /**
     * Get current up time
     * @return up time
     */
    public String getUpTime(){
        return upTime;
    }     
    
    /**
     * Get the current amount of days
     * @return days
     */
    public long getDays() {
        return days;
    }
    
    /**
     * Get the current amount of hours
     * @return hours
     */
    public long getHours() {
        return hours;
    }
    
    /**
     * Get the current amount of minutes
     * @return minutes
     */
    public long getMinutes() {
        return minutes;
    }
    
    /**
     * get the current amount of seconds
     * @return seconds
     */
    public long getSeconds() {
        return seconds;
    }
    
        /**
     * Convert a millisecond duration to a string format
     * 
     * @param millis A duration to convert to a string form
     */
    private void getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        upTime = sb.toString();
    }
    
    
    
}
