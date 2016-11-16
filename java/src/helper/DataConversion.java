package helper;

/**
 * Conversions for data
 * @author Austin Copeman
 * @version 1.1
 */
public class DataConversion {
    
    /**
     * Data conversion constructor 
     */
    public DataConversion() {
    }
    
    /**
     * v=ir
     * @param voltage in volts
     * @param resistance in ohm
     * @return current in Amp
     */
    public double calculateCurrent(double voltage, int resistance){      
        return voltage/resistance;
    }
    
    /**
     * P=V^2/r
     * @param voltage in volts
     * @param resistance in ohm
     * @return Power in watts in power
     */
    public double calculatePower(double voltage, int resistance){
        return (voltage*voltage)/resistance;
    } 
}
