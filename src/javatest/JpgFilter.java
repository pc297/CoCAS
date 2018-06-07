


package javatest;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cimlpflab
 */
import cocas.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
public class JpgFilter extends FileFilter {

//Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.jpg))
                
                 {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Jpeg files  (*.jpg)";
    }
}
