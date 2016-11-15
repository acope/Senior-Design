package helper;

/**
 * Conversions for data
 * @author Austin Copeman
 * @version 1.1
 */
public class DataConversion {

    public DataConversion() {
    }
    
    /**
     * v=ir
     * @param voltage
     * @param resistance in ohm
     * @return current in Amp
     */
    public double calculateCurrent(double voltage, int resistance){      
        return voltage/resistance;
    }
    
    /**
     * P=V^2/r
     * @param voltage
     * @param resistance
     * @return Power in watts
     */
    public double calculatePower(double voltage, int resistance){
        return (voltage*voltage)/resistance;
    } 
}
