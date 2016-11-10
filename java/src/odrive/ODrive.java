package odrive;

import helper.DllLoader;
import java.io.IOException;

/**
 * Main class 
 * @author Austin Copeman
 * @version 1.1
 */
public class ODrive {
    private static final String RXTX_SERIAL_DLL = "rxtxSerial.dll";
    private static final String PATH_WIN_32 = "/res/winDLLs/32bit/"; 
    private static final String PATH_WIN_64 = "/res/winDLLs/64bit/"; 
    
    public static void main(String[] args) throws IOException {
        DllLoader.extractAndLoad(PATH_WIN_64 + RXTX_SERIAL_DLL);
        OController controller = new OController();
    }
    
}
