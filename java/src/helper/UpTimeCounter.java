package helper;

import java.text.*;
import java.util.*;

/**
 * Modified from
 * https://www.mkyong.com/java/java-time-elapsed-in-days-hours-minutes-seconds/
 * @author mkyong
 * @author modified by Austin Copeman
 */
public class UpTimeCounter implements Runnable{
    SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    
    public UpTimeCounter(){
        
    }
    
    public void run(){
        
    }
    
    public void start(){
        
    }
    
    public void stop(){
        
    }
    
    public String timeDiff(Date start, Date end){
        long different = end.getTime() - start.getTime();
        
        long secondsMilli = 1000; //Seconds in milliseconds
        long minutesMilli = secondsMilli * 60; //Minutes in milli
        long hoursMilli = minutesMilli * 60; //Hours in milli
        long daysMilli = hoursMilli *24;
        
        long elapsedDays = different / daysMilli;
        different = different % daysMilli;
        
        long elapsedHours = different / hoursMilli;
        different = different % hoursMilli;
        
        long elapsedMinutes = different / secondsMilli;
        
        String upTime = Long.toString(elapsedDays) + " days" + Long.toString(elapsedHours) + "hours"
                        + Long.toString(elapsedMinutes) + " minutes";
        
        return upTime;
    }
}
