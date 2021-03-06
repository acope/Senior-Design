
package helper;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Oneiros http://stackoverflow.com/questions/4775487/java-3d-hello-world-jar-freeze
 */
public class DllLoader {
    
    /**
     * Main constructor for the dynamically linked library loader
     */
    public DllLoader() {
    }
    
    /**
     * Extracts and loads the dll from specified file path
     * Extracts from jar file at the specified file path e.g. /res/winDLLs/32bit/
     * Copies the file to root location then loads the dll onto the system
     * Must include full file path and file extension
     * @param dll dll file
     * @throws IOException IOException
     */
    public static void extractAndLoad(String dll) throws IOException {
        int aux = dll.lastIndexOf('/');
        if (aux == -1) {
            aux = dll.lastIndexOf('\\');
        }
        File dllCopy = new File((aux == -1) ? dll : dll.substring(aux + 1));
        try {
            System.load(dllCopy.getAbsolutePath());
        } catch (UnsatisfiedLinkError e1) {
            try {
                DllLoader.copyFile(DllLoader.class.getResourceAsStream(dll), dllCopy);
                System.load(dllCopy.getAbsolutePath());
            } catch (IOException e2) {
            }
        }
    }
    
    /**
     * Copies the files
     * @param pIn
     * @param pOut
     * @throws IOException 
     */
    private static void copyFile(InputStream pIn, File pOut) throws IOException {
        if (!pOut.exists()) {
            pOut.createNewFile();
        }
        DataInputStream dis = new DataInputStream(pIn);
        FileOutputStream fos = new FileOutputStream(pOut);
        byte[] bytes = new byte[1024];
        int len;
        while ((len = dis.read(bytes)) > 0) {
            fos.write(bytes, 0, len);
        }
        dis.close();
        fos.close();
    }
}
