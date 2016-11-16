package helper;

import java.util.concurrent.TimeUnit;

/**
 * Modified from
 * https://www.mkyong.com/java/java-time-elapsed-in-days-hours-minutes-seconds/
 * @author mkyong
 * @author modified by Austin Copeman
 * @version 1.1
 */
public class UpTimeCounter implements Runnable{
    private long startTime = System.currentTimeMillis();
    private long currentTime = startTime;
    private long days;
    private long hours;
    private long minutes;
    private long seconds;
    private String upTime;
    private boolean run = false; //True = start False = stop
    Thread t;;
    
    /**
     * Main constructor
     * Creates a new thread and starts the thread
     */
    public UpTimeCounter(){
        this.t = new Thread(this);
        run = false;
        startThread(); //Bad implementation to start thread in constructor need to fix
    }
    
    @Override
    public void run(){
        if (run = true){
            currentTime = System.currentTimeMillis();
            getDurationBreakdown(currentTime - startTime);
        }
    }
    
    /**
     * starts the thread
     */
    private void startThread(){
        t.start();
    }
    
    /**
     * Starts the up time counter
     * Retrieves current system time
     */
    public void start(){
        run = true;
        startTime = System.currentTimeMillis();
        currentTime = startTime;
    }
    
    /**
     * Stops running of timer
     */
    public void stop(){
        run = false;
    }
    
    /**
     * Get current up time
     * @return up time
     */
    public String getUpTime(){
        if(run == true){
            run();
        }
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
