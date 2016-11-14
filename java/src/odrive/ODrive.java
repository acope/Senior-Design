package odrive;

import helper.DllLoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class 
 * Checks for Java 32bit or 64bit version for Windows
 * @author Austin Copeman
 * @version 1.2
 */
public class ODrive {
    private static final String RXTX_SERIAL_DLL = "rxtxSerial.dll";
    private static final String PATH_WIN_32 = "/res/winDLLs/32bit/"; 
    private static final String PATH_WIN_64 = "/res/winDLLs/64bit/"; 
    
    public static void main(String[] args) throws IOException {
        String osArch = System.getProperty("os.arch"); //32bit=x86 64bit=64
        String osName = System.getProperty("os.name"); //OS used: Windows
        
        //Check for windows architecture
        if(osName.contains("Windows")){
            Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Operation System: " + osName, osName);           
            //check for JVM bit, assumes JVM matches computer
            if(osArch.contains("86")){
                //32 bit java
                DllLoader.extractAndLoad(PATH_WIN_32 + RXTX_SERIAL_DLL);
                Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Java Architecture: " + osArch, osArch);  
            }else{
                //64 bit java
                DllLoader.extractAndLoad(PATH_WIN_64 + RXTX_SERIAL_DLL);
                Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Java Architecture: " + osArch, osArch);  
            }
        }
        
        OController controller = new OController();
    }
    
}
