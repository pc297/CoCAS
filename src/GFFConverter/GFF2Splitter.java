/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFFConverter;
import GFFLib.GFFData;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Pierre Cauchy
 */
public class GFF2Splitter {

    File inputFile;
    File outputFile;
    
    public GFF2Splitter(File i, File o)
    {
        inputFile=i;
        outputFile=o;
        
    }
    
    public void convert() {
            try {
	    GFFData data;
	    int nLine = 0;
            String ligne;
	    FileWriter outputWriter = new FileWriter(outputFile);
	    BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
	    
	       
	    ligne = inputReader.readLine();
	    while (ligne != null) {
		if(!ligne.equals("")){
		    try {
			//Parsing du gff
			nLine++;
			data = new GFFData(ligne);
                        //Ecriture en format .bed
			
			    outputWriter.write(data.seqname+":"+data.start+"-"+data.end+"\t"+data.score+"\n");
			
			//Ecriture en format .sgr
			
		    }
		    catch (Exception e) {
			
			JOptionPane.showMessageDialog(new JFrame(), "Bad .gff format ! Please Check your file @line"+nLine+":\n"+ligne+"\n\nSystem error : "+e.toString(), "Format error", JOptionPane.ERROR_MESSAGE);
			inputReader.close();
			outputWriter.close();
			outputFile.delete();
			return;
		    }
		    
		}
		ligne = inputReader.readLine();
	    }
	    inputReader.close();
	    outputWriter.close();
	    //inputFile.delete();
	    
	}
	catch (Exception e) {
	    
	    JOptionPane.showMessageDialog(new JFrame(), "An error occurs while manipulating a file :\n"+e.toString(), "File error", JOptionPane.ERROR_MESSAGE);
	    return;
	}


}
}
