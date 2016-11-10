
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

    public DllLoader() {
    }

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
