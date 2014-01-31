package eu.scape_project.pt.util.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementing classes of this interface handle the transportation of files and
 * directories from local to remote filesystem and vice-versa. A remote filesystem
 * may be HDFS as for example.
 * 
 * @author Rainer Schmidt [rschmidt13]
 * @author Matthias Rella [myrho]
 */
public abstract class Filer {

    protected String dir = "";

    /**
     * Abstract factory method to create appropriate file for given uri
     */
    public static Filer create(String strUri) throws IOException{
        URI uri = null;
        try {
            uri = new URI(strUri);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String scheme = uri.getScheme();
        if( scheme.equals("hdfs")) {
            return new HDFSFiler(uri);
        }
        throw new IOException("no appropriate filer for URI " + strUri + " found");
    }

    public String getTmpDir() {
        // TODO introduce a namespace for temp files so that
        // other running tasks on the machine don't interfere
        return System.getProperty("java.io.tmpdir") 
                + System.getProperty("file.separator");
    }
    
    public abstract void setDirectory(String strDir ) throws IOException;

    /**
     * Copies a file from a remote filesystem to the local one.
     */
    public abstract File copyFile(String strSrc, String strDest) throws IOException; 
    /**
     * Copies a file or directory from the local filesystem to a remote one.
     */
    public abstract void depositDirectoryOrFile(String srcSrc, String strDest) throws IOException; 

    /**
     * Copies a directory from the local filesystem to a remote one.
     */
    public abstract void depositDirectory(String strSrc, String strDest) throws IOException;
    
    /**
     * Copies a file from the local filesystem to a remote one.
     */
    public abstract void depositFile(String strSrc, String strDest) throws IOException;

    /**
     * Copies file to local filesystem.
     */
    public abstract void localize() throws IOException;

    /**
     * Copies file from local filesystem to remote one.
     */
    public abstract void delocalize() throws IOException;

    /**
     * Gets the local file reference of the filer's file.
     * @return String fileRef
     */
    public abstract String getFileRef();

    /**
     * Gets the input stream of a file.
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * Gets the output stream to a file.
     */
    public abstract OutputStream getOutputStream() throws IOException;

}
