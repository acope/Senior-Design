package odrive;

import helper.DllLoader;
import helper.OSSystemCheck;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ODrive Main class 
 * Designed for use for Wave Water Works in conjuction with Oakland University <br>
 * Utilizes Arduolink to serially communicate with the Arduino platform www.ardulink.org and https://github.com/Ardulink<br>
 * Serial connection is established with RXTX library and dll libraries<br>
 * RXTX dll are automatically created for Windows system ONLY! To use with other systems dll must be installed manually.<br>
 * Please see RXTX wiki for more support rxtx.qbang.org<br>
 * @author Austin Copeman
 * @version 1.4
 */
public class ODrive {
    private static final String RXTX_SERIAL_DLL = "rxtxSerial.dll";
    private static final String PATH_WIN_32 = "/res/winDLLs/32bit/"; 
    private static final String PATH_WIN_64 = "/res/winDLLs/64bit/"; 
    
    /**
     * Main
     * Checks OS and JVM for dll
     * @param args String array of arguments
     * @throws IOException IOException
     */
    public static void main(String[] args) throws IOException {
        OSSystemCheck sc = new OSSystemCheck();
        
        if(sc.isWindows()){
            Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Operation System: {0}", sc.getOSName());
            if(sc.getJVMArch().contains("86")){
                //32 bit java
                DllLoader.extractAndLoad(PATH_WIN_32 + RXTX_SERIAL_DLL);
                Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Java Architecture: {0}", sc.getJVMArch());  
            }else{
                //64 bit java
                DllLoader.extractAndLoad(PATH_WIN_64 + RXTX_SERIAL_DLL);
                Logger.getLogger(ODrive.class.getName()).log(Level.INFO, "Java Architecture: {0}", sc.getJVMArch()); 
            }
        }else if(sc.isUnix()){
            Logger.getLogger(ODrive.class.getName()).log(Level.WARNING, "Operation System {0} is not supported for auto RXTX .dll files. Please manually install RXTX to operate", sc.getOSName()); 
        }else if(sc.isMac()){
            Logger.getLogger(ODrive.class.getName()).log(Level.WARNING, "Operation System {0} is not supported for auto RXTX .dll files. Please manually install RXTX to operate", sc.getOSName()); 
        }else if(sc.isUnix()){
            Logger.getLogger(ODrive.class.getName()).log(Level.WARNING, "Operation System {0} is not supported for auto RXTX .dll files. Please manually install RXTX to operate", sc.getOSName()); 
        }else{
            Logger.getLogger(ODrive.class.getName()).log(Level.WARNING, "Operation System {0} is not supported for auto RXTX .dll files. Please manually install RXTX to operate", sc.getOSName()); 
        }
           
        OController controller = new OController();
    }
}
