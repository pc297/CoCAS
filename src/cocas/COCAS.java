package cocas;

/*
 * COCAS.java
 *
 * Created on 24 janvier 2008, 15:27
 */

/**
 *
 * @author  cauchy
 */
import GFFConverter.GFF2BED;
import GFFConverter.GFF2SGR;
import GFFConverter.GFF2Splitter;
import GFFConverter.GFFConverter;

import GFFPeak.*;
import GFFLib.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;

import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;
import com.affymetrix.genoviz.util.Memer;
import com.affymetrix.genoviz.util.ComponentPagePrinter;

import com.affymetrix.genoviz.util.Memer;

import com.affymetrix.genometry.*;
import com.affymetrix.genometryImpl.*;
import com.affymetrix.genometryImpl.event.GroupSelectionEvent;
import com.affymetrix.genometryImpl.event.GroupSelectionListener;
import com.affymetrix.genometryImpl.event.SeqSelectionEvent;
import com.affymetrix.genometryImpl.event.SeqSelectionListener;
import com.affymetrix.genometryImpl.style.DefaultStateProvider;
import com.affymetrix.genometryImpl.style.StateProvider;
import com.affymetrix.genoviz.util.ComponentPagePrinter;
import com.affymetrix.igb.bookmarks.Bookmark;
import com.affymetrix.igb.bookmarks.BookmarkController;
import com.affymetrix.igb.das.DasDiscovery;
import com.affymetrix.igb.das2.Das2Discovery;
import com.affymetrix.igb.menuitem.*;
import com.affymetrix.igb.view.*;
import com.affymetrix.igb.parsers.XmlPrefsParser;
import com.affymetrix.igb.prefs.*;
import com.affymetrix.igb.bookmarks.SimpleBookmarkServer;
import com.affymetrix.igb.glyph.EdgeMatchAdjuster;
import com.affymetrix.igb.tiers.AffyLabelledTierMap;
import com.affymetrix.igb.tiers.AffyTieredMap.ActionToggler;
import com.affymetrix.igb.tiers.IGBStateProvider;
import com.affymetrix.igb.tiers.MultiWindowTierMap;
import com.affymetrix.igb.util.EPSWriter;
import com.affymetrix.igb.util.LocalUrlCacher;
import com.affymetrix.genometryImpl.util.SynonymLookup;
import com.affymetrix.igb.tiers.IGBStateProvider;
import com.affymetrix.igb.util.UnibrowAuthenticator;
import com.affymetrix.igb.util.UnibrowPrefsUtil;
import com.affymetrix.igb.util.WebBrowserControl;
import com.affymetrix.igb.util.ErrorHandler;
import com.affymetrix.igb.util.ViewPersistenceUtils;
import com.affymetrix.swing.DisplayUtils;

import org.xml.sax.InputSource;
import java.net.*;
import com.affymetrix.genometry.*;
import com.affymetrix.genoviz.parser.GFFParser;
import com.affymetrix.igb.Application;
import com.affymetrix.igb.das2.Das2ServerInfo;
import com.affymetrix.igb.das2.Das2Source;
import com.affymetrix.igb.das2.Das2VersionedSource;
import com.affymetrix.swing.threads.*;
import com.affymetrix.igb.genometry.*;
import com.affymetrix.igb.parsers.*;
import com.affymetrix.igb.util.*;
import com.affymetrix.igb.view.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Vector;
import javatest.ImageCanvas;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import sun.tools.jar.resources.jar_it;


class TextConsole extends JTextArea implements RMainLoopCallbacks
{
    
    
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
        this.append(text);
        //this.update(this.getGraphics());
        this.setCaretPosition(this.getText().length());
        this.update(this.getGraphics());
        //this.repaint();
        //this.getParent().update(this.getParent().getGraphics());
        
        
    }
    
    
    
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }
	
    public String rChooseFile(Rengine re, int newFile) {
	FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
	fd.show();
	String res=null;
	if (fd.getDirectory()!=null) res=fd.getDirectory();
	if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
	return res;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }

    public void rWriteConsole(Rengine re, String text) {
        System.out.print(text);
        this.append(text);
        //this.update(this.getGraphics());
        this.setCaretPosition(this.getText().length());
        this.update(this.getGraphics());
        //this.repaint();
        //this.getParent().update(this.getParent().getGraphics());
    }
}
public class COCAS extends javax.swing.JFrame {
    
       
    
    /** Creates new form COCAS */
    public int numChannels=1;
    public String normType="median";
    public String bgC="T";
    public boolean filesLoaded=false;
    public String fileNames="";
    public ArrayList<String> fileNamesList = new ArrayList<String>(0);
    public ArrayList<Boolean> dyeSwapList = new ArrayList<Boolean>(0);
    public ArrayList<JCheckBox> dyeSwapCheckBoxList = new ArrayList<JCheckBox>(0);
    
    public ArrayList<JComboBox> IPComboBoxList = new ArrayList<JComboBox>(0);
    public ArrayList<JComboBox> inputComboBoxList = new ArrayList<JComboBox>(0);
    //org.jdesktop.beansbinding.BindingGroup bindingGroup = new org.jdesktop.beansbinding.BindingGroup();
    
    private ArrayList<Integer> slideNumberList = new ArrayList<Integer>(0);
    private ArrayList<JTextField> slideNumberTextFieldList = new ArrayList<JTextField>(0);
    public String directoryName="";
    public String RG="T";
    public TextConsole consoleTextArea;
    public String expName="output";
    Rengine re;
    private double avg, stddev, variance;
    DataLoadView data_load_view = null;
    static SingletonGenometryModel gmodel;
    public String title="";
    private boolean useWizard=false;
    public TestWizard.Wizard wizard;
    public String interNormType="none";
    public boolean mergeSlides=false;
    public String mergeReplicatesMethod="mean";
    ArrayList<String> outputFilesList;
    //public JXTreeTable treetable;
    String outputDirectory="";
    GFF2BED gff2bed;
    GFF2SGR gff2sgr;
    GFF2Splitter gff2splitter;
    public String bcMethod="subtract";
    
    int peak_distance = 650;
    double peak_threshold=0;
    double extend_threshold=0;
    boolean useRingoThresholds=false;
    double ringo_percentile=0.999;
    double ringo_extend_percentile=0.99;
    int ringo_window=3;
    boolean usePval=false;
    double peak_threshold_pval=Double.POSITIVE_INFINITY;
    double extend_threshold_pval=Double.POSITIVE_INFINITY;
    
    public Thread thread;
    
    //EvalRExpression thread;
    public COCAS() {
        try {
            
            
            this.setIconImage(getIcon());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            initComponents();
            
            MouseListener popupListener = new PopupListener(fileListPopupMenu);
            fileList.addMouseListener(popupListener);
            
            
            peak_outputFileTextField.setText(System.getProperty("user.dir") + "_peak.gff");
            fileChooser.setFileFilter(new GFFFilter());
            consoleTextArea = new TextConsole();
            consoleTextArea.setColumns(20);
            consoleTextArea.setEditable(false);
            consoleTextArea.setRows(5);
            consoleScrollPane.setViewportView(consoleTextArea);
            ButtonGroup normTypeGroup = new ButtonGroup();
            normTypeGroup.add(intraNormTypeRadioButton_median);
            normTypeGroup.add(intraNormTypeRadioButton_loess);
            normTypeGroup.add(intraNormTypeRadioButton_vsn);
            normTypeGroup.add(intraNormTypeRadioButton_peng);
            ButtonGroup interNormTypeGroup = new ButtonGroup();
            interNormTypeGroup.add(interNormTypeRadioButton_none);
            interNormTypeGroup.add(interNormTypeRadioButton_mean);
            interNormTypeGroup.add(interNormTypeRadioButton_quantile);
            outputTypeCheckBox_gff.setSelected(true);
            outputTypeCheckBox_sgr.setSelected(true);
            intraNormTypeRadioButton_median.setSelected(true);
            interNormTypeRadioButton_none.setSelected(true);
            //jTabbedPane1.setEnabledAt(0, true);
            tabbedPane.setEnabledAt(2, false);
            tabbedPane.setEnabledAt(3, false);
            tabbedPane.setEnabledAt(4, false);
            tabbedPane.setSelectedIndex(0);
            bgCorrectionMethodComboBox.setSelectedIndex(1);
            peak_inputFileTextField.setColumns(20);
            peak_outputFileTextField.setColumns(20);
            
            analysisProgressBar.setVisible(false);
            

            
            
//            jTabbedPane1.setEnabledAt(5, false);
            this.centreFrame(this);
            re = new Rengine(new String[0], false, consoleTextArea);
            System.out.println(re.eval("getwd()"));
            System.out.println(re.eval("dir()"));
            String path = System.getProperty("java.class.path");
            //String path = new File(".").getCanonicalPath();
            System.out.println(path);
            
            /*
            URL url = COCAS.class.getResource("/data/cocasV2-4.R");
            try
            {
               
               BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
               String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    //System.out.println(inputLine);
                    System.out.println(re.eval(inputLine));
                    
                }
                in.close();
 
            }
            
            catch(IOException ioe)
            {
                System.out.println(COCAS.class.getResource("/data/cocasV2-4.R").toString());
            }
            
            //System.out.println(rstring);
            */
            System.out.println("source(unz(\""+path.replace("\\","/")+"\""+ ", \"data/cocasV2-4.R\",\"r\"))");
            System.out.println(re.eval("source(unz(\""+path.replace("\\","/")+"\""+ ", \"data/cocasV2-4.R\",\"r\"))"));
            //System.out.println("source(\""+rstring+"\")");
            //System.out.println(re.eval("source(\""+rstring+"\")"));
            //System.out.println(re.eval("main"));
            
            tabbedPane.remove(4);
                    
                    
            //System.out.println(jPanel9.getSize());
            //jEditorPane1.getDocument().putProperty("i18n", Boolean.TRUE);
            
            
            
            
            
            
            
            
            
            
            /*
            UIManager.put("OptionPane.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("OptionPane.messageFont", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("OptionPane.buttonFont", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("TextField.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("List.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Label.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("ComboBox.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Button.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("TableHeader.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Table.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("ToggleButton.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
             */
            //com.affymetrix.igb.view.Das2LoadView3 view = new com.affymetrix.igb.view.Das2LoadView3();

            //DasFeaturesAction2 load_das_action = new DasFeaturesAction2(gview);
            //load_das_action.actionPerformed(new java.awt.event.ActionEvent(load_das_action,1,""));
            //load_das_action.composeDasFeatRequest();
            //try
            //{
            //FileReader fr = new FileReader("F://Pierre//TwoColor//output.gff");
            //InputStream is = new FileInputStream("F://Pierre//TwoColor//output.gff");
            //gffparser.parse(is, arg1, true);
            //gview.toggleAutoScroll();
            //gview.toggleHairlineLabel();
            //gview.show();
            //gview = igb.getMapView();
            //gview.setVisible(true);
            //jPanel10.add(gview);
            //}
            //catch(IOException ioe)
            //{
            //}
        }  
         
                
               
        catch (ClassNotFoundException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            /*
            UIManager.put("OptionPane.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("OptionPane.messageFont", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("OptionPane.buttonFont", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("TextField.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("List.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Label.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("ComboBox.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Button.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("TableHeader.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("Table.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
            UIManager.put("ToggleButton.font", new javax.swing.plaf.FontUIResource(new Font("MS Reference Sans Serif", Font.PLAIN, 12)));
           */
    //com.affymetrix.igb.view.Das2LoadView3 view = new com.affymetrix.igb.view.Das2LoadView3();
    
        
        //DasFeaturesAction2 load_das_action = new DasFeaturesAction2(gview);
        
        //load_das_action.actionPerformed(new java.awt.event.ActionEvent(load_das_action,1,""));
        //load_das_action.composeDasFeatRequest();
        //try
        //{
        //FileReader fr = new FileReader("F://Pierre//TwoColor//output.gff");
        //InputStream is = new FileInputStream("F://Pierre//TwoColor//output.gff");
        
        //gffparser.parse(is, arg1, true);
        //gview.toggleAutoScroll();
        //gview.toggleHairlineLabel();
        //gview.show();
        
        //gview = igb.getMapView();
        //gview.setVisible(true);
        //jPanel10.add(gview);
        //}
        //catch(IOException ioe)
        //{
        this.show();   
       if(JOptionPane.showConfirmDialog(this, "Would you like to use the express wizard?", "Use Wizard", JOptionPane.YES_NO_OPTION)==0)
       {
            //JOptionPane.showMessageDialog(this, "Wizard selected", "Wizard selected", JOptionPane.INFORMATION_MESSAGE);
	    
                wizard = new TestWizard.Wizard(this,true);
            
            
            this.centreDialog(wizard);
            wizard.setVisible(true);
            
            if(wizard.isComplete())
            {
                useWizard=true;
            normType=wizard.getNormalizationType();
            expName=wizard.getExpName();
            title=wizard.getName();
            slideNumberList=wizard.returnSlideNumberLists();
            fileNamesList=wizard.getFileNames();
            dyeSwapList=wizard.getSwapList();
            directoryName=wizard.getDirectoryName();
            interNormType=wizard.getInterNormalizationType();
            mergeSlides=wizard.getMergeSlides();
            mergeReplicatesMethod=wizard.getReplicateMergeMethod();
            outputDirectory=wizard.getOutputDir();
            bcMethod=wizard.getBGCorrectionMethod();
                
                if(normType.equals("median"))
                {
                    intraNormTypeRadioButton_median.setSelected(true);
                }
                if(normType.equals("lowess"))
                {
                    intraNormTypeRadioButton_loess.setSelected(true);
                }
                if(normType.equals("vsn"))
                {
                    intraNormTypeRadioButton_vsn.setSelected(true);
                }
                if(normType.equals("peng"))
                {
                    intraNormTypeRadioButton_peng.setSelected(true);
                }
                if(bcMethod.equals("none")) bgCorrectionMethodComboBox.setSelectedIndex(0);
                if(bcMethod.equals("subtract")) bgCorrectionMethodComboBox.setSelectedIndex(1);
                if(bcMethod.equals("half")) bgCorrectionMethodComboBox.setSelectedIndex(2);
                if(bcMethod.equals("minimum")) bgCorrectionMethodComboBox.setSelectedIndex(3);
                if(bcMethod.equals("movingmin")) bgCorrectionMethodComboBox.setSelectedIndex(4);
                if(bcMethod.equals("edwards")) bgCorrectionMethodComboBox.setSelectedIndex(5);
                if(bcMethod.equals("normexp")) bgCorrectionMethodComboBox.setSelectedIndex(6);
                if(bcMethod.equals("rma")) bgCorrectionMethodComboBox.setSelectedIndex(7);
                                
                if(interNormType.equals("none"))
                {
                    interNormTypeRadioButton_none.setSelected(true);
                }
                if(interNormType.equals("median"))
                {
                    interNormTypeRadioButton_mean.setSelected(true);
                }
                if(interNormType.equals("quantile"))
                {
                    interNormTypeRadioButton_quantile.setSelected(true);
                }
                if(mergeSlides)
                {
                    mergeSlidesCheckBox.setSelected(true);
                }
                if(mergeReplicatesMethod.equals("mean"))
                {
                    replicateMergeMethodComboBox.setSelectedIndex(0);
                }
                if(mergeReplicatesMethod.equals("agilent"))
                {
                    replicateMergeMethodComboBox.setSelectedIndex(1);
                }
                analysisNameTextField.setText(expName);
                outputDirTextField.setText(outputDirectory);
                this.updateDyeSwapPanel();
                tabbedPane.setEnabledAt(0,true);
                tabbedPane.setSelectedIndex(0);
                this.startAnalysis();
            }
            
            
            
       }
       
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        fileListPopupMenu = new javax.swing.JPopupMenu();
        loadPopupMenuItem = new javax.swing.JMenuItem();
        unloadPopupMenuItem = new javax.swing.JMenuItem();
        tabbedPane = new javax.swing.JTabbedPane();
        settingsPanel = new javax.swing.JPanel();
        outputTypeLabel = new javax.swing.JLabel();
        outputTypeCheckBox_gff = new javax.swing.JCheckBox();
        outputTypeCheckBox_sgr = new javax.swing.JCheckBox();
        outputTypeCheckBox_bed = new javax.swing.JCheckBox();
        runButton = new javax.swing.JButton();
        runLabel = new javax.swing.JLabel();
        analysisNameLabel = new javax.swing.JLabel();
        analysisNameTextField = new javax.swing.JTextField();
        intraNormLabel = new javax.swing.JLabel();
        intraNormTypeRadioButton_median = new javax.swing.JRadioButton();
        intraNormTypeRadioButton_loess = new javax.swing.JRadioButton();
        intraNormTypeRadioButton_vsn = new javax.swing.JRadioButton();
        intraNormTypeRadioButton_peng = new javax.swing.JRadioButton();
        outputDirLabel = new javax.swing.JLabel();
        outputDirTextField = new javax.swing.JTextField();
        outputDirBrowseButton = new javax.swing.JButton();
        interNormLabel = new javax.swing.JLabel();
        interNormTypeRadioButton_none = new javax.swing.JRadioButton();
        interNormTypeRadioButton_mean = new javax.swing.JRadioButton();
        interNormTypeRadioButton_quantile = new javax.swing.JRadioButton();
        mergeSlidesCheckBox = new javax.swing.JCheckBox();
        replicateMergeMethodComboBox = new javax.swing.JComboBox();
        replicateMergeMethodLabel = new javax.swing.JLabel();
        bgCorrectionMethodComboBox = new javax.swing.JComboBox();
        bgCorrectionMethodLabel = new javax.swing.JLabel();
        outputTypeCheckBox_splitter = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        dyeSwapPanel = new javax.swing.JPanel();
        stop_Button = new javax.swing.JButton();
        peakPanel = new javax.swing.JPanel();
        peak_inputFileLabel = new javax.swing.JLabel();
        peak_outputFileLabel = new javax.swing.JLabel();
        peak_distanceLabel = new javax.swing.JLabel();
        peak_bindingPeakLabel = new javax.swing.JLabel();
        peak_bindingExtendLabel = new javax.swing.JLabel();
        peak_pValuePeakLabel = new javax.swing.JLabel();
        peak_pValueExtendLabel = new javax.swing.JLabel();
        peak_inputFileTextField = new javax.swing.JTextField();
        peak_outputFileTextField = new javax.swing.JTextField();
        peak_distanceTextField = new javax.swing.JTextField();
        peak_bindingPeakTextField = new javax.swing.JTextField();
        peak_bindingExtendTextField = new javax.swing.JTextField();
        peak_pValuePeakTextField = new javax.swing.JTextField();
        peak_pValueExtendTextField = new javax.swing.JTextField();
        peak_inputBrowserButton = new javax.swing.JButton();
        peak_outputBrowserButton1 = new javax.swing.JButton();
        peak_runButton = new javax.swing.JButton();
        peak_bindingCheckBox = new javax.swing.JCheckBox();
        peak_pValueCheckBox = new javax.swing.JCheckBox();
        peak_bindingPeakComboBox = new javax.swing.JComboBox();
        peak_bindingExtendComboBox = new javax.swing.JComboBox();
        area_gffOptionComboBox = new javax.swing.JComboBox();
        area_regionIDColumnLabel = new javax.swing.JLabel();
        area_gffOptionLabel = new javax.swing.JLabel();
        area_regionIDColumnTextField = new javax.swing.JTextField();
        peakPercentileLabel = new javax.swing.JLabel();
        peakPercentileTextField = new javax.swing.JTextField();
        ringoWindowSizeLabel = new javax.swing.JLabel();
        ringoSpinner = new javax.swing.JSpinner();
        ringoCheckBox = new javax.swing.JCheckBox();
        percentileCheckbox = new javax.swing.JCheckBox();
        percentileLabel = new javax.swing.JLabel();
        percentileTextField = new javax.swing.JTextField();
        extendPercentileLabel = new javax.swing.JLabel();
        extendPercentileTextField = new javax.swing.JTextField();
        qcrPanel = new javax.swing.JPanel();
        resultsPanel = new javax.swing.JPanel();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsComboBox = new javax.swing.JComboBox();
        igbPanel = new javax.swing.JPanel();
        browserPanel = new javax.swing.JPanel();
        annotationPanel = new javax.swing.JPanel();
        consoleLabel = new javax.swing.JLabel();
        consoleScrollPane = new javax.swing.JScrollPane();
        analysisProgressBar = new javax.swing.JProgressBar();
        fileListScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        menuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new javax.swing.JMenu();
        loadBarMenuItem1 = new javax.swing.JMenuItem();
        unloadBarMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        updateMenu = new javax.swing.JMenu();
        updateLibrariesMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        startWizardMenuItem = new javax.swing.JMenuItem();
        helpMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        loadPopupMenuItem.setText("Add File");
        loadPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPopupMenuItemActionPerformed1(evt);
            }
        });
        fileListPopupMenu.add(loadPopupMenuItem);

        unloadPopupMenuItem.setText("Remove File");
        unloadPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unloadPopupMenuItemActionPerformed(evt);
            }
        });
        fileListPopupMenu.add(unloadPopupMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CoCAS - ChIP on Chip Analysis Suite");
        setResizable(false);

        tabbedPane.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        tabbedPane.setPreferredSize(new java.awt.Dimension(400, 300));

        outputTypeLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputTypeLabel.setText("Output Type");
        outputTypeLabel.setEnabled(false);

        outputTypeCheckBox_gff.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputTypeCheckBox_gff.setText("GFF");
        outputTypeCheckBox_gff.setToolTipText("<html>General Feature Format, use for UCSC/Ensembl.<br>\nSee <a href=\" http://www.sanger.ac.uk/Software/formats/GFF\"> http://www.sanger.ac.uk/Software/formats/GFF</a> for details</html>");
        outputTypeCheckBox_gff.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputTypeCheckBox_gff.setEnabled(false);
        outputTypeCheckBox_gff.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputTypeCheckBox_gff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeCheckBox_gffActionPerformed(evt);
            }
        });

        outputTypeCheckBox_sgr.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputTypeCheckBox_sgr.setText("SGR");
        outputTypeCheckBox_sgr.setToolTipText("<html>S-PLUS Saved Graph. Use for IGB<br>\nSee the <a href=\"http://www.affymetrix.com/support/developer/tools/download_igb.affx\">Affymetrix IGB website</a> for more details</html>");
        outputTypeCheckBox_sgr.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputTypeCheckBox_sgr.setEnabled(false);
        outputTypeCheckBox_sgr.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputTypeCheckBox_sgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeCheckBox_sgrActionPerformed(evt);
            }
        });

        outputTypeCheckBox_bed.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputTypeCheckBox_bed.setText("BED");
        outputTypeCheckBox_bed.setToolTipText("<html>Browser Extensible Data format. Use for UCSC/Ensembl.<br>\nSee <a href=\"http://genome.ucsc.edu/FAQ/FAQformat#format1\">http://genome.ucsc.edu/FAQ/FAQformat#format1</a> for details</html>");
        outputTypeCheckBox_bed.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputTypeCheckBox_bed.setEnabled(false);
        outputTypeCheckBox_bed.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputTypeCheckBox_bed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeCheckBox_bedActionPerformed(evt);
            }
        });

        runButton.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        runButton.setText("Run");
        runButton.setEnabled(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        runLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        runLabel.setText("Process Microarray (s) with selected settings");
        runLabel.setEnabled(false);

        analysisNameLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        analysisNameLabel.setText("Analysis Name");
        analysisNameLabel.setEnabled(false);

        analysisNameTextField.setText("output");
        analysisNameTextField.setToolTipText("A directory will be created  with the analysis name ");
        analysisNameTextField.setEnabled(false);

        intraNormLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        intraNormLabel.setText("Intra Normalization Type");
        intraNormLabel.setEnabled(false);

        intraNormTypeRadioButton_median.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        intraNormTypeRadioButton_median.setText("Median");
        intraNormTypeRadioButton_median.setToolTipText("<html>Global normalization assumes that the red and green intensities are related by a constant factor,<br> i.e. R = kG, and the center of the distribution of log ratios is shifted to zero<br><br>log2R/G -> log2R/G – c = log2R/(kG)<br><br> where c = log2k is the median. See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/11473024\">Zien A, Aigner T, Zimmer R, Lengauer T. <br>Centralization: a new method for the normalization of gene expression data. Bioinformatics. 2001;<br>17 Suppl 1:S323-31.</a> for details</html>");
        intraNormTypeRadioButton_median.setEnabled(false);
        intraNormTypeRadioButton_median.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intraNormTypeRadioButton_medianActionPerformed(evt);
            }
        });

        intraNormTypeRadioButton_loess.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        intraNormTypeRadioButton_loess.setText("Loess");
        intraNormTypeRadioButton_loess.setToolTipText("<html>Use Lowess if you expect little overall enrichment<br>\nLowess (Locally weighted scatter plot smoothing) intensity-dependent normalization performs<br>\na fit of the data by subtracting a linear regression curve.<br><br>\n\nlog2R/G -> log2R/G – c(A) = log2R/[k(A)G] <br><br>\n\nwhere c(A) is the lowess fit to the MA-plot.\nSee <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/11473024\">Yang YH, Dudoit S, Luu P, Lin DM,<br>\nPeng V, Ngai J, Speed TP. Normalization for cDNA microarray data: a robust composite method<br>\naddressing single and multiple slide systematic variation. Nucleic Acids Res. 2002 Feb 15;30(4):e15.<br></a> for details</html>");
        intraNormTypeRadioButton_loess.setEnabled(false);
        intraNormTypeRadioButton_loess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intraNormTypeRadioButton_loessActionPerformed(evt);
            }
        });

        intraNormTypeRadioButton_vsn.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        intraNormTypeRadioButton_vsn.setText("Variance Stabilization (VSN)");
        intraNormTypeRadioButton_vsn.setToolTipText("<html>The Variance Stabilisation Normalisation (V.S.N.) method builds<br>\nupon the fact that the variance of microarray data depends on the<br>\nsignal intensity and that a transformation can be found after which the<br>\nvariance is approximately constant.<br> \nSee <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/12169536\">Huber W, von Heydebreck A, Sültmann H, Poustka A, Vingron M.<br>\n Variance stabilization applied to microarray data calibration and to the<br>\nquantification of differential expression. Bioinformatics. 2002;18 Suppl 1:<br>\nS96-104.</a> for details");
        intraNormTypeRadioButton_vsn.setEnabled(false);
        intraNormTypeRadioButton_vsn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intraNormTypeRadioButton_vsnActionPerformed(evt);
            }
        });

        intraNormTypeRadioButton_peng.setText("Linear Correction and Weighted Loess");
        intraNormTypeRadioButton_peng.setToolTipText("<html>This normalization uses signal enrichment rotation against intensity according<br>\nto an angle estimated by PCA, then applies a weighted loess normalization.<br>\nSee <a href=\\\"http://www.ncbi.nlm.nih.gov/pubmed/17592629\\\">Peng S, Alekseyenko AA, Larschan E, Kuroda MI, Park PJ.<br>\nNormalization and experimental design for ChIP-chip data. BMC Bioinformatics.<br>\n 2007 Jun 25;8:219.</a> for details</html>");
        intraNormTypeRadioButton_peng.setEnabled(false);
        intraNormTypeRadioButton_peng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intraNormTypeRadioButton_pengActionPerformed(evt);
            }
        });

        outputDirLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputDirLabel.setText("Output Directory");
        outputDirLabel.setEnabled(false);

        outputDirTextField.setText("output");
        outputDirTextField.setEnabled(false);

        outputDirBrowseButton.setText("Browse");
        outputDirBrowseButton.setEnabled(false);
        outputDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputDirBrowseButtonActionPerformed(evt);
            }
        });

        interNormLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        interNormLabel.setText("Inter Normalization Type");
        interNormLabel.setEnabled(false);

        interNormTypeRadioButton_none.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        interNormTypeRadioButton_none.setText("None");
        interNormTypeRadioButton_none.setEnabled(false);
        interNormTypeRadioButton_none.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interNormTypeRadioButton_noneActionPerformed(evt);
            }
        });

        interNormTypeRadioButton_mean.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        interNormTypeRadioButton_mean.setText("Median");
        interNormTypeRadioButton_mean.setEnabled(false);
        interNormTypeRadioButton_mean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interNormTypeRadioButton_meanActionPerformed(evt);
            }
        });

        interNormTypeRadioButton_quantile.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        interNormTypeRadioButton_quantile.setText("Quantile");
        interNormTypeRadioButton_quantile.setEnabled(false);
        interNormTypeRadioButton_quantile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interNormTypeRadioButton_quantileActionPerformed(evt);
            }
        });

        mergeSlidesCheckBox.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        mergeSlidesCheckBox.setText("Multiple Slide Design Merge");
        mergeSlidesCheckBox.setToolTipText("<html>\n\nUse this if your design comprises two or more slides<br>\n\nThis option causes all slides entered as one experiment<br>\nto be treated as replicates of part of a multi-slide design.<br><br>\n\nExample:<br><br>\n\n251471611301 : chr1-10 Replicate 1 as slide 1<br>\n251471611302 : chr1-10 Replicate 2 as slide 1<br>\n251471711301 : chr11-Y Replicate 1 as slide 2<br>\n251471711302 : chr10-Y Replicate 2 as slide 2\n\n</html>");
        mergeSlidesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mergeSlidesCheckBox.setEnabled(false);
        mergeSlidesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        mergeSlidesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeSlidesCheckBoxActionPerformed(evt);
            }
        });

        replicateMergeMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean", "<html>Roberts <i>et al.</i></html>" }));
        replicateMergeMethodComboBox.setToolTipText("<html>The method used to deal with replicates.<br>\nChoose mean for a simple average of all replicates for each probe,<br>\nor use Roberts <i>et al.</i> for the Rosetta error model.<br>\nSee <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/16522673\">http://www.ncbi.nlm.nih.gov/pubmed/16522673</a> for details</html>");
        replicateMergeMethodComboBox.setEnabled(false);
        replicateMergeMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replicateMergeMethodComboBoxActionPerformed(evt);
            }
        });

        replicateMergeMethodLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        replicateMergeMethodLabel.setText("Replicate Merge Method");
        replicateMergeMethodLabel.setEnabled(false);

        bgCorrectionMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Subtract", "Half", "Minimum", "Moving Minimum", "Edwards", "NormExp", "RMA" }));
        bgCorrectionMethodComboBox.setToolTipText("Choose your background substraction method.\nSustract recommended.");
        bgCorrectionMethodComboBox.setEnabled(false);
        bgCorrectionMethodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgCorrectionMethodComboBoxActionPerformed(evt);
            }
        });

        bgCorrectionMethodLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        bgCorrectionMethodLabel.setText("Background Correction Method");
        bgCorrectionMethodLabel.setEnabled(false);

        outputTypeCheckBox_splitter.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        outputTypeCheckBox_splitter.setText("Splitter");
        outputTypeCheckBox_splitter.setToolTipText("<html>Splitter format, position followed by enrichment score. Use for peak detectionl by Splitter.<br>\nSee <a href=\"http://zlab.bu.edu/yf/anchor/web/splitter.cgi?step=0\">http://zlab.bu.edu/yf/anchor/web/splitter.cgi?step=0</a> for details</html>");
        outputTypeCheckBox_splitter.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputTypeCheckBox_splitter.setEnabled(false);
        outputTypeCheckBox_splitter.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputTypeCheckBox_splitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypeCheckBox_splitterActionPerformed(evt);
            }
        });

        dyeSwapPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dyeSwapPanel.setMaximumSize(new java.awt.Dimension(221, 0));
        dyeSwapPanel.setMinimumSize(new java.awt.Dimension(221, 0));
        dyeSwapPanel.setPreferredSize(new java.awt.Dimension(221, 0));
        jScrollPane1.setViewportView(dyeSwapPanel);

        stop_Button.setText("Stop");
        stop_Button.setEnabled(false);
        stop_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_ButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout settingsPanelLayout = new org.jdesktop.layout.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsPanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(settingsPanelLayout.createSequentialGroup()
                        .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, intraNormTypeRadioButton_median)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, intraNormTypeRadioButton_loess)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, intraNormTypeRadioButton_vsn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, intraNormTypeRadioButton_peng)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, intraNormLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(bgCorrectionMethodLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(interNormLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(interNormTypeRadioButton_none)
                            .add(interNormTypeRadioButton_mean)
                            .add(interNormTypeRadioButton_quantile)
                            .add(replicateMergeMethodLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(30, 30, 30)
                        .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mergeSlidesCheckBox)
                            .add(outputTypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outputTypeCheckBox_bed)
                            .add(outputTypeCheckBox_sgr)
                            .add(outputTypeCheckBox_gff)
                            .add(bgCorrectionMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(replicateMergeMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outputTypeCheckBox_splitter)))
                    .add(settingsPanelLayout.createSequentialGroup()
                        .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, settingsPanelLayout.createSequentialGroup()
                                .add(analysisNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(analysisNameTextField))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, settingsPanelLayout.createSequentialGroup()
                                .add(outputDirLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(outputDirTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(outputDirBrowseButton))
                    .add(settingsPanelLayout.createSequentialGroup()
                        .add(runButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stop_Button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(runLabel)))
                .add(20, 20, 20))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .add(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outputTypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(intraNormLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(intraNormTypeRadioButton_median)
                    .add(outputTypeCheckBox_gff))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(intraNormTypeRadioButton_loess)
                    .add(outputTypeCheckBox_sgr))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(intraNormTypeRadioButton_vsn)
                    .add(outputTypeCheckBox_bed))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(intraNormTypeRadioButton_peng)
                    .add(outputTypeCheckBox_splitter))
                .add(18, 18, 18)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bgCorrectionMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bgCorrectionMethodLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interNormLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .add(mergeSlidesCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interNormTypeRadioButton_none)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interNormTypeRadioButton_mean)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interNormTypeRadioButton_quantile)
                .add(10, 10, 10)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(replicateMergeMethodLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(replicateMergeMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(52, 52, 52)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(analysisNameLabel)
                    .add(analysisNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(outputDirLabel)
                    .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(outputDirTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(outputDirBrowseButton)))
                .add(32, 32, 32)
                .add(settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(runButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(runLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(stop_Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7))
        );

        tabbedPane.addTab("Settings", settingsPanel);

        peakPanel.setEnabled(false);

        peak_inputFileLabel.setText("Input file");

        peak_outputFileLabel.setText("Output file");

        peak_distanceLabel.setText("Distance threshold");

        peak_bindingPeakLabel.setText("Peak threshold");

        peak_bindingExtendLabel.setText("Extend threshold");

        peak_pValuePeakLabel.setText("Peak threshold");
        peak_pValuePeakLabel.setEnabled(false);

        peak_pValueExtendLabel.setText("Extend threshold");
        peak_pValueExtendLabel.setEnabled(false);

        peak_inputFileTextField.setEditable(false);
        peak_inputFileTextField.setToolTipText("Input file to be analysed");

        peak_outputFileTextField.setEditable(false);
        peak_outputFileTextField.setToolTipText("Output file of the program");

        peak_distanceTextField.setText("650");

        peak_bindingPeakTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                peak_bindingPeakTextFieldPropertyChange(evt);
            }
        });
        peak_bindingPeakTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                peak_bindingPeakTextFieldKeyTyped(evt);
            }
        });

        peak_bindingExtendTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                peak_bindingExtendTextFieldKeyTyped(evt);
            }
        });

        peak_pValuePeakTextField.setEnabled(false);

        peak_pValueExtendTextField.setEnabled(false);

        peak_inputBrowserButton.setText("Browse");
        peak_inputBrowserButton.setPreferredSize(new java.awt.Dimension(74, 23));
        peak_inputBrowserButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peak_inputBrowserButtonMouseReleased(evt);
            }
        });

        peak_outputBrowserButton1.setText("Browse");
        peak_outputBrowserButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peak_outputBrowserButton1MouseReleased(evt);
            }
        });

        peak_runButton.setText("Run");
        peak_runButton.setToolTipText("Run the area calculation.");
        peak_runButton.setEnabled(false);
        peak_runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        peak_runButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peak_runButtonMouseReleased(evt);
            }
        });

        peak_bindingCheckBox.setSelected(true);
        peak_bindingCheckBox.setText("Binding ratio threshold");
        peak_bindingCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peak_bindingCheckBoxMouseReleased(evt);
            }
        });

        peak_pValueCheckBox.setText("p-Value threshold");
        peak_pValueCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peak_pValueCheckBoxMouseReleased(evt);
            }
        });

        peak_bindingPeakComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean + 1xSD", "Mean + 2xSD", "Mean + 2.5xSD", "Mean + 3xSD", "Custom" }));
        peak_bindingPeakComboBox.setSelectedIndex(1);
        peak_bindingPeakComboBox.setToolTipText("Use 1x SD for ChIP-seq data");
        peak_bindingPeakComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                peak_bindingPeakComboBoxItemStateChanged(evt);
            }
        });

        peak_bindingExtendComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean + 1xSD", "Mean + 1.5xSD", "Mean + 2xSD", "Custom" }));
        peak_bindingExtendComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                peak_bindingExtendComboBoxItemStateChanged(evt);
            }
        });
        peak_bindingExtendComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peak_bindingExtendComboBoxActionPerformed(evt);
            }
        });

        area_gffOptionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Effective", "Positive", "Negative" }));
        area_gffOptionComboBox.setToolTipText("Area to be displayed in the gff output file");

        area_regionIDColumnLabel.setText("Region column ID");
        area_regionIDColumnLabel.setToolTipText("Region annotation column passed upon area calculation. IGB uses column 10");

        area_gffOptionLabel.setText("Area type");
        area_gffOptionLabel.setToolTipText("Type of area displayed in output file");

        area_regionIDColumnTextField.setColumns(2);
        area_regionIDColumnTextField.setText("8");
        area_regionIDColumnTextField.setToolTipText("Column n° in input file where are region ID");

        peakPercentileLabel.setText("Peak percentile");
        peakPercentileLabel.setEnabled(false);

        peakPercentileTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        peakPercentileTextField.setText("0.999");
        peakPercentileTextField.setEnabled(false);
        peakPercentileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                peakPercentileTextFieldKeyReleased(evt);
            }
        });

        ringoWindowSizeLabel.setText("Window size (probes)");
        ringoWindowSizeLabel.setEnabled(false);

        ringoSpinner.setEnabled(false);
        ringoSpinner.setValue(3);
        ringoSpinner.setVerifyInputWhenFocusTarget(false);

        ringoCheckBox.setText("Ringo thresholds");
        ringoCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ringoCheckBoxMouseReleased(evt);
            }
        });

        percentileCheckbox.setText("Percentile threshold");
        percentileCheckbox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                percentileCheckboxMouseReleased(evt);
            }
        });

        percentileLabel.setText("Percentile");
        percentileLabel.setEnabled(false);

        percentileTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        percentileTextField.setText("0.95");
        percentileTextField.setEnabled(false);
        percentileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                percentileTextFieldKeyReleased(evt);
            }
        });

        extendPercentileLabel.setText("Extend percentile");
        extendPercentileLabel.setEnabled(false);

        extendPercentileTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        extendPercentileTextField.setText("0.99");
        extendPercentileTextField.setEnabled(false);
        extendPercentileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                extendPercentileTextFieldKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout peakPanelLayout = new org.jdesktop.layout.GroupLayout(peakPanel);
        peakPanel.setLayout(peakPanelLayout);
        peakPanelLayout.setHorizontalGroup(
            peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(peakPanelLayout.createSequentialGroup()
                        .add(percentileCheckbox)
                        .addContainerGap())
                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(peakPanelLayout.createSequentialGroup()
                            .add(peak_runButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .add(peakPanelLayout.createSequentialGroup()
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, peakPanelLayout.createSequentialGroup()
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, peakPanelLayout.createSequentialGroup()
                                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(peak_bindingCheckBox)
                                                .add(peakPanelLayout.createSequentialGroup()
                                                    .add(21, 21, 21)
                                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(peak_bindingExtendLabel)
                                                        .add(peak_bindingPeakLabel)
                                                        .add(peak_distanceLabel)))
                                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(ringoCheckBox)
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, peakPanelLayout.createSequentialGroup()
                                                        .add(21, 21, 21)
                                                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                            .add(ringoWindowSizeLabel)
                                                            .add(peakPercentileLabel)
                                                            .add(extendPercentileLabel))
                                                        .add(66, 66, 66))))
                                            .add(15, 15, 15)
                                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                .add(peak_bindingExtendTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                                .add(peak_bindingPeakTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                                .add(peak_distanceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                                .add(org.jdesktop.layout.GroupLayout.LEADING, ringoSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, extendPercentileTextField)
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, peakPercentileTextField))))
                                        .add(peakPanelLayout.createSequentialGroup()
                                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(area_gffOptionLabel)
                                                .add(area_regionIDColumnLabel)
                                                .add(peakPanelLayout.createSequentialGroup()
                                                    .add(22, 22, 22)
                                                    .add(percentileLabel)))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 97, Short.MAX_VALUE)
                                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                .add(area_gffOptionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .add(area_regionIDColumnTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .add(percentileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(peak_bindingExtendComboBox, 0, 0, Short.MAX_VALUE)
                                        .add(peak_bindingPeakComboBox, 0, 91, Short.MAX_VALUE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(peak_pValueExtendLabel)
                                        .add(peak_pValuePeakLabel)
                                        .add(peak_pValueCheckBox)))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, peakPanelLayout.createSequentialGroup()
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(peak_outputFileLabel)
                                        .add(peak_inputFileLabel))
                                    .add(154, 154, 154)
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(peak_inputFileTextField)
                                        .add(peak_outputFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))))
                            .add(18, 18, 18)
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(peak_inputBrowserButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(peak_outputBrowserButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, peak_pValueExtendTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, peak_pValuePeakTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)))
                            .addContainerGap(40, Short.MAX_VALUE)))))
        );
        peakPanelLayout.setVerticalGroup(
            peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_inputFileLabel)
                    .add(peak_inputBrowserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(peak_inputFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_outputFileLabel)
                    .add(peak_outputBrowserButton1)
                    .add(peak_outputFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(peakPanelLayout.createSequentialGroup()
                        .add(peak_bindingCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(peak_bindingPeakLabel)
                            .add(peak_bindingPeakComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(peak_bindingPeakTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(peak_bindingExtendLabel)
                            .add(peak_bindingExtendComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(peak_bindingExtendTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(peak_pValueExtendLabel)
                            .add(peak_pValueExtendTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(peak_distanceLabel)
                            .add(peak_distanceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(32, 32, 32)
                        .add(ringoCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(peakPercentileLabel)
                            .add(peakPercentileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(10, 10, 10)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(extendPercentileLabel)
                            .add(extendPercentileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(ringoWindowSizeLabel)
                            .add(ringoSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(percentileCheckbox)
                        .add(18, 18, 18)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(percentileLabel)
                            .add(percentileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(peakPanelLayout.createSequentialGroup()
                                .add(area_regionIDColumnTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(area_gffOptionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(peakPanelLayout.createSequentialGroup()
                                .add(area_regionIDColumnLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(area_gffOptionLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))))
                    .add(peakPanelLayout.createSequentialGroup()
                        .add(peak_pValueCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(peak_pValuePeakLabel)
                            .add(peak_pValuePeakTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(peak_runButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(19, 19, 19))
        );

        tabbedPane.addTab("Peak detection", peakPanel);

        org.jdesktop.layout.GroupLayout qcrPanelLayout = new org.jdesktop.layout.GroupLayout(qcrPanel);
        qcrPanel.setLayout(qcrPanelLayout);
        qcrPanelLayout.setHorizontalGroup(
            qcrPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 645, Short.MAX_VALUE)
        );
        qcrPanelLayout.setVerticalGroup(
            qcrPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 499, Short.MAX_VALUE)
        );

        tabbedPane.addTab("QC Report", qcrPanel);

        resultsComboBox.setEnabled(false);
        resultsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout resultsPanelLayout = new org.jdesktop.layout.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
            .add(resultsComboBox, 0, 645, Short.MAX_VALUE)
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, resultsPanelLayout.createSequentialGroup()
                .add(resultsComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 470, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPane.addTab("Results", resultsPanel);

        org.jdesktop.layout.GroupLayout browserPanelLayout = new org.jdesktop.layout.GroupLayout(browserPanel);
        browserPanel.setLayout(browserPanelLayout);
        browserPanelLayout.setHorizontalGroup(
            browserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 645, Short.MAX_VALUE)
        );
        browserPanelLayout.setVerticalGroup(
            browserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 378, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout annotationPanelLayout = new org.jdesktop.layout.GroupLayout(annotationPanel);
        annotationPanel.setLayout(annotationPanelLayout);
        annotationPanelLayout.setHorizontalGroup(
            annotationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 645, Short.MAX_VALUE)
        );
        annotationPanelLayout.setVerticalGroup(
            annotationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 115, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout igbPanelLayout = new org.jdesktop.layout.GroupLayout(igbPanel);
        igbPanel.setLayout(igbPanelLayout);
        igbPanelLayout.setHorizontalGroup(
            igbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(annotationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(browserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        igbPanelLayout.setVerticalGroup(
            igbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(igbPanelLayout.createSequentialGroup()
                .add(browserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(annotationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("IGB", igbPanel);

        consoleLabel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        consoleLabel.setText("Comments");

        consoleScrollPane.setAutoscrolls(true);

        fileListScrollPane.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                fileListScrollPaneVetoableChange(evt);
            }
        });

        fileListScrollPane.setViewportView(fileList);

        fileMenu1.setText("File");
        fileMenu1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));

        loadBarMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        loadBarMenuItem1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        loadBarMenuItem1.setText("Load");
        loadBarMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBarMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(loadBarMenuItem1);

        unloadBarMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        unloadBarMenuItem.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        unloadBarMenuItem.setText("Unload");
        unloadBarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unloadBarMenuItemActionPerformed(evt);
            }
        });
        fileMenu1.add(unloadBarMenuItem);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(jMenuItem1);

        menuBar1.add(fileMenu1);

        updateMenu.setText("Update");
        updateMenu.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));

        updateLibrariesMenuItem.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        updateLibrariesMenuItem.setText("Libraries");
        updateLibrariesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateLibrariesMenuItemActionPerformed(evt);
            }
        });
        updateMenu.add(updateLibrariesMenuItem);

        menuBar1.add(updateMenu);

        helpMenu.setText("?");
        helpMenu.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));

        startWizardMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        startWizardMenuItem.setText("Start Wizard");
        startWizardMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startWizardMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(startWizardMenuItem);

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        helpMenuItem.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        aboutMenuItem.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar1.add(helpMenu);

        setJMenuBar(menuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(consoleScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1031, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(consoleLabel))
                    .add(fileListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 365, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 650, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(analysisProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 228, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(fileListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 502, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 527, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(consoleLabel)
                    .add(analysisProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(consoleScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void startAnalysis()
    {
       consoleTextArea.setText("");
       analysisProgressBar.setVisible(true);
       analysisProgressBar.setIndeterminate(true);
       this.update(this.getGraphics());
        
        //directoryName=directoryName.replaceAll("\\\\","//");
        directoryName=directoryName.replace('\\','/');
        directoryName=directoryName.replaceAll(" ","\\ ");
        System.out.println(directoryName);
        
        
        //re.eval("setwd(\""+directoryName+"\")");
        
        
        System.out.println(re.eval("getwd()"));
        System.out.println(re.eval("dir()"));
        
        //System.out.println("source(\""+directoryName+"//cocasV2-4.R\")");
        //System.out.println(re.eval("source(\""+directoryName+"//cocasV2-4.R\")"));
        
        //System.out.println(re.eval("source(\""+System.getProperty("java.class.path")+"//cocasV2.1.R\")"));
        
        //System.out.println(re.eval("chipAnalyse(rep1 =\""+fileNamesList[0]+"\", rep2= FALSE, swap1 = FALSE, swap2 = FALSE, name =\"output\")"));
        //consoleTextArea.setText(console.getRText());
        if(!useWizard)
        {
            title = analysisNameTextField.getText();
        outputDirectory+="//"+analysisNameTextField.getText();
        expName = directoryName+"//"+analysisNameTextField.getText();
        if(expName.equals(null)||expName.equals("")||expName.matches("[\"#?!§%^¨$£]")) {
            expName=directoryName+"//"+"output";
            title="output";
        }
        }
        else
        {
            outputDirectory.replace("\\", "/");
        }
        System.out.println(fileNamesList.get(0));
        
        
        
        thread = new Thread() {
            public void run() {
                
                //String tmpdir = directoryName+="//"+Integer.toString((int)Math.abs(Math.random()*100000000));
                
                int max=Integer.MIN_VALUE;
                if(!useWizard)
                {
                
                
                outputDirectory=outputDirTextField.getText()+"//"+analysisNameTextField.getText();
                int tmp=0;
                for(int i =0;i<fileNamesList.size();i++) {
                    
                    tmp=Integer.parseInt(slideNumberTextFieldList.get(i).getText());
                    slideNumberList.set(i,tmp);
                    //if((slideNumberList.set(i,Integer.parseInt(slideNumberTextFieldList.get(i).getText()))>max))
                    if(slideNumberList.get(i)>max)
                    
                    {
                        max=slideNumberList.get(i);
                    }
                }
               
                }
                else
                {
                    max = wizard.getNumExps();
                }
                re.eval("input_files <- list()");
                
                //directory delete function
                cocas.DeleteDir.deleteDirectory(new File(outputDirectory));
                new File(outputDirectory).mkdir();
                
                String fileNoSwap="c(";
                String fileSwap="c(";
                System.out.println("max = "+max);
                for(int j=1;j<=max;j++)
                {
                    
                    fileNoSwap="c(";
                    fileSwap="c(";
                    
                    for(int i =0;i<fileNamesList.size();i++) 
                    {
                    
                        if(slideNumberList.get(i)==j)
                        {
                            if(dyeSwapList.get(i)==false) {
                                if (fileNoSwap.equals("c(")) {
                                    fileNoSwap+="\""+fileNamesList.get(i)+"\"";
                                } else {
                                    fileNoSwap+=", \""+fileNamesList.get(i)+"\"";

                                }
                            } 
                            else {
                                if (fileSwap.equals("c(")) {
                                    fileSwap+="\""+fileNamesList.get(i)+"\"";
                                } else {
                                    fileSwap+=", \""+fileNamesList.get(i)+"\"";

                                }
                            }
                        }
                    }
                    fileNoSwap+=")";
                    fileSwap+=")";
                    System.out.println(re.eval("input_files[["+j+"]]<-list(swap="+fileSwap+",noswap="+fileNoSwap+")"));
                    System.out.println("input_files[["+j+"]]<-list(swap="+fileSwap+",noswap="+fileNoSwap+")");
                
                }
                //System.out.println("main(NoSwap="+fileNoSwap+", Swap="+fileSwap+",directory= \""+expName+"\", normalization=\""+normType+"\", title=\""+title+"\")");
                //System.out.println(re.eval("main(NoSwap="+fileNoSwap+", Swap="+fileSwap+",directory= \""+expName+"\", normalization=\""+normType+"\", title=\""+title+"\")"));
                System.out.println("main(input_files,directory= \""+outputDirectory+"\", normalizationIntra=\""+normType+"\", bc.method=\""+bcMethod+"\", normalizationInter=\""+interNormType+"\", mergeSlides=\""+Boolean.toString(mergeSlides)+"\", mergeReplicates.method=\""+mergeReplicatesMethod+"\", title=\""+title+"\")");
                System.out.println(re.eval("main(input_files,directory= \""+outputDirectory+"\", normalizationIntra=\""+normType+"\", bc.method=\""+bcMethod+"\", normalizationInter=\""+interNormType+"\", mergeSlides=\""+Boolean.toString(mergeSlides)+"\", mergeReplicates.method=\""+mergeReplicatesMethod+"\", title=\""+title+"\")"));
                //jTabbedPane1.setEnabledAt(3,true);
                //jTabbedPane1.setEnabledAt(5,true);
                System.out.println(outputDirectory);
                javatest.QCRPanel panel = new javatest.QCRPanel(outputDirectory+"//");
                panel.setName("QCR Report");
                tabbedPane.setComponentAt(2, panel);
                tabbedPane.setEnabledAt(2, true);
                peak_inputFileTextField.setText(outputDirectory+"\\\\"+title+".gff");
                peak_outputFileTextField.setText(outputDirectory+"\\\\"+title+"_peak.area.gff");
                
                    //should make this as a method, reused
                    String[] temp=new File(outputDirectory).list();
                    outputFilesList=new ArrayList<String>(0);
                    
                    for(int i=0;i<temp.length;i++)
                    {
                      if(temp[i].endsWith(".gff"))  
                      {
                          outputFilesList.add(temp[i]);
                      }
                      
                      
                    }
                for(int i=0;i<outputFilesList.size();i++)
                {
                    if(outputFilesList.get(i).endsWith(".gff"))
                    {
                        consoleTextArea.append("Peak detection : Slide "+(i+1)+" ...\n");
                        int n = 0;           // number input values
                        double sum  = 0.0;   // sum of input values
                        double sum2 = 0.0;   // sum of squares of input values
                        double x = 0.0;
                        String ligne;
                        BufferedReader bReader = null;
                        // read data and compute statistics
                        try 
                        {
                            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(outputDirectory+"//"+outputFilesList.get(i))));
                            ligne = bReader.readLine();
                            while (ligne != null) 
                            {
                                x = Double.parseDouble(ligne.split("\t")[5]);
                                n++;
                                sum += x;
                                sum2 += x*x;
                                ligne = bReader.readLine();
                            }
                            bReader.close();
                            avg = sum / n;
                            variance = sum2/n - avg*avg;
                            stddev = Math.sqrt(variance);


                        consoleTextArea.append("avg= "+avg+" variance= "+variance+" stddev= "+stddev+"\n");
                        int nPeak = 0;
                        
                        if(peak_bindingCheckBox.isSelected())
                        {
                        peak_distance = Integer.parseInt(peak_distanceTextField.getText());
                        switch(peak_bindingPeakComboBox.getSelectedIndex())
                        {
                            case 0 : peak_threshold = avg+1*stddev; break;
                            case 1 : peak_threshold = avg+2*stddev; break;
                            case 2 : peak_threshold = avg+2.5*stddev; break;
                            case 3 : peak_threshold = avg+3*stddev; break;
                            case 4 : peak_threshold=Double.parseDouble(peak_bindingPeakTextField.getText()); break;
                        }
                        
                        switch(peak_bindingExtendComboBox.getSelectedIndex())
                        {
                            case 0 : extend_threshold = avg+1*stddev; break;
                            case 1 : extend_threshold = avg+1.5*stddev; break;
                            case 2 : extend_threshold = avg+2*stddev; break;
                            case 3 : extend_threshold=Double.parseDouble(peak_bindingExtendTextField.getText()); break;
                        }
                        
                        }
                        if(ringoCheckBox.isSelected())
                        {
                            //System.out.println("ringo");
                            ringo_percentile=Double.parseDouble(peakPercentileTextField.getText());
                            ringo_window = (Integer)ringoSpinner.getModel().getValue();
                            
                            
                            peak_threshold = ringoStarr(new File(outputDirectory+"//"+outputFilesList.get(i)), ringo_percentile, ringo_window);
                            //peak_threshold = calculatePercentile(new File(outputDirectory+"//"+outputFilesList.get(i)), ringo_percentile);
                            
                            extend_threshold=peak_threshold;
                            //System.out.println("starr");
                            System.out.println(extend_threshold);
                        }
                        
                        if(percentileCheckbox.isSelected())
                        {
                            peak_threshold=calculatePercentile(new File(outputDirectory+"//"+outputFilesList.get(i)), Double.parseDouble(percentileTextField.getText()));
                            extend_threshold=peak_threshold;
                        }
                        
                        
                        peak_bindingPeakTextField.setText(Double.toString(peak_threshold));
                        peak_bindingExtendTextField.setText(Double.toString(extend_threshold));
                        
                        if(usePval)
                        {
                            peak_threshold_pval=Double.parseDouble(peak_pValuePeakTextField.getText());
                            extend_threshold_pval=Double.parseDouble(peak_pValueExtendTextField.getText());
                        }
                        
                        
                        //nPeak = GFFPeak.GPS(outputDirectory+"//"+outputFilesList.get(i), outputDirectory+"//"+outputFilesList.get(i).substring(0,outputFilesList.get(i).indexOf(".gff"))+"_peak.gff", 650, avg+2*stddev, Double.POSITIVE_INFINITY, avg+stddev, Double.POSITIVE_INFINITY, "EFFECTIVE");
                        nPeak = GFFPeak.GPS(outputDirectory+"//"+outputFilesList.get(i), outputDirectory+"//"+outputFilesList.get(i).substring(0,outputFilesList.get(i).indexOf(".gff"))+"_peak.gff", peak_distance, peak_threshold, peak_threshold_pval, extend_threshold, extend_threshold_pval, GFFRegion.EFFECTIVE);
                        consoleTextArea.append(""+nPeak+" peaks found\n");
                        consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
                        consoleTextArea.update(consoleTextArea.getGraphics());

                    } 
                    catch (Exception exception){
                        JOptionPane.showMessageDialog(panel,exception.toString());
                        analysisProgressBar.setVisible(false);
                        stop_Button.setEnabled(false);
                    }
                    }
                }
                try {










                    //System.out.println(re.eval("main("+fileNoSwap+", "+fileSwap+", \""+expName+"\",\""+normType+"\")"));

                    /*
                    if(fileNamesList.length==1)
                    {
                    System.out.println("chipAnalyse(rep1 =\""+fileNamesList[0]+"\", rep2= FALSE, swap1 = "+Boolean.toString(dyeSwapList[0]).toUpperCase()+", swap2 = FALSE, name =\""+expName+"\")");
                    re.eval("chipAnalyse(rep1 =\""+fileNamesList[0]+"\", rep2= FALSE, swap1 = "+Boolean.toString(dyeSwapList[0]).toUpperCase()+", swap2 = FALSE, name =\""+expName+"\")");
                    //org.rosuda.JRI.RVector fileNoSwap = new org.rosuda.JRI.RVector();
                    //org.rosuda.JRI.RVector fileSwap = new org.rosuda.JRI.RVector();
                    }
                    if(fileNamesList.length==2)
                    {
                    System.out.println("chipAnalyse(rep1 =\""+fileNamesList[0]+"\",rep2=\""+fileNamesList[1]+"\", swap1 = "+Boolean.toString(dyeSwapList[0]).toUpperCase()+", swap2 = "+Boolean.toString(dyeSwapList[1]).toUpperCase()+", name =\""+expName+"\")");
                    re.eval("chipAnalyse(rep1 =\""+fileNamesList[0]+"\",rep2=\""+fileNamesList[1]+"\", swap1 = "+Boolean.toString(dyeSwapList[0]).toUpperCase()+", swap2 = "+Boolean.toString(dyeSwapList[1]).toUpperCase()+", name =\""+expName+"\")");
                    }
                    //*/


                    //jTabbedPane1.add(panel);
                    //Viewer viewer = new Viewer(directoryName+"//"+expName+".png",jPanel9.getWidth(),jPanel9.getHeight());
                    //viewer.setSize(jPanel9.getSize());
                    //jPanel9.add(viewer);


                    displayResults();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(panel,ex.toString());
                        analysisProgressBar.setVisible(false);
                        stop_Button.setEnabled(false);
                } catch (IOException ex) {
                    Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(panel,ex.toString());
                        analysisProgressBar.setVisible(false);
                        stop_Button.setEnabled(false);
                }
                    
                    
                    if(outputTypeCheckBox_bed.isSelected())
                    {
                        for(int i=0;i<outputFilesList.size();i++)
                        {
                            if(outputFilesList.get(i).endsWith(".gff"))
                            {
                                
                                gff2bed = new GFF2BED(new File(outputDirectory+"//"+outputFilesList.get(i)),new File(outputDirectory+"//"+outputFilesList.get(i).split("\\.")[0]+".bed"));
                                gff2bed.convert();
                            }
                            
                        }
                        
                    }
                    
                    if(outputTypeCheckBox_splitter.isSelected())
                    {
                        for(int i=0;i<outputFilesList.size();i++)
                        {
                            //trick to only select the gff that hasn't undergone peak detection
                            if((outputFilesList.get(i).endsWith(".gff")&&(!outputFilesList.get(i).contains("_peak"))))
                            {
                                System.out.println(outputFilesList.get(i));
                                System.out.println(outputFilesList.get(i).split("\\."));
                                System.out.println(outputFilesList.get(i).split("\\.").length);
                                System.out.println(outputFilesList.get(i).split("\\.")[0]);
                                gff2splitter = new GFF2Splitter(new File(outputDirectory+"//"+outputFilesList.get(i).split("\\.")[0]+".gff"),new File(outputDirectory+"//"+outputFilesList.get(i).split("\\.")[0]+".splitter"));
                                gff2splitter.convert();
                            }
                            
                        }
                        
                    }
                    //jTabbedPane1.setEnabledAt(5, true);
                    
                    
                    
                    /*
                    SeqMapView gview = SeqMapView.makeSeqMapView(true, false, true);
                    gmodel = SingletonGenometryModel.getGenometryModel();
                    
                    //Das2GenomeLoader dasloader = new Das2GenomeLoader(gview, new JDialog(this, "Genome Chooser", true));
                    //jPanel3.add(dasloader);
                    com.affymetrix.igb.view.AnnotBrowserView browser = new com.affymetrix.igb.view.AnnotBrowserView();
                    //jPanel10.setEnabled(true);
                    //browser.add(gview);
                    //com.affymetrix.genoviz.datamodel.AnnotatedSequence bioseq = new com.affymetrix.genoviz.datamodel.AnnotatedSequence();
                    //ArrayList plugin_list = new ArrayList(16);
                    gview.setSize(jPanel1.getSize());
                    jPanel1.add(gview);
                    //AnnotatedSeqGroup group = gmodel.getSelectedSeqGroup();
                    AnnotatedSeqGroup group = new AnnotatedSeqGroup("hg18");
                    //group.addSeq();
                    com.affymetrix.genometryImpl.parsers.GFFParser parser = new com.affymetrix.genometryImpl.parsers.GFFParser();
                    //group.addSeq()
                    
                    
                    browser.setSize(jPanel2.getSize());
                    jPanel2.add(browser);
                    
                    
                    SeqSymmetry[] symlist = new SeqSymmetry[1];
                    
                    
                    //java.util.List sym;
                    try {
                        
                        parser.parse(new FileInputStream(expName+"//output.peak.area.gff"), "output", group, false);
                        
                    } catch (IOException ioe) {
                        System.out.println("file not found");
                    }
                    //gview.makeCytobandGlyph();
                    gview.setAnnotatedSeq(group.getSeq(0));
                    Das2ServerInfo hg18 = new Das2ServerInfo("http://netaffxdas.affymetrix.com/das2/genome/", "H_sapiens_Mar_2006", true);
                    Das2Source source = new Das2Source(hg18, new URI("http://netaffxdas.affymetrix.com/das2/genome/"), "H_sapiens_Mar_2006", "http://netaffxdas.affymetrix.com/das2/genome/H_sapiens_Apr_2006", "H_sapiens", "H_sapiens_Apr_2006");
                    //            Das2VersionedSource version = new Das2VersionedSource(source, new URI("http://netaffxdas.affymetrix.com/das2/genome/H_sapiens_Apr_2006"), "H_sapiens", "H_sapiens_Apr_2006", "http://netaffxdas.affymetrix.com/das2/genome/H_sapiens_Apr_2006", true);
                    //new Das2VersionedSource
                    //gview.setAnnotatedSeq(group.getSeq());
                    String quick_load_url = QuickLoadView2.getQuickLoadUrl();
                    //Das2GenomeLoader.showGenomeChooserDialog(gview);
                    //Das2VersionedSource version = new Das2VersionedSource;
                    //    String quick_load_url = "file:/C:/data/quickload/";
                    SynonymLookup dlookup = SynonymLookup.getDefaultLookup();
                    LocalUrlCacher.loadSynonyms(dlookup, quick_load_url + "synonyms.txt");
                    processDasServersList(quick_load_url);
                    processDas2ServersList(quick_load_url);
                    WebLink.autoLoad();
                    
                    // bootstrap bookmark from Preferences for last genome / sequence / region
                    ViewPersistenceUtils.restoreLastView(gview);
                    //com.affymetrix.igb.view.Das2LoadView3 view = new com.affymetrix.igb.view.Das2LoadView3();
                    
                    //DasFeaturesAction2 load_das_action = new DasFeaturesAction2(gview);
                    //load_das_action.actionPerformed(new java.awt.event.ActionEvent(load_das_action,1,""));
                    //load_das_action.composeDasFeatRequest();
                    //try
                    //{
                    //FileReader fr = new FileReader("F://Pierre//TwoColor//output.gff");
                    //InputStream is = new FileInputStream("F://Pierre//TwoColor//output.gff");
                    //gffparser.parse(is, arg1, true);
                    //gview.toggleAutoScroll();
                    //gview.toggleHairlineLabel();
                    //gview.show();
                    //gview = igb.getMapView();
                    //gview.setVisible(true);
                    //jPanel10.add(gview);
                    //}
                    //catch(IOException ioe)
                    //{
                    //}
                } catch (URISyntaxException ex) {
                    Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
                     System.out.println(ex.toString());
                     * 
                     * 
                     * 
                     * 
                     * 
                */
                
                    analysisProgressBar.setVisible(false);
                    stop_Button.setEnabled(false);
                }
                
                
            
                    
            
            
        } ;
        
        //thread = new EvalRExpression(re,"chipAnalyse(rep1 =\""+fileNamesList[0]+"\", rep2= FALSE, swap1 = FALSE, swap2 = FALSE, name =\"output\")");
        //try
        //{
        thread.start();
        
        
        //while(thread.isAlive())
        //{
        //consoleTextArea.update(consoleTextArea.getGraphics());
        //jScrollPane2.update(jScrollPane2.getGraphics());
        
        //}
        //thread.join();
        
        //}
        //catch(InterruptedException ie)
        //{
        
        //}
        //this.update(this.getGraphics());
    }
    
    public void displayResults() throws FileNotFoundException, IOException
    {
        int first_peak_gff_index=0;
                    for(int i = 0;i<outputFilesList.size();i++)
                    {
                        if(outputFilesList.get(i).contains("_peak.area.gff"))
                        {
                            first_peak_gff_index=i;
                            break;
                        }
                    }
                    FileReader fr = new FileReader(outputDirectory+"//"+outputFilesList.get(first_peak_gff_index));
                    BufferedReader reader = new BufferedReader(fr);
                    Vector data = new Vector(0);
                    boolean endOfFile=false;
                    while(!endOfFile) {
                        String tmp = reader.readLine();
                        if(tmp == null) {
                            endOfFile=true;
                        } else {
                            Vector linedata = new Vector(edu.emory.mathcs.backport.java.util.Arrays.asList(tmp.split("\t")));
                            
                    
                    
                    DecimalFormat format=new DecimalFormat("00000.00");
                    linedata.set(5,format.format(Double.parseDouble(tmp.split("\t")[5])));
                    //linedata.set(5,Double.parseDouble(tmp.split("\t")[5]));
                    
                    
                            data.addElement(linedata);
                        }
                    }
                    //String[] headers ={"Chromosome","Source","Feature","Start","End","Score","Strand"};
                    Vector headers = new Vector() {
                        {
                            add("Chromosome");
                            add("Source");
                            add("Feature");
                            add("Start");
                            add("End");
                            add("Score");
                            add("Strand");
                            add("P-Value");
                            add("Attributes");
                            add("Comments");
                        }
                    };
                    
                    JXTable table = new JXTable(data,headers);
                    table.setLocale(Locale.getDefault());
                    
                    //jScrollPane3 = new JScrollPane(table);
                    //jPanel8.add(jScrollPane3);
                    resultsScrollPane.setViewportView(table);
                    table.setFillsViewportHeight(true);
                    table.setSortable(true);
                    
                    //table.resize(jScrollPane3.getSize());
                    //jScrollPane3.add(table);
                    //table.show();
                    
                    
                    tabbedPane.setEnabledAt(3,true);
                    
                    
                    int selectedIndex=0;
                    String[] temp2=new File(outputDirectory).list();
                    outputFilesList=new ArrayList<String>(0);
                    
                    for(int i=0;i<temp2.length;i++)
                    {
                      if(temp2[i].endsWith(".gff"))  
                      {
                          outputFilesList.add(temp2[i]);
                      }
                    
                    }
                    for(int i=0;i<outputFilesList.size();i++)
                    {
                        if(outputFilesList.get(i).contains("_peak.area.gff"))
                      {
                          selectedIndex=i;
                          break;
                      }
                    }
                    String[] tmp3= new String[outputFilesList.size()];
                    for(int i=0;i<outputFilesList.size();i++)
                    {
                        tmp3[i]=outputFilesList.get(i);
                    }
                    
                    resultsComboBox.setModel(new DefaultComboBoxModel(tmp3));
                    resultsComboBox.setSelectedIndex(selectedIndex);
                    resultsComboBox.setEnabled(true);
    }
    
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
       stop_Button.setEnabled(true);
        this.startAnalysis();
}//GEN-LAST:event_runButtonActionPerformed

    private void outputTypeCheckBox_bedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputTypeCheckBox_bedActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_outputTypeCheckBox_bedActionPerformed

    private void outputTypeCheckBox_sgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputTypeCheckBox_sgrActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_outputTypeCheckBox_sgrActionPerformed

    private void outputTypeCheckBox_gffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputTypeCheckBox_gffActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_outputTypeCheckBox_gffActionPerformed

    private void intraNormTypeRadioButton_vsnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intraNormTypeRadioButton_vsnActionPerformed
        // TODO add your handling code here:
        normType="vsn";
}//GEN-LAST:event_intraNormTypeRadioButton_vsnActionPerformed

    private void intraNormTypeRadioButton_loessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intraNormTypeRadioButton_loessActionPerformed
        // TODO add your handling code here:
        normType="loess";
}//GEN-LAST:event_intraNormTypeRadioButton_loessActionPerformed

    private void intraNormTypeRadioButton_medianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intraNormTypeRadioButton_medianActionPerformed
        // TODO add your handling code here:
        normType="median";
}//GEN-LAST:event_intraNormTypeRadioButton_medianActionPerformed

    private void peak_bindingPeakTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_peak_bindingPeakTextFieldPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_peak_bindingPeakTextFieldPropertyChange

    private void peak_bindingPeakTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_peak_bindingPeakTextFieldKeyTyped
        // TODO add your handling code here:
        	peak_bindingPeakComboBox.setSelectedIndex(3);
    }//GEN-LAST:event_peak_bindingPeakTextFieldKeyTyped

    private void peak_bindingExtendTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_peak_bindingExtendTextFieldKeyTyped
        // TODO add your handling code here:
        	peak_bindingExtendComboBox.setSelectedIndex(3);
    }//GEN-LAST:event_peak_bindingExtendTextFieldKeyTyped

    private void peak_inputBrowserButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peak_inputBrowserButtonMouseReleased
        // TODO add your handling code here:
        
        int returnVal = fileChooser.showOpenDialog(peakPanel);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
           peak_runButton.setEnabled(true);
	   File inputFile = fileChooser.getSelectedFile();
	   peak_inputFileTextField.setText(inputFile.getAbsolutePath());
	   if( inputFile.getAbsolutePath().endsWith(".gff") ){
		peak_outputFileTextField.setText( inputFile.getPath().replace(".gff","_peak.gff") );
	   }
	   else{
	       peak_outputFileTextField.setText( inputFile.getPath()+"_peak.gff" );
	   }
	   if(inputFile.exists()){
		int n = 0;           // number input values
		double sum  = 0.0;   // sum of input values
		double sum2 = 0.0;   // sum of squares of input values
		double x = 0.0;
		String ligne;
		BufferedReader bReader = null;
		
		// read data and compute statistics
		try {
		    bReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile.getAbsolutePath())));
		    ligne = bReader.readLine();
		    while (ligne != null) {
			x = Double.parseDouble(ligne.split("\t")[5]);
			n++;
			sum += x;
			sum2 += x*x;
			ligne = bReader.readLine();
		    }
		    bReader.close();
		} 
		catch (Exception exception) {
                System.out.println(exception.toString());}
		
		
		avg = sum / n;
		//variance = (n*sum2 - sum*sum) / (n*(n-1));
		variance = sum2/n - avg*avg;
		stddev = Math.sqrt(variance);
		peak_bindingPeakTextField.setText(""+(avg+2*stddev));
		peak_bindingExtendTextField.setText(""+(avg+1*stddev));
		System.out.println("avg= "+avg+" variance= "+variance+" stddev= "+stddev);

	   }
	}
    }//GEN-LAST:event_peak_inputBrowserButtonMouseReleased

    private void peak_outputBrowserButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peak_outputBrowserButton1MouseReleased
        // TODO add your handling code here:
        int returnVal = fileChooser.showDialog(peakPanel,"Ok");
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	   peak_outputFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
           peak_runButton.setEnabled(true);
	}
    }//GEN-LAST:event_peak_outputBrowserButton1MouseReleased

    private void peak_runButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peak_runButtonMouseReleased
        // TODO add your handling code here:
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
	
	
	File inputFile = new File(peak_inputFileTextField.getText());
	File outputFile = new File(peak_outputFileTextField.getText());
	int distance;
	Double pValueExtendThreshold;
	Double pValuePeakThreshold;
	Double bindingPeakThreshold;
	Double bindingExtendThreshold;
	Double ringoThreshold;
        
        
	if(peak_bindingCheckBox.isSelected() || ringoCheckBox.isSelected() || percentileCheckbox.isSelected()) {
	    try {
		peak_distance = Integer.parseInt(peak_distanceTextField.getText());
		if (peak_bindingCheckBox.isSelected()) {
		    bindingPeakThreshold = Double.parseDouble(peak_bindingPeakTextField.getText());
		    bindingExtendThreshold = Double.parseDouble(peak_bindingExtendTextField.getText());
		} else {
		    System.out.println("binding threshold not selected");
		    bindingPeakThreshold = Double.NEGATIVE_INFINITY;
		    bindingExtendThreshold = Double.NEGATIVE_INFINITY;
		}
		if (peak_pValueCheckBox.isSelected()) {
		    pValuePeakThreshold = Double.parseDouble(peak_pValuePeakTextField.getText());
		    pValueExtendThreshold = Double.parseDouble(peak_pValueExtendTextField.getText());
		} else {
		    System.out.println("pvalue threshold not selected");
		    pValuePeakThreshold = Double.POSITIVE_INFINITY;
		    pValueExtendThreshold = Double.POSITIVE_INFINITY;

		}
                if(ringoCheckBox.isSelected())
                {
                    ringo_percentile=Double.parseDouble(peakPercentileTextField.getText());
                    ringo_extend_percentile=Double.parseDouble(extendPercentileTextField.getText());
                    ringo_window = (Integer)ringoSpinner.getModel().getValue();
                    
                    bindingPeakThreshold = ringoStarr(new File(inputFile.getPath()), ringo_percentile, ringo_window);
                    //bindingPeakThreshold = calculatePercentile(new File(inputFile.getPath()), ringo_percentile);
                    
                    
                    bindingExtendThreshold=ringoStarr(new File(inputFile.getPath()), ringo_extend_percentile, ringo_window);
                    
                    System.out.println(bindingPeakThreshold);
                    System.out.println(bindingExtendThreshold);
                    
                }
                
                if(percentileCheckbox.isSelected())
                {
                    bindingPeakThreshold=this.calculatePercentile(inputFile, Double.parseDouble(percentileTextField.getText()));
                    bindingExtendThreshold=bindingPeakThreshold;
                
                }
                

	    } catch (Exception ex) {
                Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(peakPanel, "Incorrect parameters.\nPlease check format of your parameters.", "Parameter error", JOptionPane.ERROR_MESSAGE);
		return;
            } 

	} 
	else {
	    JOptionPane.showMessageDialog(peakPanel, "No threshold selected!\nPlease choose one or more threshold.", "No threshold selected !", JOptionPane.WARNING_MESSAGE);
	    return;
	}

	
	
	
	if(!inputFile.exists()){
	    JOptionPane.showMessageDialog(peakPanel, "Specified input file\n'"+inputFile.getPath()+"'\nnot found !", "File not found !", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	if(outputFile.exists()){
	    int choice = JOptionPane.showConfirmDialog(peakPanel, "Specified output file\n'"+outputFile.getPath()+"'\nalready exist.\n Overwrite?", "File already exist !", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    if( choice != JOptionPane.YES_OPTION) return;
	}
	
        try{
	
            System.out.println(bindingPeakThreshold);
            System.out.println(bindingExtendThreshold);
            System.out.println(peak_distance);
            
            //System.out.println(avg+2*stddev);
            
            
            int nPeak = GFFPeak.GPS(inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), peak_distance, bindingPeakThreshold, pValuePeakThreshold, bindingExtendThreshold , pValueExtendThreshold, GFFRegion.EFFECTIVE);
            
            //int nPeak = GFFPeak.GFFPeak.GPS(inputFile.getPath(),outputFile.getPath(), 650, avg+2*stddev, Double.POSITIVE_INFINITY, avg+stddev, Double.POSITIVE_INFINITY, GFFLib.GFFRegion.EFFECTIVE);
                        
            
            consoleTextArea.append(Integer.toString(nPeak)+ " peaks found\n");
            consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
	setCursor(null);
	JOptionPane.showMessageDialog(peakPanel, "Peak detection terminated", "Peak detection terminated", JOptionPane.INFORMATION_MESSAGE);
        try {
            outputDirectory=outputFile.getParent();
            String[] temp=new File(outputDirectory).list();
                    outputFilesList=new ArrayList<String>(0);
                    
                    for(int i=0;i<temp.length;i++)
                    {
                      if(temp[i].endsWith(".gff"))  
                      {
                          outputFilesList.add(temp[i]);
                      }
                      
                      
                    }
            
            displayResults();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
        
    }//GEN-LAST:event_peak_runButtonMouseReleased

    private void peak_bindingCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peak_bindingCheckBoxMouseReleased
        
            peak_bindingCheckBox.setSelected(true);
        peak_bindingPeakLabel.setEnabled(true);
            peak_bindingPeakTextField.setEnabled(true);
            peak_bindingExtendLabel.setEnabled(true);
            peak_bindingExtendTextField.setEnabled(true);
            ringoCheckBox.setSelected(false);
            useRingoThresholds=false;
            peakPercentileLabel.setEnabled(false);
            ringoWindowSizeLabel.setEnabled(false);
            ringoSpinner.setEnabled(false);
            peakPercentileTextField.setEnabled(false);
            extendPercentileLabel.setEnabled(false);
            extendPercentileTextField.setEnabled(false);
            
            percentileCheckbox.setSelected(false);
            percentileLabel.setEnabled(false);
            percentileTextField.setEnabled(false);
            peak_bindingPeakComboBox.setEnabled(true);
            peak_bindingExtendComboBox.setEnabled(true);
            peak_distanceLabel.setEnabled(true);
            peak_distanceTextField.setEnabled(true);
        
       
	
        
        
    }//GEN-LAST:event_peak_bindingCheckBoxMouseReleased

    private double ringoStarr(File inputFile, double percentile, int wSize) throws Exception{
        double[] percentiles = {percentile};
        return ringoStarr(inputFile,percentiles,wSize)[0];
    }
    private double[] ringoStarr(File inputFile, double[] percentiles, int wSize) throws Exception{
        String ligne;
        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        ArrayList<GFFData> datas = new ArrayList<GFFData>(250000);
        ArrayList<GFFData> window = new ArrayList<GFFData>(10);
        ArrayList<Double> scores = new ArrayList<Double>(250000);
        double mean, sum;
        int iPercentile;
        double[] thresholds = new double[percentiles.length];

        ligne = bReader.readLine();
        //On vire les lignes vides ou de commentaire
        while(ligne.startsWith("##") || ligne.equals("")) ligne = bReader.readLine();
        //Pour chaque ligne non vide du fichier .gff
        while (ligne != null) {
           datas.add(new GFFData(ligne));
           ligne = bReader.readLine();
        }
        //Shuffling
        java.util.Collections.shuffle(datas);
        for(int i=0;i<datas.size()-wSize+1;i++){
           window.clear();
           for(int j=i;j<i+wSize;j++)window.add(datas.get(j));
           //lissage de la fenetre
           sum = 0;
           mean = 0;
           for(GFFData d : window) sum += d.score;
           mean = sum/wSize;
           for(GFFData d : window) d.score = mean;
           //fin lissage, on ajoute la première sonde au tableau des scores modifiés.
           scores.add(window.get(0).score);
        }
        //On rajoute les sondes restantes dans la fenêtre.
        for(int i=1;i<wSize;i++) scores.add(window.get(i).score);
        bReader.close();
        
        //On trie les scores modifiés
        java.util.Collections.sort(scores);
        //On calcule l'index du score correspondant au percentile
        for(int i=0;i<percentiles.length;i++){
            iPercentile = (int)(percentiles[i]*scores.size());
            thresholds[i] = scores.get(iPercentile);
        }
        
        
        return thresholds;
    }
    private void peak_pValueCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peak_pValueCheckBoxMouseReleased
        if(peak_pValueCheckBox.isSelected()){
	    peak_pValuePeakLabel.setEnabled(true);
            peak_pValuePeakTextField.setEnabled(true);
            peak_pValueExtendLabel.setEnabled(true);
            peak_pValueExtendTextField.setEnabled(true);
	}
	else{
	    peak_pValuePeakLabel.setEnabled(false);
            peak_pValuePeakTextField.setEnabled(false);
            peak_pValueExtendLabel.setEnabled(false);
            peak_pValueExtendTextField.setEnabled(false);
	}
    }//GEN-LAST:event_peak_pValueCheckBoxMouseReleased

    private void peak_bindingPeakComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_peak_bindingPeakComboBoxItemStateChanged
        // TODO add your handling code here:
        switch(peak_bindingPeakComboBox.getSelectedIndex()){
	    case 0 : {
		peak_bindingPeakTextField.setText(""+(avg+stddev));
		break;
	    }
            case 1 : {
		peak_bindingPeakTextField.setText(""+(avg+2*stddev));
		break;
	    }
	    case 2 : {
		peak_bindingPeakTextField.setText(""+(avg+2.5*stddev));
		break;
	    }
	    case 3 : {
		peak_bindingPeakTextField.setText(""+(avg+3*stddev));
		break;
	    }
	    case 4 : {
		break;
	    }
	    default : {
		break;
	    }
	}
    }//GEN-LAST:event_peak_bindingPeakComboBoxItemStateChanged

    private void peak_bindingExtendComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_peak_bindingExtendComboBoxItemStateChanged
        // TODO add your handling code here:
        switch(peak_bindingExtendComboBox.getSelectedIndex()){
	    case 0 : {
		peak_bindingExtendTextField.setText(""+(avg+stddev));
		break;
	    }
	    case 1 : {
		peak_bindingExtendTextField.setText(""+(avg+1.5*stddev));
		break;
	    }
	    case 2 : {
		peak_bindingExtendTextField.setText(""+(avg+2*stddev));
		break;
	    }
	    case 3 : {
		break;
	    }
	    default : {
		break;
	    }
	}
    }//GEN-LAST:event_peak_bindingExtendComboBoxItemStateChanged

    private void peak_bindingExtendComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peak_bindingExtendComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_peak_bindingExtendComboBoxActionPerformed

    private void intraNormTypeRadioButton_pengActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intraNormTypeRadioButton_pengActionPerformed
        // TODO add your handling code here:
        normType="peng";
}//GEN-LAST:event_intraNormTypeRadioButton_pengActionPerformed

    private void interNormTypeRadioButton_noneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interNormTypeRadioButton_noneActionPerformed
// TODO add your handling code here:
    interNormType="none";
}//GEN-LAST:event_interNormTypeRadioButton_noneActionPerformed

    private void interNormTypeRadioButton_meanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interNormTypeRadioButton_meanActionPerformed
// TODO add your handling code here:
    interNormType="median";
}//GEN-LAST:event_interNormTypeRadioButton_meanActionPerformed

    private void interNormTypeRadioButton_quantileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interNormTypeRadioButton_quantileActionPerformed
// TODO add your handling code here:
    interNormType="quantile";
}//GEN-LAST:event_interNormTypeRadioButton_quantileActionPerformed

    private void mergeSlidesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeSlidesCheckBoxActionPerformed
// TODO add your handling code here:
    if(mergeSlidesCheckBox.isSelected())
                {
                    mergeSlides=true;
                }
}//GEN-LAST:event_mergeSlidesCheckBoxActionPerformed

    private void replicateMergeMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replicateMergeMethodComboBoxActionPerformed
// TODO add your handling code here:
        
                if(replicateMergeMethodComboBox.getSelectedIndex()==0)
                {
                    mergeReplicatesMethod="mean";
                }
                if(replicateMergeMethodComboBox.getSelectedIndex()==1)
                {
                    mergeReplicatesMethod="agilent";
                }
}//GEN-LAST:event_replicateMergeMethodComboBoxActionPerformed

    private void resultsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsComboBoxActionPerformed
// TODO add your handling code here:
     FileReader fr = null;
        try {
            fr = new FileReader(outputDirectory + "//"+outputFilesList.get(resultsComboBox.getSelectedIndex()));
            BufferedReader reader = new BufferedReader(fr);
            Vector data = new Vector(0);
            boolean endOfFile = false;
            
            while (!endOfFile) {
                String tmp = reader.readLine();
                if (tmp == null) {
                    endOfFile = true;
                } else {
                    Vector linedata = new Vector(edu.emory.mathcs.backport.java.util.Arrays.asList(tmp.split("\t")));
                    
                    
                    DecimalFormat format=new DecimalFormat("00000.00");
                    linedata.set(5,format.format(Double.parseDouble(tmp.split("\t")[5])));
                    //linedata.set(5,Double.parseDouble(tmp.split("\t")[5]));
                    
                   
                    data.addElement(linedata);
                }
            }
            //String[] headers ={"Chromosome","Source","Feature","Start","End","Score","Strand"};
            Vector headers = new Vector() {

                {
                    add("Chromosome");
                    add("Source");
                    add("Feature");
                    add("Start");
                    add("End");
                    add("Score");
                    add("Strand");
                    add("P-Value");
                    add("Attributes");
                    add("Comments");
                }
            };

            JXTable table = new JXTable(data,headers);
            table.setLocale(Locale.getDefault());
                    //jScrollPane3 = new JScrollPane(table);
                    //jPanel8.add(jScrollPane3);
                    resultsScrollPane.setViewportView(table);
                    table.setFillsViewportHeight(true);
                    table.setSortable(true);
        } catch (IOException ex) {
            Logger.getLogger(COCAS.class.getName()).log(Level.SEVERE, null, ex);
        
        }
}//GEN-LAST:event_resultsComboBoxActionPerformed

private void outputDirBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputDirBrowseButtonActionPerformed
// TODO add your handling code here:
    JFileChooser directoryChooser = new JFileChooser();
    directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = directoryChooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
       outputDirectory = directoryChooser.getSelectedFile().getAbsolutePath();
       //outputDirectory=outputDirectory.replaceAll("\\\\","//");
       outputDirectory=outputDirectory.replace('\\','/');
       outputDirectory=outputDirectory.replaceAll(" ","\\ ");
       outputDirTextField.setText(outputDirectory);
    }
    
}//GEN-LAST:event_outputDirBrowseButtonActionPerformed

private void loadPopupMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPopupMenuItemActionPerformed1
    // TODO add your handling code here:
    this.loadFiles();
}//GEN-LAST:event_loadPopupMenuItemActionPerformed1

private void unloadPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unloadPopupMenuItemActionPerformed
    // TODO add your handling code here:
    this.unLoadFiles();
}//GEN-LAST:event_unloadPopupMenuItemActionPerformed

// get bg correction method in GUI
private void bgCorrectionMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bgCorrectionMethodComboBoxActionPerformed
    
    
    switch(bgCorrectionMethodComboBox.getSelectedIndex())
    {
        case 0: bcMethod = "none"; break;
        case 1: bcMethod = "subtract"; break;
        case 2: bcMethod = "half"; break;
        case 3: bcMethod = "minimum"; break;
        case 4: bcMethod = "movingmin"; break;
        case 5: bcMethod = "edwards"; break;
        case 6: bcMethod = "normexp"; break;
        case 7: bcMethod = "rma"; break;
    }
    
}//GEN-LAST:event_bgCorrectionMethodComboBoxActionPerformed

private void outputTypeCheckBox_splitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputTypeCheckBox_splitterActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_outputTypeCheckBox_splitterActionPerformed

private void fileListScrollPaneVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_fileListScrollPaneVetoableChange
    jScrollPane1.getViewport().setViewPosition(fileListScrollPane.getViewport().getViewPosition());    // TODO add your handling code here:
}//GEN-LAST:event_fileListScrollPaneVetoableChange

private void loadBarMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBarMenuItem1ActionPerformed
    this.loadFiles();
}//GEN-LAST:event_loadBarMenuItem1ActionPerformed

private void unloadBarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unloadBarMenuItemActionPerformed
    // TODO add your handling code here:
    this.unLoadFiles();
}//GEN-LAST:event_unloadBarMenuItemActionPerformed

private void updateLibrariesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateLibrariesMenuItemActionPerformed
    // TODO add your handling code here:
    REXP R;
    R=re.eval("library()$results[,1]");
    String[] rarray=R.asStringArray();
    RVector rvector = R.asVector();
    //RList rlist = R.asList();
    String rstring = R.asString();
    String rsymbol = R.asSymbolName();
    System.out.println(R);
    java.util.List<String> rlist = edu.emory.mathcs.backport.java.util.Arrays.asList(rarray);
    
    
    if(!rlist.containsAll(new java.util.ArrayList(0) {
        {
            add("tkWidgets");
            add("widgetTools");
            add("tcltk");
            add("tools");
            add("DynDoc");
            add("geneplotter");
            add("annotate");
            add("lattice");
            //add("vsn");
            //add("affy");
            add("affyio");
            add("RColorBrewer");
            
        }
    })) {
        re.eval("source(\"http://bioconductor.org/biocLite.R\")");
        re.eval("biocLite()");
        re.eval("install.packages(\"MASS\")");
        re.eval("install.packages(\"spatstat\")");
    }
    
    String path = System.getProperty("java.class.path");
            //String path = new File(".").getCanonicalPath();
    System.out.println(path);
    
    System.out.println("source(unz(\""+path.replace("\\","/")+"\""+ ", \"data/cocasV2-4.R\",\"r\"))");
    System.out.println(re.eval("source(unz(\""+path.replace("\\","/")+"\""+ ", \"data/cocasV2-4.R\",\"r\"))"));
            
    
}//GEN-LAST:event_updateLibrariesMenuItemActionPerformed

private void startWizardMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startWizardMenuItemActionPerformed
    // TODO add your handling code here:
    try {
        wizard = new TestWizard.Wizard(this,true);
        
        
        this.centreDialog(wizard);
        wizard.setVisible(true);
        useWizard=true;
        normType=wizard.getNormalizationType();
        expName=wizard.getExpName();
        title=wizard.getName();
        slideNumberList=wizard.returnSlideNumberLists();
        fileNamesList=wizard.getFileNames();
        dyeSwapList=wizard.getSwapList();
        directoryName=wizard.getDirectoryName();
        interNormType=wizard.getInterNormalizationType();
        mergeSlides=wizard.getMergeSlides();
        mergeReplicatesMethod=wizard.getReplicateMergeMethod();
        if(wizard.isComplete()) {
            this.startAnalysis();
        }
        
    } catch(Exception e) {
        
    }
}//GEN-LAST:event_startWizardMenuItemActionPerformed

private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
    // TODO add your handling code here:
    String path = System.getProperty("java.class.path").substring(0, System.getProperty("java.class.path").indexOf("COCAS_v2.4.jar")-1);
    //WebBrowserControl.displayURL(path.replace("\\","/")+"/data/help.html");
    
    System.out.println("file:///"+path.replace("\\","/").replace(" ","%20")+"/data/help.html");
    
     try {
    			        String osName = System.getProperty("os.name").toLowerCase();
	        	        Runtime rt = Runtime.getRuntime();
				if (osName.indexOf( "win" ) >= 0) {
		        	        rt.exec( "rundll32 url.dll,FileProtocolHandler " + "file:///"+path.replace("\\","/").replace(" ","%20")+"/data/help.html");
		                }
                                else if (osName.indexOf("mac") >= 0) {
		                        rt.exec( "open " + "file:///"+path.replace("\\","/").replace(" ","%20")+"/data/help.html");
				}
		        
		                else if (osName.indexOf("ix") >=0 || osName.indexOf("ux") >=0 || osName.indexOf("sun") >=0) {
		        	        String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
		        			"netscape","opera","links","lynx"};
 
		        	        // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
		        	        StringBuffer cmd = new StringBuffer();
		        	        for (int i = 0 ; i < browsers.length ; i++)
		        		        cmd.append((i == 0  ? "" : " || " ) + browsers[i] +" \"" +"file:///"+path.replace("\\","/").replace(" ","%20")+"/data/help.html");
 
		        	        rt.exec(new String[] { "sh", "-c", cmd.toString() });
		                }
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
}//GEN-LAST:event_helpMenuItemActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
    // TODO add your handling code here:
    
    //Creates new "About" credits frame
    try {
        URL url = COCAS.class.getResource("/data/CoCAS_Splash.gif");
        final BufferedImage img = ImageIO.read(url);
        JFrame aboutFrame = new JFrame(){public void paint(Graphics g){g.drawImage(img, 0, 0, rootPane);}};
        
        
        aboutFrame.setSize(img.getWidth(), img.getHeight());
        aboutFrame.setResizable(false);
        centreFrame(aboutFrame);
        
        aboutFrame.setVisible(true);
        aboutFrame.show();
    } catch (IOException e) {
    }
}//GEN-LAST:event_aboutMenuItemActionPerformed

private void ringoCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ringoCheckBoxMouseReleased
    // TODO add your handling code here:
    
        peak_bindingCheckBox.setSelected(false);
        useRingoThresholds=true;
        peakPercentileLabel.setEnabled(true);
        ringoCheckBox.setSelected(true);
        ringoWindowSizeLabel.setEnabled(true);
        ringoSpinner.setEnabled(true);
        peakPercentileTextField.setEnabled(true);
        extendPercentileLabel.setEnabled(true);
            extendPercentileTextField.setEnabled(true);
        peak_bindingPeakLabel.setEnabled(false);
        peak_bindingPeakComboBox.setEnabled(false);
        peak_bindingPeakTextField.setEnabled(false);
        peak_bindingExtendLabel.setEnabled(false);
        peak_bindingExtendComboBox.setEnabled(false);
        peak_bindingExtendTextField.setEnabled(false);
        percentileCheckbox.setSelected(false);
            percentileLabel.setEnabled(false);
            percentileTextField.setEnabled(false);
            peak_distanceLabel.setEnabled(false);
            peak_distanceTextField.setEnabled(false);
        
    
    
}//GEN-LAST:event_ringoCheckBoxMouseReleased

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    // TODO add your handling code here:
    System.exit(0);
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void peakPercentileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_peakPercentileTextFieldKeyReleased
// TODO add your handling code here:
    if(evt.equals(java.awt.event.KeyEvent.VK_ENTER))
    {
            
    }
}//GEN-LAST:event_peakPercentileTextFieldKeyReleased

private void stop_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_ButtonActionPerformed
    // TODO add your handling code here:
    thread.interrupt();
    analysisProgressBar.setVisible(false);
    stop_Button.setEnabled(false);
}//GEN-LAST:event_stop_ButtonActionPerformed

private void percentileCheckboxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percentileCheckboxMouseReleased
// TODO add your handling code here:
    
   
            peak_bindingCheckBox.setSelected(false);
            peak_bindingPeakLabel.setEnabled(false);
            peak_bindingPeakTextField.setEnabled(false);
            peak_bindingExtendLabel.setEnabled(false);
            peak_bindingExtendTextField.setEnabled(false);
            useRingoThresholds=false;
            peakPercentileLabel.setEnabled(false);
            ringoWindowSizeLabel.setEnabled(false);
            ringoSpinner.setEnabled(false);
            extendPercentileLabel.setEnabled(false);
            extendPercentileTextField.setEnabled(false);
            ringoCheckBox.setSelected(false);
            peakPercentileTextField.setEnabled(false);
            percentileCheckbox.setSelected(true);
            percentileLabel.setEnabled(true);
            percentileTextField.setEnabled(true);       
            peak_bindingPeakComboBox.setEnabled(false);
            peak_bindingExtendComboBox.setEnabled(false);
            peak_distanceLabel.setEnabled(false);
            peak_distanceTextField.setEnabled(false);
        
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}//GEN-LAST:event_percentileCheckboxMouseReleased

private void percentileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_percentileTextFieldKeyReleased
// TODO add your handling code here:
}//GEN-LAST:event_percentileTextFieldKeyReleased

private void extendPercentileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_extendPercentileTextFieldKeyReleased
    // TODO add your handling code here:
}//GEN-LAST:event_extendPercentileTextFieldKeyReleased
    
    /**
     * show file load dialog
     */
    public void loadFiles()
    {
        
        
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        
        // Enable multiple selections
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new TextFilter());
        //chooser.setDialogTitle("Choose Microarray Files");
        // Show the dialog; wait until dialog is closed
        int returnVal = chooser.showOpenDialog(this);
        try
        {
            //fileNamesList = new String[chooser.getSelectedFiles().length];
            for(int i=0;i<chooser.getSelectedFiles().length;i++)
            {
                fileNames+=chooser.getSelectedFiles()[i].getName()+"\n\n";
                fileNamesList.add(chooser.getSelectedFiles()[i].getAbsolutePath().replace("\\","/"));
                 
            }
            
            if(returnVal == JFileChooser.APPROVE_OPTION)
            
            {
            //jTabbedPane1.setEnabledAt(0,true);
            
            
        this.updateDyeSwapPanel(); 
        tabbedPane.setSelectedIndex(0);
        
        //directoryName=chooser.getSelectedFile().getAbsolutePath().substring(0,chooser.getSelectedFile().getAbsolutePath().lastIndexOf("\\"));
        directoryName=chooser.getSelectedFile().getParent();
        
        //consoleTextArea.setText(console.getRText());
        
        //consoleTextArea.append(re.eval("R.version.string").asString());
        
        
        //re.eval("setwd(\"C:/tmp\")");
        
        //directoryName=directoryName.replaceAll("\\\\","//");
        directoryName=directoryName.replace('\\','/');
        directoryName=directoryName.replaceAll(" ","\\ ");
        outputDirTextField.setText(directoryName);
        System.out.println(directoryName);
        
        
        
        
        
            }
        }
        catch(NullPointerException npe)
        {
            
        }
    }
    
   public void unLoadFiles()
   {
       
       
       int[] unloadListIndices = fileList.getSelectedIndices();
       
       for(int i=0;i<unloadListIndices.length;i++)
       {
           fileNamesList.remove(unloadListIndices[i]);
           dyeSwapList.remove(unloadListIndices[i]);
           dyeSwapCheckBoxList.remove(unloadListIndices[i]);
           slideNumberList.remove(unloadListIndices[i]);
           slideNumberTextFieldList.remove(unloadListIndices[i]);
           inputComboBoxList.get(unloadListIndices[i]).removeActionListener(inputComboBoxList.get(unloadListIndices[i]).getActionListeners()[0]);
           IPComboBoxList.get(unloadListIndices[i]).removeActionListener(IPComboBoxList.get(unloadListIndices[i]).getActionListeners()[0]);
           
           
           inputComboBoxList.remove(unloadListIndices[i]);
           IPComboBoxList.remove(unloadListIndices[i]);
           
       }
        
        
       
        
        this.updateDyeSwapPanel();
        
        
   }
   
   public double calculatePercentile(File inputFile, double d) throws FileNotFoundException, IOException, Exception
   {
       double percentile=0;
        BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        ArrayList<Double> datas = new ArrayList<Double>();
       String line="";
        
        //Pour chaque ligne non vide du fichier .gff
        while ((line = bReader.readLine()) != null) {
           datas.add(Double.parseDouble(line.split("\t")[5]));
           
        }
       java.util.Collections.sort(datas);
       int index=(int)(datas.size()*d);
       percentile=datas.get(index);
       return percentile;
       
   }
   
   
    public void centreDialog(JDialog dialog)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = dialog.getSize();
        screenSize.height = screenSize.height/2;
        screenSize.width = screenSize.width/2;
        size.height = size.height/2;
        size.width = size.width/2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        dialog.setLocation(x, y);
    }
    public void centreFrame(JFrame frame)
    {
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = frame.getSize();
        screenSize.height = screenSize.height/2;
        screenSize.width = screenSize.width/2;
        size.height = size.height/2;
        size.width = size.width/2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        frame.setLocation(x, y);

    }
    public void updateDyeSwapPanel()
    {
            dyeSwapPanel.removeAll();
            if(useWizard)
            {
                for(int i=0;i<fileNamesList.size();i++)
                    {
                    dyeSwapCheckBoxList.add(new JCheckBox());
                    dyeSwapCheckBoxList.get(i).setText("Swap Dyes");
                    dyeSwapCheckBoxList.get(i).setToolTipText("Check this box if Cy5 has been assigned to Input and Cy3 to IP");
                    
                    IPComboBoxList.add(new JComboBox());
                    IPComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy5", "Cy3" }));
                    IPComboBoxList.get(i).setSelectedIndex(0);
                    
                    inputComboBoxList.add(new JComboBox());
                    inputComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy3", "Cy5" }));
                    inputComboBoxList.get(i).setSelectedIndex(0);
                    final int j=i;
                    //add action listener to update comoboxes
                    IPComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            inputComboBoxList.get(j).setSelectedIndex(IPComboBoxList.get(j).getSelectedIndex());
                            if(IPComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                            
                        }
                    });
                    
                    inputComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            IPComboBoxList.get(j).setSelectedIndex(inputComboBoxList.get(j).getSelectedIndex());
                            if(inputComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                        }
                    });
                    
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, IPComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), inputComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, inputComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), IPComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    inputComboBoxList.get(i).setToolTipText("Choose Input stain : usually Cy3 is used for input, choose Cy5 for dye swap ");
                    IPComboBoxList.get(i).setToolTipText("Choose IP stain : usually Cy5 is used for IP, choose Cy3 for dye swap ");
                    
                    
                    
                    dyeSwapCheckBoxList.get(i).setSelected(false);
                    if(dyeSwapList.get(i))
                    {
                        IPComboBoxList.get(i).setSelectedItem("Cy3");                        
                    }
                    else
                    if(dyeSwapList.get(i))
                    {
                        IPComboBoxList.get(i).setSelectedItem("Cy5");                        
                    }    
                    
                    
                    
                    
                    
                    
                    slideNumberTextFieldList.add(new JTextField());
                    slideNumberTextFieldList.get(i).setColumns(2);
                    slideNumberTextFieldList.get(i).setText(Integer.toString(slideNumberList.get(i)));
                    slideNumberTextFieldList.get(i).setToolTipText("This field corresponds to your experiment number or slide number in the case of a multiple array design");
                    
                    
                    
                    JPanel tmpPanel = new JPanel();
                
                tmpPanel.add(new JLabel("Slide #"));
                    tmpPanel.add(slideNumberTextFieldList.get(i));
                    
                      tmpPanel.add(new JLabel("IP"));
                      tmpPanel.add(IPComboBoxList.get(i));
                      tmpPanel.add(new JLabel("Input"));
                      tmpPanel.add(inputComboBoxList.get(i));
                    //dyeSwapPanel.add(dyeSwapCheckBoxList.get(i));
                       //tmpPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
                       //tmpPanel.setSize(dyeSwapPanel.getWidth(), fileList.getFixedCellHeight());
                       dyeSwapPanel.add(tmpPanel);
                    //dyeSwapPanel.add(dyeSwapCheckBoxList.get(i));
                    }
            }
            else
            {
            boolean addText=false;
            if(dyeSwapList.size()==0)
            {
                System.out.println(fileNamesList.size());
                addText=true;
                //dyeSwapList = new ArrayList<Boolean>(fileNamesList.size());
                //dyeSwapCheckBoxList = new ArrayList<JCheckBox>(fileNamesList.size());
                //slideNumberList = new ArrayList<Integer>(fileNamesList.size());
                //slideNumberTextFieldList = new ArrayList<JTextField>(fileNamesList.size());
                for(int i=0;i<fileNamesList.size();i++)
                {
                    
                    
                    dyeSwapCheckBoxList.add(new JCheckBox());
                    dyeSwapCheckBoxList.get(i).setText("Swap Dyes");
                   
                    
                    IPComboBoxList.add(new JComboBox());
                    IPComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy5", "Cy3" }));
                    IPComboBoxList.get(i).setSelectedIndex(0);
                    
                    inputComboBoxList.add(new JComboBox());
                    inputComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy3", "Cy5" }));
                    inputComboBoxList.get(i).setSelectedIndex(0);
                    final int j=i;
                    //add action listener to update comoboxes
                    IPComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            inputComboBoxList.get(j).setSelectedIndex(IPComboBoxList.get(j).getSelectedIndex());
                            if(IPComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                            System.out.println("dye swap"+dyeSwapList.get(j));
                        }
                        
                    });
                    
                    inputComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            IPComboBoxList.get(j).setSelectedIndex(inputComboBoxList.get(j).getSelectedIndex());
                            if(inputComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                            System.out.println("dye swap"+dyeSwapList.get(j));
                        }
                    });
                    
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, IPComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), inputComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, inputComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), IPComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    inputComboBoxList.get(i).setToolTipText("Choose Input stain : usually Cy3 is used for input, choose Cy5 for dye swap ");
                    IPComboBoxList.get(i).setToolTipText("Choose IP stain : usually Cy5 is used for IP, choose Cy3 for dye swap ");
                    
                    //fileList.setFixedCellHeight(6);
                    dyeSwapCheckBoxList.get(i).setSelected(false);
                    slideNumberList.add(1);
                    slideNumberTextFieldList.add(new JTextField());
                    slideNumberTextFieldList.get(i).setColumns(2);
                    slideNumberTextFieldList.get(i).setToolTipText("This field corresponds to your experiment number or slide number in the case of a multiple array design");
                    
                    
                    dyeSwapList.add(false);
                }
                
                
                //bindingGroup.bind();
            }
            
            /*
            JLabel[] IPLabelList = new JLabel[fileNamesList.length];
            JComboBox[] IPComboBoxList = new JComboBox[fileNamesList.length];
            JLabel[] InputLabelList = new JLabel[fileNamesList.length];
            JComboBox[] InputComboBoxList = new JComboBox[fileNamesList.length];
            */
            
            
            //if some files loaded fill list from end
            if(filesLoaded)
                {
                    int s=inputComboBoxList.size();
                    for(int i=s;i<fileNamesList.size();i++)
                    {
                         dyeSwapCheckBoxList.add(new JCheckBox());
                    dyeSwapCheckBoxList.get(i).setText("Swap Dyes");
                    dyeSwapCheckBoxList.get(i).setToolTipText("Check this box if Cy5 has been assigned to Input and Cy3 to IP");
                    
                    IPComboBoxList.add(new JComboBox());
                    IPComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy5", "Cy3" }));
                    IPComboBoxList.get(i).setSelectedIndex(0);
                    
                    inputComboBoxList.add(new JComboBox());
                    inputComboBoxList.get(i).setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy3", "Cy5" }));
                    inputComboBoxList.get(i).setSelectedIndex(0);
                    final int j=i;
                    //add action listener to update comoboxes
                    IPComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            inputComboBoxList.get(j).setSelectedIndex(IPComboBoxList.get(j).getSelectedIndex());
                            if(IPComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                            
                        }
                    });
                    
                    inputComboBoxList.get(i).addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt) 
                        {
                            IPComboBoxList.get(j).setSelectedIndex(inputComboBoxList.get(j).getSelectedIndex());
                            if(inputComboBoxList.get(j).getSelectedIndex()==0)
                            {
                                dyeSwapList.set(j, false);
                            }
                            else
                            {
                                dyeSwapList.set(j, true);
                            }
                            
                        }
                    });
                    
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, IPComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), inputComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    //bindingGroup.addBinding(org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, inputComboBoxList.get(i), org.jdesktop.beansbinding.ELProperty.create("${selectedIndex}"), IPComboBoxList.get(i), org.jdesktop.beansbinding.BeanProperty.create("selectedItem")));
                    inputComboBoxList.get(i).setToolTipText("Choose Input stain : usually Cy3 is used for input, choose Cy5 for dye swap ");
                    IPComboBoxList.get(i).setToolTipText("Choose IP stain : usually Cy5 is used for IP, choose Cy3 for dye swap ");
                    
                    
                    
                    
                    dyeSwapCheckBoxList.get(i).setSelected(false);
                    slideNumberList.add(1);
                    slideNumberTextFieldList.add(new JTextField());
                    slideNumberTextFieldList.get(i).setColumns(2);
                    slideNumberTextFieldList.get(i).setText("1");
                    slideNumberTextFieldList.get(i).setToolTipText("This field corresponds to your experiment number or slide number in the case of a multiple array design");
                    dyeSwapList.add(false);
                    }
                }
            for(int i=0;i<fileNamesList.size();i++)
            {
                
                
                
                //slideNumberTextFieldList[i].setText(slideNumberTextFieldList[i].getText());
                //dyeSwapCheckBoxList[i].setSelected(dyeSwapCheckBoxList[i].isSelected());
                //slideNumberTextFieldList[i].setText("1");
                //dyeSwapCheckBoxList[i].setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 11));
                
                
                /*
                IPLabelList[i]=new JLabel();
                IPLabelList[i].setText("IP");
                IPLabelList[i].setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 11));
                jPanel11.add(IPLabelList[i]);
                IPComboBoxList[i] = new JComboBox();IPComboBoxList[i].setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy3", "Cy5"}));
                IPComboBoxList[i].setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 11));
                jPanel11.add(IPComboBoxList[i]);
                InputLabelList[i]=new JLabel();
                InputLabelList[i].setText("Input");
                InputLabelList[i].setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 11));
                jPanel11.add(InputLabelList[i]);
                InputComboBoxList[i] = new JComboBox();InputComboBoxList[i].setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cy5", "Cy3"}));
                InputComboBoxList[i].setFont(new Font("MS Reference Sans Serif", Font.PLAIN, 11));
                jPanel11.add(InputComboBoxList[i]);
                */
                JPanel tmpPanel = new JPanel();
                
                tmpPanel.add(new JLabel("Slide #"));
                    tmpPanel.add(slideNumberTextFieldList.get(i));
                    
                      tmpPanel.add(new JLabel("IP"));
                      tmpPanel.add(IPComboBoxList.get(i));
                      tmpPanel.add(new JLabel("Input"));
                      tmpPanel.add(inputComboBoxList.get(i));
                    //dyeSwapPanel.add(dyeSwapCheckBoxList.get(i));
                       //tmpPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
                       //tmpPanel.setSize(dyeSwapPanel.getWidth(), fileList.getFixedCellHeight());
                       dyeSwapPanel.add(tmpPanel);
                       
                       
            }
            if(addText)
            {
                for(int i=0;i<fileNamesList.size();i++)
                {
                    slideNumberTextFieldList.get(i).setText("1");
                }
                addText=false;
            }
            }
        dyeSwapPanel.update(dyeSwapPanel.getGraphics());
        
        fileList.setListData((String[]) fileNamesList.toArray(new String[fileNamesList.size()]));
        fileList.setFixedCellHeight(35);
        
        fileList.update(fileList.getGraphics());
        dyeSwapPanel.setSize(221, fileList.getHeight()-10);
            dyeSwapPanel.setPreferredSize(new Dimension(221, fileList.getHeight()-10));
            dyeSwapPanel.setMinimumSize(new Dimension(221, fileList.getHeight()-10));
            dyeSwapPanel.setMaximumSize(new Dimension(221, fileList.getHeight()-10));
        
        intraNormLabel.setEnabled(true);
        intraNormTypeRadioButton_peng.setEnabled(true);
        intraNormTypeRadioButton_median.setEnabled(true);
        intraNormTypeRadioButton_loess.setEnabled(true);
        intraNormTypeRadioButton_vsn.setEnabled(true);
        interNormLabel.setEnabled(true);
        interNormTypeRadioButton_none.setEnabled(true);
        interNormTypeRadioButton_mean.setEnabled(true);
        interNormTypeRadioButton_quantile.setEnabled(true);
        outputTypeLabel.setEnabled(true);
        //jCheckBox2.setEnabled(true);
        //jCheckBox3.setEnabled(true);
        outputTypeCheckBox_bed.setEnabled(true);
        intraNormTypeRadioButton_peng.setText("Linear Correction and Weighted Loess");
        outputTypeCheckBox_splitter.setEnabled(true);
        analysisNameLabel.setEnabled(true);
        analysisNameTextField.setEnabled(true);
        runButton.setEnabled(true);
        runLabel.setEnabled(true);
        mergeSlidesCheckBox.setEnabled(true);
        replicateMergeMethodLabel.setEnabled(true);
        outputDirLabel.setEnabled(true);
        outputDirBrowseButton.setEnabled(true);
        outputDirTextField.setEnabled(true);
        replicateMergeMethodComboBox.setEnabled(true);
        bgCorrectionMethodLabel.setEnabled(true);
        bgCorrectionMethodComboBox.setEnabled(true);
        filesLoaded=true;
        
    }
    
    public static String get_arg(String label,String[] args) {
    String to_return = null;
    if (label != null && args != null) {
      int num_args = args.length;
      boolean got_it = false;
      for (int i = 0 ; i < num_args ; i++) {
	String item = args[i];
	if (got_it) {
	  to_return = item;
	  break;
	}
	if (item.equals(label)) {
	  got_it = true;
	}
      }
    }
    return to_return;
  }
    
    
    
    public static String[] addString(String[] l ,String s)
    {
        String[] tmp = new String[l.length+1];
        for(int i = 0;i<l.length;i++)
        {
            tmp[i]=l[i];
            
        }
        tmp[l.length]=s;
        return tmp;
    }
    
    public static boolean[] addBoolean(boolean[] l ,boolean s)
    {
        boolean[] tmp = new boolean[l.length+1];
        for(int i = 0;i<l.length;i++)
        {
            tmp[i]=l[i];
            
        }
        tmp[l.length]=s;
        return tmp;
    }
    
    public String[] removeString(String[] l,String s)
    {
        String[] tmp = new String[0];
        for(int i = 0;i<l.length;i++)
        {
            if(!l[i].equals(s))
            {
                tmp = this.addString(tmp, s);
            }
            
        }
        return tmp;
    }
    
    public String [] removeIntIndices(String[] l,int [] a)
    {
        String[] tmp = new String[0];
        for(int i = 0;i<l.length;i++)
        {
            //for(int j = 0;j<a.length;j++)
            //{
                //if(i!=a[j])
                //{
                //    tmp = this.addString(tmp, l[i]);
                //}
            //}
            if(this.intIsNotIn(a, i))
            {
                tmp = this.addString(tmp, l[i]);
            }
        }
        return tmp;
    }
    
    
    
    public boolean [] removeBooleanIndices(boolean[] l,int [] a)
    {
        boolean[] tmp = new boolean[0];
        for(int i = 0;i<l.length;i++)
        {
            //for(int j = 0;j<a.length;j++)
            //{
                //if(i!=a[j])
                //{
                //    tmp = this.addString(tmp, l[i]);
                //}
            //}
            if(this.intIsNotIn(a, i))
            {
                tmp = this.addBoolean(tmp, l[i]);
            }
        }
        return tmp;
    }
    
    public boolean intIsNotIn(int [] l,int a)
    {
        boolean isNotIn=true;
        for(int i=0;i<l.length;i++)
        {
            if(l[i]==a)
            {
                isNotIn=false;
            }
        }
        return isNotIn;
    }
    
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               COCAS cocas =  new COCAS();
               cocas.setSize(1024,768);
                       cocas.setVisible(true);
                
            }
        });
    }
    
    public Image getIcon() {
    Image icon = null;
    try {
      URL url = COCAS.class.getResource("/data/CoCAS.gif");
      if (url != null) {
        icon = Toolkit.getDefaultToolkit().getImage(url);
      }
    } catch (Exception e) {
      // It isn't a big deal if we can't find the icon, just return null
    }
    return icon;
  }
    
    
    public static void processDasServersList(String ql_url) {
    String server_loc_list = ql_url + "das_servers.txt";
    try {
      System.out.println("Trying to load DAS Server list: " + server_loc_list);
      DasDiscovery.addServersFromTabFile(server_loc_list);
    }
    catch (Exception ex) {
      System.out.println("WARNING: Failed to load DAS Server list: " + ex);
    }
  }

  public static void processDas2ServersList(String ql_url) {
    String server_loc_list = ql_url + "das2_servers.txt";
    try {
      System.out.println("Trying to load DAS Server list: " + server_loc_list);
      Das2Discovery.addServersFromTabFile(server_loc_list);
    }
    catch (Exception ex) {
      System.out.println("WARNING: Failed to load DAS Server list: " + ex);
    }
  }
  public static SingletonGenometryModel getGenometryModel() {
    return gmodel;
  }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JLabel analysisNameLabel;
    private javax.swing.JTextField analysisNameTextField;
    private javax.swing.JProgressBar analysisProgressBar;
    private javax.swing.JPanel annotationPanel;
    private javax.swing.JComboBox area_gffOptionComboBox;
    private javax.swing.JLabel area_gffOptionLabel;
    private javax.swing.JLabel area_regionIDColumnLabel;
    private javax.swing.JTextField area_regionIDColumnTextField;
    private javax.swing.JComboBox bgCorrectionMethodComboBox;
    private javax.swing.JLabel bgCorrectionMethodLabel;
    private javax.swing.JPanel browserPanel;
    private javax.swing.JLabel consoleLabel;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JPanel dyeSwapPanel;
    private javax.swing.JLabel extendPercentileLabel;
    private javax.swing.JTextField extendPercentileTextField;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JList fileList;
    private javax.swing.JPopupMenu fileListPopupMenu;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JMenu fileMenu1;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JPanel igbPanel;
    private javax.swing.JLabel interNormLabel;
    private javax.swing.JRadioButton interNormTypeRadioButton_mean;
    private javax.swing.JRadioButton interNormTypeRadioButton_none;
    private javax.swing.JRadioButton interNormTypeRadioButton_quantile;
    private javax.swing.JLabel intraNormLabel;
    private javax.swing.JRadioButton intraNormTypeRadioButton_loess;
    private javax.swing.JRadioButton intraNormTypeRadioButton_median;
    private javax.swing.JRadioButton intraNormTypeRadioButton_peng;
    private javax.swing.JRadioButton intraNormTypeRadioButton_vsn;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem loadBarMenuItem1;
    private javax.swing.JMenuItem loadPopupMenuItem;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JCheckBox mergeSlidesCheckBox;
    private javax.swing.JButton outputDirBrowseButton;
    private javax.swing.JLabel outputDirLabel;
    private javax.swing.JTextField outputDirTextField;
    private javax.swing.JCheckBox outputTypeCheckBox_bed;
    private javax.swing.JCheckBox outputTypeCheckBox_gff;
    private javax.swing.JCheckBox outputTypeCheckBox_sgr;
    private javax.swing.JCheckBox outputTypeCheckBox_splitter;
    private javax.swing.JLabel outputTypeLabel;
    private javax.swing.JPanel peakPanel;
    private javax.swing.JLabel peakPercentileLabel;
    private javax.swing.JTextField peakPercentileTextField;
    private javax.swing.JCheckBox peak_bindingCheckBox;
    private javax.swing.JComboBox peak_bindingExtendComboBox;
    private javax.swing.JLabel peak_bindingExtendLabel;
    private javax.swing.JTextField peak_bindingExtendTextField;
    private javax.swing.JComboBox peak_bindingPeakComboBox;
    private javax.swing.JLabel peak_bindingPeakLabel;
    private javax.swing.JTextField peak_bindingPeakTextField;
    private javax.swing.JLabel peak_distanceLabel;
    private javax.swing.JTextField peak_distanceTextField;
    private javax.swing.JButton peak_inputBrowserButton;
    private javax.swing.JLabel peak_inputFileLabel;
    private javax.swing.JTextField peak_inputFileTextField;
    private javax.swing.JButton peak_outputBrowserButton1;
    private javax.swing.JLabel peak_outputFileLabel;
    private javax.swing.JTextField peak_outputFileTextField;
    private javax.swing.JCheckBox peak_pValueCheckBox;
    private javax.swing.JLabel peak_pValueExtendLabel;
    private javax.swing.JTextField peak_pValueExtendTextField;
    private javax.swing.JLabel peak_pValuePeakLabel;
    private javax.swing.JTextField peak_pValuePeakTextField;
    private javax.swing.JButton peak_runButton;
    private javax.swing.JCheckBox percentileCheckbox;
    private javax.swing.JLabel percentileLabel;
    private javax.swing.JTextField percentileTextField;
    private javax.swing.JPanel qcrPanel;
    private javax.swing.JComboBox replicateMergeMethodComboBox;
    private javax.swing.JLabel replicateMergeMethodLabel;
    private javax.swing.JComboBox resultsComboBox;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JCheckBox ringoCheckBox;
    private javax.swing.JSpinner ringoSpinner;
    private javax.swing.JLabel ringoWindowSizeLabel;
    private javax.swing.JButton runButton;
    private javax.swing.JLabel runLabel;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JMenuItem startWizardMenuItem;
    private javax.swing.JButton stop_Button;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JMenuItem unloadBarMenuItem;
    private javax.swing.JMenuItem unloadPopupMenuItem;
    private javax.swing.JMenuItem updateLibrariesMenuItem;
    private javax.swing.JMenu updateMenu;
    // End of variables declaration//GEN-END:variables
    
}
