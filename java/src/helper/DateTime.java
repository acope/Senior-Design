package helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Austin Copeman
 * @version 1.1
 */
public class DateTime {
    
    /**
     * Date time main constructor
     */
    public DateTime() {
    }
    
        /**
     * Gets time from system
     * @return HH:mm:ss
     */
    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get time number only
     * @return HHmmss
     */
    public String getTimeNumOnly(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date 
     * @return MM/dd/yyyy
     */
    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get a custom date
     * @param customDate String
     * @return formatted custom date
     */
    public String getCustomDate(String customDate){
        SimpleDateFormat sdf = new SimpleDateFormat(customDate);
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date numbers only
     * @return yyyyMMdd
     */
    public String getDateNumOnly(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Get date and time
     * @return MM/dd/yyyy HH:mm:ss
     */
    public String getDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Returns the seconds of current system time
     * @return seconds
     */
    public int getSecond(){
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }
    
     /**
     * Returns the minute of current system time
     * @return minute
     */
    public int getMinute(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }

    /**
     * Returns the hour of current system time
     * @return hour
     */    
    public int getHour(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }

    /**
     * Returns the day of current system time
     * @return day
     */    
    public int getDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }

    /**
     * Returns the month of current system time
     * @return month
     */    
    public int getMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }

    /**
     * Returns the year of current system time
     * @return year
     */    
    public int getYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.parseInt(sdf.format(Calendar.getInstance().getTime()));
    }
    
    
}
