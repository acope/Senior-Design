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
    public double calculateCurrent(int voltage, int resistance){      
        return (double)voltage/resistance;
    }
    
    /**
     * P=V^2/r
     * @param voltage
     * @param resistance
     * @return Power in watts
     */
    public double calculatePower(int voltage, int resistance){
        return (double)(voltage*voltage)/resistance;
    } 
}
