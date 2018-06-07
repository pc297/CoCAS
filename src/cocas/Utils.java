package cocas;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cimlpflab
 */
import java.io.File;

public class Utils {

    public final static String txt = "txt";
    public final static String seq = "seq";
    public final static String fa = "fa";
    public final static String fasta = "fasta";
    public final static String jpg = "jpg";
    

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}