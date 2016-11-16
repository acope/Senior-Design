package helper;

/**
 * Checks the OS system for Windows, Mac, Linux/Unix and Solaris
 * @author https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname
 * @author Austin Copeman modifying code
 */
public class OSSystemCheck {
    private final String OS = System.getProperty("os.name").toLowerCase(); //Find the OS type
    private final String ARCH = System.getProperty("os.arch"); // Find the JVM architecture 32bit=x86 64bit=64
    
    /**
     * Main constructor
     */
    public OSSystemCheck() {
    }
    
    /**
     * Checks to see if the system is Windows
     * @return true if Windows
     */
    public boolean isWindows(){
        return (OS.contains("win"));
    }
    
    /**
     * Checks to see if the system is Mac
     * @return true if Mac
     */
    public boolean isMac(){
        return (OS.contains("mac"));
    }
    
    /**
     * Checks to see if the system is Unix/Linux
     * @return true if Unix/Linux
     */
    public boolean isUnix(){
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
    
    /**
     * Checks to see if the system in Solaris
     * @return true if Solaris
     */
    public boolean isSolaris(){
        return (OS.contains("sunos"));
    }
    
    /**
     * Get the OS type
     * e.g. Windows, Mac, Linux/Unix, Solaris
     * @return OS type
     */
    public String getOSName(){
        return OS;
    }
    
    /**
     * Get the architecture of the JVM
     * @return x86 or 64
     */
    public String getJVMArch(){
        return ARCH;
    }
}
