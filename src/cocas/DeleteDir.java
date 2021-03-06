/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cocas;

/**
 *
 * @author Pierre Cauchy
 */
import java.io.File;

class DeleteDir {
  public static void main(String args[]) {
    deleteDirectory(new File(args[0]));
  }

  static public boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return( path.delete() );
  }
}
