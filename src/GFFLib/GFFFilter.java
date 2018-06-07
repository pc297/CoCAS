/****************************
 * @author Adrien Jeanniard *
 * @date 2 avr. 2008	    *
 ****************************/

package GFFLib;

import java.io.File;

public class GFFFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".gff");
    }
    
    public String getDescription() {
        return "GFF files";
    }
}