package helper;

/**
 * Conversions for data
 * @author Austin Copeman
 */
public class DataConversion {
    
    /**
     * Data conversion constructor 
     */
    public DataConversion() {
    }
    
    /**
     * Calculates the current using Ohm law<br>
     * v=ir
     * @param voltage in volts
     * @param resistance in ohm
     * @return current in Amp
     */
    public double calculateCurrent(double voltage, int resistance){      
        return voltage/resistance;
    }
    
    /**
     * Calculates the power<br>
     * P=V^2/r
     * @param voltage in volts
     * @param resistance in ohm
     * @return Power in watts in power
     */
    public double calculatePower(double voltage, int resistance){
        return (voltage*voltage)/resistance;
    } 
    
    /**
     * Takes raw data from Arduino and converts into actual RPM
     * @param rawRPM raw RPM from encoder
     * @param sampleRate sampling rate from Arduino
     * @return actual RPM
     */
    public double convertRawRPM(int rawRPM, int sampleRate){
        
        return (double)rawRPM * (60.0 / (sampleRate)) / 360.0;
    }
    
    /**
     * Takes in raw voltage from Arduino and converts to actual voltage
     * @param rawVoltage raw voltage from Arduino
     * @return actual voltage
     */
    public double convertRawVoltage(int rawVoltage){
        return (((double)rawVoltage * 5 * 14.5)  / 1024); //255 = range, 5 = 0v to 5v 14.5 = voltage divider ratio
    }
}
