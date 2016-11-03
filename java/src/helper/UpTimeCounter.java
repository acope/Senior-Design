package helper;

import java.util.concurrent.TimeUnit;

/**
 * Modified from
 * https://www.mkyong.com/java/java-time-elapsed-in-days-hours-minutes-seconds/
 * @author mkyong
 * @author modified by Austin Copeman
 */
public class UpTimeCounter implements Runnable{
    private long startTime = System.currentTimeMillis();
    private long currentTime = startTime;
    private String upTime;
    private boolean run = false; //True = start False = stop
    Thread t;;
    
    public UpTimeCounter(){
        this.t = new Thread(this);
        run = false;
        t.start();
        
    }
    
    @Override
    public void run(){
        if (run = true){
            currentTime = System.currentTimeMillis();
            getDurationBreakdown(currentTime - startTime);
        }
    }
    
    public void start(){
        run = true;
        startTime = System.currentTimeMillis();
        currentTime = startTime;
    }
    
    public void stop(){
        run = false;
    }
    
    public String getUpTime(){
        if(run == true){
            run();
        }
        return upTime;
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

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

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