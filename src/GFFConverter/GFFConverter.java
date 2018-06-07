/****************************
 * @author Adrien Jeanniard *
 * @date 20 may 2008	    *
 ****************************/

package GFFConverter;

import GFFLib.*;
import java.awt.Cursor;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class GFFConverter extends javax.swing.JFrame {
    
    /** Creates new form GFFConverter */
    public GFFConverter() {
	try {
	UIManager.setLookAndFeel(
	UIManager.getSystemLookAndFeelClassName());
	} 
	catch (Exception e) { }
	initComponents();
	GFFFileChooser.setFileFilter(new GFFFilter());
    }
    private void openFile(){
	int returnVal = GFFFileChooser.showOpenDialog(inputFileBrowseButton.getParent());
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	   inputFileTextField.setText(GFFFileChooser.getSelectedFile().getAbsolutePath());
	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GFFFileChooser = new javax.swing.JFileChooser();
        formatRadioButtonGroup = new javax.swing.ButtonGroup();
        fileRadiobuttonGroup = new javax.swing.ButtonGroup();
        inputFileLabel = new javax.swing.JLabel();
        inputFileTextField = new javax.swing.JTextField();
        inputFileBrowseButton = new javax.swing.JButton();
        convertButton = new javax.swing.JButton();
        bedRadioButton = new javax.swing.JRadioButton();
        sgrRadioButton = new javax.swing.JRadioButton();
        outputFormatLabel = new javax.swing.JLabel();
        overwriteRadioButton = new javax.swing.JRadioButton();
        copyRadioButton = new javax.swing.JRadioButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileMenu_open = new javax.swing.JMenuItem();
        fileMenu_exit = new javax.swing.JMenuItem();
        aboutMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GFFConverter");

        inputFileLabel.setText("File to convert");

        inputFileBrowseButton.setText("Browse");
        inputFileBrowseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                inputFileBrowseButtonMouseReleased(evt);
            }
        });

        convertButton.setText("Convert");
        convertButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                convertButtonMouseReleased(evt);
            }
        });

        formatRadioButtonGroup.add(bedRadioButton);
        bedRadioButton.setSelected(true);
        bedRadioButton.setText(".bed");

        formatRadioButtonGroup.add(sgrRadioButton);
        sgrRadioButton.setText(".sgr");

        outputFormatLabel.setText("Output format");

        fileRadiobuttonGroup.add(overwriteRadioButton);
        overwriteRadioButton.setText("Overwrite current file");

        fileRadiobuttonGroup.add(copyRadioButton);
        copyRadioButton.setSelected(true);
        copyRadioButton.setText("Create a copy");

        fileMenu.setText("File");

        fileMenu_open.setText("Open");
        fileMenu_open.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileMenu_openMouseReleased(evt);
            }
        });
        fileMenu.add(fileMenu_open);

        fileMenu_exit.setText("Exit");
        fileMenu_exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileMenu_exitMouseReleased(evt);
            }
        });
        fileMenu.add(fileMenu_exit);

        menuBar.add(fileMenu);

        aboutMenu.setText("?");

        jMenuItem1.setText("About");
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseReleased(evt);
            }
        });
        aboutMenu.add(jMenuItem1);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(convertButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputFormatLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sgrRadioButton)
                            .addComponent(bedRadioButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputFileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(copyRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(overwriteRadioButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(inputFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputFileBrowseButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputFileLabel)
                    .addComponent(inputFileBrowseButton)
                    .addComponent(inputFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyRadioButton)
                    .addComponent(overwriteRadioButton))
                .addGap(7, 7, 7)
                .addComponent(outputFormatLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bedRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sgrRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(convertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputFileBrowseButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputFileBrowseButtonMouseReleased
	openFile();
}//GEN-LAST:event_inputFileBrowseButtonMouseReleased

    private void convertButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertButtonMouseReleased
	String inputFileName, outputFileName, outputFormat=".out";
	File inputFile, outputFile;
	
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	inputFileName = inputFileTextField.getText();
	if(bedRadioButton.isSelected()) outputFormat = ".bed";
	if(sgrRadioButton.isSelected()) outputFormat = ".sgr";
	if( inputFileName.endsWith(".gff") ){
	    outputFileName = inputFileName.replaceFirst(".gff$",outputFormat);
	}
	else{
	    outputFileName = inputFileName+outputFormat;
	}
		
	inputFile = new File(inputFileName);
	outputFile = new File(outputFileName);
	
	if(!inputFile.exists()){
	    JOptionPane.showMessageDialog(convertButton.getParent(), "Specified input file\n"+inputFile.getPath()+"\nnot found !", "File not found !", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	if(outputFile.exists() && copyRadioButton.isSelected()){
	    int choice = JOptionPane.showConfirmDialog(convertButton.getParent(), "Specified output file\n'"+outputFile.getPath()+"'\nalready exist.\n Overwrite?", "File already exist !", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    if( choice != JOptionPane.YES_OPTION) return;
	}
	
		
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
			if(bedRadioButton.isSelected()) {
			    outputWriter.write(data.seqname+"\t"+data.start+"\t"+data.end+"\n");
			}
			//Ecriture en format .sgr
			if(sgrRadioButton.isSelected()) {
			    outputWriter.write(data.seqname+"\t"+(data.start+data.end)/2+"\t"+data.score+"\n");
			}
		    }
		    catch (Exception e) {
			setCursor(null);
			JOptionPane.showMessageDialog(convertButton.getParent(), "Bad .gff format ! Please Check your file @line"+nLine+":\n"+ligne+"\n\nSystem error : "+e.toString(), "Format error", JOptionPane.ERROR_MESSAGE);
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
	    if(overwriteRadioButton.isSelected()) inputFile.delete();
	    
	}
	catch (Exception e) {
	    setCursor(null);
	    JOptionPane.showMessageDialog(convertButton.getParent(), "An error occurs while manipulating a file :\n"+e.toString(), "File error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	setCursor(null);
	JOptionPane.showMessageDialog(convertButton.getParent(), "Conversion terminated", "Conversion terminated", JOptionPane.INFORMATION_MESSAGE);
	return;
    }//GEN-LAST:event_convertButtonMouseReleased

    private void fileMenu_exitMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenu_exitMouseReleased
	System.exit(0);
}//GEN-LAST:event_fileMenu_exitMouseReleased

    private void fileMenu_openMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileMenu_openMouseReleased
	openFile();
    }//GEN-LAST:event_fileMenu_openMouseReleased

    private void jMenuItem1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseReleased
	String info = "GFFConverter is a simple GUI to quickly and easily convert .gff files.\n" +
		"\n.bed format used : chromosome | start | end\n"+
		".sgr format used : chromosome | center | score\n"+
		"\nGFFConverter coded, created and designed by Adrien Jeanniard.\n" +
		"adrien.jeanniard@etumel.univmed.fr\n" +
		"copyright (c) 2008\n";

	JOptionPane.showMessageDialog(jMenuItem1.getParent(), info, "About GFF Area&Peak", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem1MouseReleased
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		new GFFConverter().setVisible(true);
	    }
	});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser GFFFileChooser;
    private javax.swing.JMenu aboutMenu;
    private javax.swing.JRadioButton bedRadioButton;
    private javax.swing.JButton convertButton;
    private javax.swing.JRadioButton copyRadioButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenu_exit;
    private javax.swing.JMenuItem fileMenu_open;
    private javax.swing.ButtonGroup fileRadiobuttonGroup;
    private javax.swing.ButtonGroup formatRadioButtonGroup;
    private javax.swing.JButton inputFileBrowseButton;
    private javax.swing.JLabel inputFileLabel;
    private javax.swing.JTextField inputFileTextField;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel outputFormatLabel;
    private javax.swing.JRadioButton overwriteRadioButton;
    private javax.swing.JRadioButton sgrRadioButton;
    // End of variables declaration//GEN-END:variables
    
}