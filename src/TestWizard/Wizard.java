package TestWizard;

import java.awt.CardLayout;
import java.awt.Cursor;

/*
 * Wizard.java
 *
 * Created on 19 juin 2008, 16:09
 */
import java.awt.Font;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkListener;



/**
 *
 * @author  jeanniard
 */
public class Wizard extends javax.swing.JDialog {
    CardLayout cardLayout;
    public boolean wizardComplete=false;
    public WizardHyperLinkListener listener;
    /** Creates new form Wizard */
    public Wizard(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch(Exception e){}
        
        initComponents();
        cardLayout = (CardLayout)wizardInnerPanel.getLayout();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        interArrayNormalisation_meanRadioButton.setEnabled(true);
        interArrayNormalisation_agilentRadioButton.setEnabled(true);
        bgCorrectionMethodComboBox.setSelectedIndex(1);
        intraArrayNormalisation_explainEditorPane.setContentType("text/html");
        interArrayNormalisation_explainEditorPane.setContentType("text/html");
        intraArrayNormalisation_explainEditorPane.removeHyperlinkListener(listener); 
        intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>Global normalization assumes that the red and green intensities are related by a constant factor, i.e. R = kG, and the center of the distribution of log ratios is shifted to zero"

                + "<br><br>log2R/G -> log2R/G – c = log2R/(kG)<br><br> where c = log2k is the median. See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/11473024\">Zien A, Aigner T, Zimmer R, Lengauer T. Centralization: a new method for the normalization of gene expression data. Bioinformatics. 2001;17 Suppl 1:S323-31.</a> for details</body></html>");
         intraArrayNormalisation_explainEditorPane.addHyperlinkListener(listener = new WizardHyperLinkListener("http://www.ncbi.nlm.nih.gov/pubmed/17592629"));
    
            
            
        
       
         
    }
    private void loadParametersFile(File inputFile) throws Exception{
        BufferedReader inputReader;
        String ligne, lastPanel;
        ArrayList<String[]> comp = new ArrayList<String[]>(10);
        int nInsertedSlide=0;
        
        inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        ligne = inputReader.readLine();
        for(int i=wizardInnerPanel.getComponentCount()-6 ; i>0 ;i--) wizardInnerPanel.remove(i);
        while(ligne != null){
            if(ligne.startsWith("###")){
                lastPanel = ligne;
                System.out.println(ligne);
                ligne = inputReader.readLine();
                while(ligne != null && !ligne.equals("###")){
                    comp.add(ligne.split("="));
                    System.out.println(ligne.split("=")[0]+"\t"+ligne.split("=")[1]);
                    ligne = inputReader.readLine();
                }
                if(lastPanel.equals("###namesPanel")){
                    for(String[] s : comp){
                        if(s[0].equals("analysisName")) names_analysisNameTextField.setText(s[1]);
                        if(s[0].equals("numberOfSlides")) names_numberOfSlidesSpinner.setValue(Integer.parseInt(s[1]));
                    }
                    comp.clear();
                }
                if(lastPanel.equals("###slidePanel")){
                    SlidePanel newSlide = new SlidePanel();
                    wizardInnerPanel.add(newSlide, "slide"+(++nInsertedSlide), wizardInnerPanel.getComponentCount()-5);
                    for(String[] s : comp){
                        if(s[0].equals("slideName")) newSlide.setTitle(s[1]);
                        if(s[0].equals("file")) newSlide.addFile(new File(s[1]));
                        if(s[0].equals("swap")) newSlide.setSwap(Boolean.parseBoolean(s[1]), newSlide.numberOfFiles-1);    
                    }
                    comp.clear();
                }
                if(lastPanel.equals("###intraArrayNormalisationPanel")){
                    for(String[] s : comp){
                        if(s[0].equals("normalisation")){
                            if(s[1].equals("median")) intraArrayNormalisation_medianRadioButton.setSelected(true);
                            if(s[1].equals("peng")) intraArrayNormalisation_pengRadioButton.setSelected(true);
                            if(s[1].equals("vsn")) intraArrayNormalisation_vsnRadioButton.setSelected(true);
                            if(s[1].equals("lowess")) intraArrayNormalisation_lowessRadioButton.setSelected(true);
                            
                        }
                        if(s[0].equals("mergeMethod")){
                            if(s[1].equals("mean")) interArrayNormalisation_meanRadioButton.setSelected(true);
                            if(s[1].equals("agilent")) interArrayNormalisation_agilentRadioButton.setSelected(true);
                        }
                        
                        if(s[0].equals("mergeMethod")){
                            if(s[1].equals("none")) bgCorrectionMethodComboBox.setSelectedIndex(0);
                            if(s[1].equals("subtract")) bgCorrectionMethodComboBox.setSelectedIndex(1);
                            if(s[1].equals("half")) bgCorrectionMethodComboBox.setSelectedIndex(2);
                            if(s[1].equals("minimum")) bgCorrectionMethodComboBox.setSelectedIndex(3);
                            if(s[1].equals("movingmin")) bgCorrectionMethodComboBox.setSelectedIndex(4);
                            if(s[1].equals("edwards")) bgCorrectionMethodComboBox.setSelectedIndex(5);
                            if(s[1].equals("normexp")) bgCorrectionMethodComboBox.setSelectedIndex(6);
                            if(s[1].equals("rma")) bgCorrectionMethodComboBox.setSelectedIndex(7);
                        }
                    }
                    comp.clear();
                }
                if(lastPanel.equals("###interArrayNormalisationPanel")){
                    for(String[] s : comp){
                        if(s[0].equals("normalisation")){
                            if(s[1].equals("none")) interArrayNormalisation_noneRadioButton.setSelected(true);
                            if(s[1].equals("median")) interArrayNormalisation_medianRadioButton.setSelected(true);
                            if(s[1].equals("quantile")) interArrayNormalisation_quantileRadioButton.setSelected(true);
                        }
                        
                    }
                    comp.clear();
                }
                if(lastPanel.equals("###peakPanel")){
                    for(String[] s : comp){
                        if(s[0].equals("distanceThreshold")) peak_distanceTextField.setText(s[1]);
                        if(s[0].equals("bindingThreshold")) peak_bindingCheckBox.setSelected(Boolean.parseBoolean(s[1]));
                        if(s[0].equals("bindingPeakMeanThreshold")) peak_bindingPeakMeanTextField.setText(s[1]);
                        if(s[0].equals("bindingExtendMeanThreshold")) peak_bindingExtendMeanTextField.setText(s[1]);
                        if(s[0].equals("bindingPeakStddevThreshold")) peak_bindingPeakStddevTextField.setText(s[1]);
                        if(s[0].equals("bindingExtendStddevThreshold")) peak_bindingExtendStddevTextField.setText(s[1]);
                        if(s[0].equals("pValueThreshold")) peak_pValueCheckBox.setSelected(Boolean.parseBoolean(s[1]));
                        if(s[0].equals("pValuePeakThreshold")) peak_pValuePeakTextField.setText(s[1]);
                        if(s[0].equals("pValueExtendThreshold")) peak_pValueExtendTextField.setText(s[1]);
                    }
                    comp.clear();
                }
                if(lastPanel.equals("###outputPanel")){
                    for(String[] s : comp){
                        if(s[0].equals("outputDirectory")) output_outputDirectoryTextField.setText(s[1]);
                        if(s[0].equals("gffOutput")) output_gffCheckBox.setSelected(Boolean.parseBoolean(s[1]));
                        if(s[0].equals("sgrOutput")) output_sgrCheckBox.setSelected(Boolean.parseBoolean(s[1]));
                        if(s[0].equals("bedOutput")) output_bedCheckBox.setSelected(Boolean.parseBoolean(s[1]));
                    }
                    comp.clear();
                }
            }

            ligne = inputReader.readLine();
        }
        inputReader.close();
    }
    
    public ArrayList<String> getFileNames()
    {
        ArrayList<String> fileNames = new ArrayList<String>(0);
        for(int i=0 ; i < wizardInnerPanel.getComponentCount()-6 ; i++){
            
            SlidePanel sl = (SlidePanel)wizardInnerPanel.getComponent(i+1);
            for(int j=0 ; j<sl.numberOfFiles ; j++ ){
                //fileNames[i]=sl.getFile(j).getName();
             fileNames.add(sl.getFile(j).getAbsolutePath());
               
            }
            
    }
        return fileNames;
    }
    
    public String getDirectoryName()
    {
        String directoryName = "";
        
            
            SlidePanel sl = (SlidePanel)wizardInnerPanel.getComponent(1);
            
            
                directoryName=sl.getFile(0).getAbsolutePath();
                
            
            
    
        return directoryName.substring(0,directoryName.lastIndexOf("\\"));
    }
    
    
    
    
    public int getNumExps()
    {
        return wizardInnerPanel.getComponentCount()-6;
    }
    
    public ArrayList<Integer> returnSlideNumberLists()
    {
        ArrayList<java.lang.Integer> slideList = new ArrayList<java.lang.Integer>(0);
        for(int i=0 ; i < wizardInnerPanel.getComponentCount()-6 ; i++)
        {
            SlidePanel sl = (SlidePanel)wizardInnerPanel.getComponent(i+1);
            for(int j=0 ; j<sl.numberOfFiles ; j++ )
            {
                slideList.add(i+1);
            }
        }
        
        
         

       
        return slideList;
        
    }
    
    public ArrayList<Boolean> getSwapList()
    {
        ArrayList<Boolean> swapList = new ArrayList<Boolean>(0);
        for(int i=0 ; i < wizardInnerPanel.getComponentCount()-6 ; i++){
            
            SlidePanel sl = (SlidePanel)wizardInnerPanel.getComponent(i+1);
            
            for(int j=0 ; j<sl.numberOfFiles ; j++ ){
                
                //swapList[i]=(boolean)sl.getSwap(j);
                swapList.add((boolean)sl.getSwap(j));
            }
            
    }
        return swapList;
    }
    
    public String getExpName()
    {
        return names_analysisNameTextField.getText();
    }
    
    public String getOutputDir()
    {
        return output_outputDirectoryTextField.getText();
    }
    
    public String getNormalizationType()
    {
        String normType="";
        
        if(intraArrayNormalisation_medianRadioButton.isSelected()) normType="median";
        if(intraArrayNormalisation_lowessRadioButton.isSelected()) normType="loess";
        if(intraArrayNormalisation_vsnRadioButton.isSelected()) normType="vsn";
        if(intraArrayNormalisation_pengRadioButton.isSelected()) normType="peng";
        return normType;
    }
    
    
    public String getBGCorrectionMethod()
    {
        String bcMethod="";
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
        return bcMethod;
    }
    
    public String getInterNormalizationType()
    {
        String interNormType="";
        
        if(interArrayNormalisation_noneRadioButton.isSelected()) interNormType="none";
        if(interArrayNormalisation_medianRadioButton.isSelected()) interNormType="median";
        if(interArrayNormalisation_quantileRadioButton.isSelected()) interNormType="quantile";
         
        return interNormType;
        
    }
    
    public boolean getMergeSlides()
    {
        return interArrayNormalisation_mergeReplicatesCheckBox.isSelected();
    }
    
    public String getReplicateMergeMethod()
    {
        String replicateMergeMethod="";
        if(interArrayNormalisation_meanRadioButton.isSelected()) replicateMergeMethod="mean";
        if(interArrayNormalisation_agilentRadioButton.isSelected()) replicateMergeMethod="agilent";
        
        return replicateMergeMethod;
    }
    
    private void saveParametersFile(File outputFile) throws Exception{
        FileWriter output = new FileWriter(outputFile);                                                
        //namesPanel
        output.write("###namesPanel\n");
        output.write("analysisName="+names_analysisNameTextField.getText()+"\n");
        output.write("numberOfSlides="+names_numberOfSlidesSpinner.getValue()+"\n");
        output.write("###\n");
        //slidePanel
        for(int i=0 ; i < wizardInnerPanel.getComponentCount()-6 ; i++){
            output.write("###slidePanel\n");
            SlidePanel sl = (SlidePanel)wizardInnerPanel.getComponent(i+1);
            output.write("slideName"+"="+sl.getTitle()+"\n");
            for(int j=0 ; j<sl.numberOfFiles ; j++ ){
                output.write("file="+sl.getFile(j).getAbsolutePath()+"\n");
                output.write("swap="+sl.getSwap(j)+"\n");
            }
            output.write("###\n");
        }
        //intraArrayNormalisationPanel
        output.write("###intraArrayNormalisationPanel\n");
        if(intraArrayNormalisation_medianRadioButton.isSelected()) output.write("normalisation=median\n");
        if(intraArrayNormalisation_lowessRadioButton.isSelected()) output.write("normalisation=lowess\n");
        if(intraArrayNormalisation_vsnRadioButton.isSelected()) output.write("normalisation=vsn\n");
        if(intraArrayNormalisation_pengRadioButton.isSelected()) output.write("normalisation=peng\n");
        if(interArrayNormalisation_meanRadioButton.isSelected()) output.write("mergeMethod=mean\n");
        if(interArrayNormalisation_agilentRadioButton.isSelected()) output.write("mergeMethod=agilent\n");
        switch(bgCorrectionMethodComboBox.getSelectedIndex())
        {
            case 0: output.write("bcMethod=none\n"); break;
            case 1: output.write("bcMethod=subtract\n"); break;
            case 2: output.write("bcMethod=half\n"); break;
            case 3: output.write("bcMethod=minimum\n"); break;
            case 4: output.write("bcMethod=movingmin\n"); break;
            case 5: output.write("bcMethod=edwards\n"); break;
            case 6: output.write("bcMethod=normexp\n"); break;
            case 7: output.write("bcMethod=rma\n"); break;
        }
        output.write("###\n");
        //interArrayNormalisationPanel
        output.write("###interArrayNormalisationPanel\n");
        if(interArrayNormalisation_noneRadioButton.isSelected()) output.write("normalisation=none\n");
        if(interArrayNormalisation_medianRadioButton.isSelected()) output.write("normalisation=median\n");
        if(interArrayNormalisation_quantileRadioButton.isSelected()) output.write("normalisation=quantile\n");
        output.write("###\n");
        //peakPanel
        output.write("###peakPanel\n");
        output.write("distanceThreshold="+peak_distanceTextField.getText()+"\n");
        output.write("bindingThreshold="+peak_bindingCheckBox.isSelected()+"\n");
        output.write("bindingPeakMeanThreshold="+peak_bindingPeakMeanTextField.getText()+"\n");
        output.write("bindingPeakStddevThreshold="+peak_bindingPeakStddevTextField.getText()+"\n");
        output.write("bindingExtendMeanThreshold="+peak_bindingExtendMeanTextField.getText()+"\n");
        output.write("bindingExtendStddevThreshold="+peak_bindingExtendStddevTextField.getText()+"\n");
        output.write("pValueThreshold="+peak_pValueCheckBox.isSelected()+"\n");
        output.write("pValuePeakThreshold="+peak_pValuePeakTextField.getText()+"\n");
        output.write("pValueExtendThreshold="+peak_pValueExtendTextField.getText()+"\n");
        output.write("###\n");
        //outputPanel
        output.write("###outputPanel\n");
        output.write("outputDirectory="+output_outputDirectoryTextField.getText()+"\n");
        output.write("gffOutput="+output_gffCheckBox.isSelected()+"\n");
        output.write("sgrOutput="+output_sgrCheckBox.isSelected()+"\n");
        output.write("bedOutput="+output_bedCheckBox.isSelected()+"\n");
        output.write("###\n");
        
        
        
        output.close();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        directoryChooser = new javax.swing.JFileChooser();
        slideCreationDialog = new javax.swing.JDialog();
        slideCreationPanel = new javax.swing.JPanel();
        slideCreation_progressBar = new javax.swing.JProgressBar();
        intraArrayNormalisation_buttonGroup = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        interArrayNormalisation_buttonGroup = new javax.swing.ButtonGroup();
        mergeReplicates_buttonGroup = new javax.swing.ButtonGroup();
        wizardMainPanel = new javax.swing.JPanel();
        wizard_cancelButton = new javax.swing.JButton();
        wizard_nextButton = new javax.swing.JButton();
        wizard_backButton = new javax.swing.JButton();
        wizardMain_separator = new javax.swing.JSeparator();
        wizard_imageLabel = new javax.swing.JLabel();
        wizardInnerPanel = new javax.swing.JPanel();
        namesPanel = new javax.swing.JPanel();
        names_TitleLabel = new javax.swing.JLabel();
        names_analysisNameLabel = new javax.swing.JLabel();
        names_numberOfSlidesLabel = new javax.swing.JLabel();
        names_analysisNameTextField = new javax.swing.JTextField();
        names_numberOfSlidesSpinner = new javax.swing.JSpinner();
        names_loadButton = new javax.swing.JButton();
        intraArrayNormalisationPanel = new javax.swing.JPanel();
        intraArrayNormalisation_titleLabel = new javax.swing.JLabel();
        interArrayNormalisation_mergeReplicatesCheckBox = new javax.swing.JCheckBox();
        intraArrayNormalisation_medianRadioButton = new javax.swing.JRadioButton();
        intraArrayNormalisation_lowessRadioButton = new javax.swing.JRadioButton();
        intraArrayNormalisation_vsnRadioButton = new javax.swing.JRadioButton();
        intraArrayNormalisation_pengRadioButton = new javax.swing.JRadioButton();
        intraArrayNormalisation_separator = new javax.swing.JSeparator();
        bgCorrectionMethodLabel = new javax.swing.JLabel();
        bgCorrectionMethodComboBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        intraArrayNormalisation_explainEditorPane = new javax.swing.JEditorPane();
        interArrayNormalisationPanel = new javax.swing.JPanel();
        interArrayNormalisation_titleLabel = new javax.swing.JLabel();
        interArrayNormalisation_normalisationLabel = new javax.swing.JLabel();
        interArrayNormalisation_noneRadioButton = new javax.swing.JRadioButton();
        interArrayNormalisation_medianRadioButton = new javax.swing.JRadioButton();
        interArrayNormalisation_quantileRadioButton = new javax.swing.JRadioButton();
        interArrayNormalisation_meanRadioButton = new javax.swing.JRadioButton();
        interArrayNormalisation_agilentRadioButton = new javax.swing.JRadioButton();
        interArrayNormalisation_separator = new javax.swing.JSeparator();
        interArrayNormalisation_normalisationLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        interArrayNormalisation_explainEditorPane = new javax.swing.JEditorPane();
        peakPanel = new javax.swing.JPanel();
        peak_titleLabel = new javax.swing.JLabel();
        peak_distanceLabel = new javax.swing.JLabel();
        peak_bindingPeakLabel = new javax.swing.JLabel();
        peak_bindingPeakMeanLabel = new javax.swing.JLabel();
        peak_bindingPeakStddevLabel = new javax.swing.JLabel();
        peak_bindingExtendLabel = new javax.swing.JLabel();
        peak_bindingExtendMeanLabel = new javax.swing.JLabel();
        peak_bindingExtendStddevLabel = new javax.swing.JLabel();
        peak_pValuePeakLabel = new javax.swing.JLabel();
        peak_pValueExtendLabel = new javax.swing.JLabel();
        peak_distanceTextField = new javax.swing.JTextField();
        peak_pValueExtendTextField = new javax.swing.JTextField();
        peak_pValuePeakTextField = new javax.swing.JTextField();
        peak_bindingExtendMeanTextField = new javax.swing.JTextField();
        peak_bindingPeakMeanTextField = new javax.swing.JTextField();
        peak_bindingPeakStddevTextField = new javax.swing.JTextField();
        peak_bindingExtendStddevTextField = new javax.swing.JTextField();
        peak_bindingCheckBox = new javax.swing.JCheckBox();
        peak_pValueCheckBox = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        outputPanel = new javax.swing.JPanel();
        output_titleLabel = new javax.swing.JLabel();
        output_formatLabel = new javax.swing.JLabel();
        output_gffCheckBox = new javax.swing.JCheckBox();
        output_sgrCheckBox = new javax.swing.JCheckBox();
        output_bedCheckBox = new javax.swing.JCheckBox();
        output_outputDirectoryLabel = new javax.swing.JLabel();
        output_outputDirectoryTextField = new javax.swing.JTextField();
        output_browseButton = new javax.swing.JButton();
        summaryPanel = new javax.swing.JPanel();
        summary_title = new javax.swing.JLabel();
        summary_experimentNameLabel = new javax.swing.JLabel();
        summary_outputDirectoryLabel = new javax.swing.JLabel();
        summary_numberOfSlidesLabel = new javax.swing.JLabel();
        summary_experimentTextField = new javax.swing.JTextField();
        summary_outputDirectoryTextField = new javax.swing.JTextField();
        summary_numberOfSlidesTextField = new javax.swing.JTextField();
        summary_saveButton = new javax.swing.JButton();

        slideCreationDialog.setTitle("Slide creation...");
        slideCreationDialog.setAlwaysOnTop(true);
        slideCreationDialog.setBounds(new java.awt.Rectangle(100, 100, 200, 50));
        slideCreationDialog.setResizable(false);

        org.jdesktop.layout.GroupLayout slideCreationPanelLayout = new org.jdesktop.layout.GroupLayout(slideCreationPanel);
        slideCreationPanel.setLayout(slideCreationPanelLayout);
        slideCreationPanelLayout.setHorizontalGroup(
            slideCreationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(slideCreation_progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );
        slideCreationPanelLayout.setVerticalGroup(
            slideCreationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(slideCreation_progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout slideCreationDialogLayout = new org.jdesktop.layout.GroupLayout(slideCreationDialog.getContentPane());
        slideCreationDialog.getContentPane().setLayout(slideCreationDialogLayout);
        slideCreationDialogLayout.setHorizontalGroup(
            slideCreationDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(slideCreationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        slideCreationDialogLayout.setVerticalGroup(
            slideCreationDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(slideCreationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("COCAS wizard");
        setBounds(new java.awt.Rectangle(50, 50, 500, 440));
        setName("wizard"); // NOI18N
        setResizable(false);

        wizard_cancelButton.setText("Cancel");
        wizard_cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                wizard_cancelButtonMouseReleased(evt);
            }
        });

        wizard_nextButton.setText("Next >");
        wizard_nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                wizard_nextButtonMouseReleased(evt);
            }
        });

        wizard_backButton.setText("< Back");
        wizard_backButton.setEnabled(false);
        wizard_backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                wizard_backButtonMouseReleased(evt);
            }
        });

        wizard_imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wizard_imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/data/cluster.png"))); // NOI18N
        wizard_imageLabel.setText("jLabel6");
        wizard_imageLabel.setMaximumSize(new java.awt.Dimension(100, 350));
        wizard_imageLabel.setMinimumSize(new java.awt.Dimension(100, 350));
        wizard_imageLabel.setPreferredSize(new java.awt.Dimension(100, 350));

        wizardInnerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        wizardInnerPanel.setMaximumSize(new java.awt.Dimension(350, 350));
        wizardInnerPanel.setMinimumSize(new java.awt.Dimension(350, 350));
        wizardInnerPanel.setPreferredSize(new java.awt.Dimension(350, 350));
        wizardInnerPanel.setLayout(new java.awt.CardLayout());

        namesPanel.setName("namesPanel"); // NOI18N

        names_TitleLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        names_TitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        names_TitleLabel.setText("New COCAS pipeline analysis");

        names_analysisNameLabel.setText("Analysis name");
        names_analysisNameLabel.setToolTipText("The way you want to name the analysis of your experiment.");

        names_numberOfSlidesLabel.setText("Number of slides");
        names_numberOfSlidesLabel.setToolTipText("An experiment is considered to be a slide plus its potential replicates");

        names_analysisNameTextField.setToolTipText("The way you want to name the analysis of your experiment.");

        names_numberOfSlidesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        names_numberOfSlidesSpinner.setToolTipText("An experiment is considered to be a slide plus his potential replicates");

        names_loadButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        names_loadButton.setText("Load parameters");
        names_loadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                names_loadButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout namesPanelLayout = new org.jdesktop.layout.GroupLayout(namesPanel);
        namesPanel.setLayout(namesPanelLayout);
        namesPanelLayout.setHorizontalGroup(
            namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, names_TitleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(namesPanelLayout.createSequentialGroup()
                        .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(names_analysisNameLabel)
                            .add(names_numberOfSlidesLabel))
                        .add(18, 18, 18)
                        .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(names_analysisNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(names_numberOfSlidesSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, names_loadButton))
                .addContainerGap())
        );
        namesPanelLayout.setVerticalGroup(
            namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(namesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(names_TitleLabel)
                .add(30, 30, 30)
                .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(names_analysisNameLabel)
                    .add(names_analysisNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(namesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(names_numberOfSlidesLabel)
                    .add(names_numberOfSlidesSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 193, Short.MAX_VALUE)
                .add(names_loadButton)
                .addContainerGap())
        );

        wizardInnerPanel.add(namesPanel, "card6");

        intraArrayNormalisation_titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        intraArrayNormalisation_titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intraArrayNormalisation_titleLabel.setText("Intra-array normalisation");

        interArrayNormalisation_mergeReplicatesCheckBox.setText("Multiple Slide Design");
        interArrayNormalisation_mergeReplicatesCheckBox.setToolTipText("Check this box if your design comprises more than one slide");
        interArrayNormalisation_mergeReplicatesCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                interArrayNormalisation_mergeReplicatesCheckBoxMouseReleased(evt);
            }
        });

        intraArrayNormalisation_buttonGroup.add(intraArrayNormalisation_medianRadioButton);
        intraArrayNormalisation_medianRadioButton.setSelected(true);
        intraArrayNormalisation_medianRadioButton.setText("Median");
        intraArrayNormalisation_medianRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                intraArrayNormalisation_medianRadioButtonMouseReleased(evt);
            }
        });

        intraArrayNormalisation_buttonGroup.add(intraArrayNormalisation_lowessRadioButton);
        intraArrayNormalisation_lowessRadioButton.setText("Lowess");
        intraArrayNormalisation_lowessRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                intraArrayNormalisation_lowessRadioButtonMouseReleased(evt);
            }
        });

        intraArrayNormalisation_buttonGroup.add(intraArrayNormalisation_vsnRadioButton);
        intraArrayNormalisation_vsnRadioButton.setText("Variance Stabilisation (VSN)");
        intraArrayNormalisation_vsnRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                intraArrayNormalisation_vsnRadioButtonMouseReleased(evt);
            }
        });

        intraArrayNormalisation_buttonGroup.add(intraArrayNormalisation_pengRadioButton);
        intraArrayNormalisation_pengRadioButton.setText("Peng et al.");
        intraArrayNormalisation_pengRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                intraArrayNormalisation_pengRadioButtonMouseReleased(evt);
            }
        });

        bgCorrectionMethodLabel.setText("Background Correction Method");

        bgCorrectionMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Subtract", "Half", "Minimum", "Moving Minimum", "Edwards", "NormExp", "RMA" }));
        bgCorrectionMethodComboBox.setToolTipText("Recommended default setting : Subtract");

        jScrollPane2.setBorder(null);

        intraArrayNormalisation_explainEditorPane.setBackground(java.awt.SystemColor.control);
        intraArrayNormalisation_explainEditorPane.setBorder(null);
        intraArrayNormalisation_explainEditorPane.setEditable(false);
        jScrollPane2.setViewportView(intraArrayNormalisation_explainEditorPane);

        org.jdesktop.layout.GroupLayout intraArrayNormalisationPanelLayout = new org.jdesktop.layout.GroupLayout(intraArrayNormalisationPanel);
        intraArrayNormalisationPanel.setLayout(intraArrayNormalisationPanelLayout);
        intraArrayNormalisationPanelLayout.setHorizontalGroup(
            intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, intraArrayNormalisationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, intraArrayNormalisationPanelLayout.createSequentialGroup()
                        .add(intraArrayNormalisation_lowessRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 287, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, intraArrayNormalisationPanelLayout.createSequentialGroup()
                        .add(intraArrayNormalisation_vsnRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 191, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, intraArrayNormalisationPanelLayout.createSequentialGroup()
                        .add(intraArrayNormalisation_pengRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 271, Short.MAX_VALUE))
                    .add(intraArrayNormalisation_titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, intraArrayNormalisation_separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .add(intraArrayNormalisationPanelLayout.createSequentialGroup()
                        .add(intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(intraArrayNormalisation_medianRadioButton)
                            .add(bgCorrectionMethodLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 79, Short.MAX_VALUE)
                        .add(intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(bgCorrectionMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(interArrayNormalisation_mergeReplicatesCheckBox))))
                .add(16, 16, 16))
        );
        intraArrayNormalisationPanelLayout.setVerticalGroup(
            intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(intraArrayNormalisationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(intraArrayNormalisation_titleLabel)
                .add(18, 18, 18)
                .add(intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(intraArrayNormalisation_medianRadioButton)
                    .add(interArrayNormalisation_mergeReplicatesCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(intraArrayNormalisation_lowessRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(intraArrayNormalisation_vsnRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(intraArrayNormalisation_pengRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(intraArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bgCorrectionMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bgCorrectionMethodLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(intraArrayNormalisation_separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        wizardInnerPanel.add(intraArrayNormalisationPanel, "card5");

        interArrayNormalisation_titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        interArrayNormalisation_titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        interArrayNormalisation_titleLabel.setText("Inter-array normalisation");

        interArrayNormalisation_normalisationLabel.setText("Normalisation type");

        interArrayNormalisation_buttonGroup.add(interArrayNormalisation_noneRadioButton);
        interArrayNormalisation_noneRadioButton.setSelected(true);
        interArrayNormalisation_noneRadioButton.setText("None");

        interArrayNormalisation_buttonGroup.add(interArrayNormalisation_medianRadioButton);
        interArrayNormalisation_medianRadioButton.setText("Median");
        interArrayNormalisation_medianRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                interArrayNormalisation_medianRadioButtonMouseReleased(evt);
            }
        });

        interArrayNormalisation_buttonGroup.add(interArrayNormalisation_quantileRadioButton);
        interArrayNormalisation_quantileRadioButton.setText("Quantile");

        mergeReplicates_buttonGroup.add(interArrayNormalisation_meanRadioButton);
        interArrayNormalisation_meanRadioButton.setSelected(true);
        interArrayNormalisation_meanRadioButton.setText("Mean");

        mergeReplicates_buttonGroup.add(interArrayNormalisation_agilentRadioButton);
        interArrayNormalisation_agilentRadioButton.setText("Roberts et al.");

        interArrayNormalisation_normalisationLabel1.setText("Replicate merge method");

        jScrollPane1.setBorder(null);

        interArrayNormalisation_explainEditorPane.setBackground(java.awt.SystemColor.control);
        interArrayNormalisation_explainEditorPane.setBorder(null);
        interArrayNormalisation_explainEditorPane.setEditable(false);
        jScrollPane1.setViewportView(interArrayNormalisation_explainEditorPane);

        org.jdesktop.layout.GroupLayout interArrayNormalisationPanelLayout = new org.jdesktop.layout.GroupLayout(interArrayNormalisationPanel);
        interArrayNormalisationPanel.setLayout(interArrayNormalisationPanelLayout);
        interArrayNormalisationPanelLayout.setHorizontalGroup(
            interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, interArrayNormalisationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(interArrayNormalisation_separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, interArrayNormalisation_titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, interArrayNormalisationPanelLayout.createSequentialGroup()
                        .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(interArrayNormalisation_normalisationLabel)
                            .add(interArrayNormalisation_noneRadioButton)
                            .add(interArrayNormalisation_medianRadioButton)
                            .add(interArrayNormalisation_quantileRadioButton))
                        .add(62, 62, 62)
                        .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(interArrayNormalisation_normalisationLabel1)
                            .add(interArrayNormalisation_meanRadioButton)
                            .add(interArrayNormalisation_agilentRadioButton))
                        .add(5, 5, 5)))
                .addContainerGap())
        );
        interArrayNormalisationPanelLayout.setVerticalGroup(
            interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interArrayNormalisationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(interArrayNormalisation_titleLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interArrayNormalisation_normalisationLabel)
                    .add(interArrayNormalisation_normalisationLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interArrayNormalisation_noneRadioButton)
                    .add(interArrayNormalisation_meanRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interArrayNormalisationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interArrayNormalisation_medianRadioButton)
                    .add(interArrayNormalisation_agilentRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interArrayNormalisation_quantileRadioButton)
                .add(18, 18, 18)
                .add(interArrayNormalisation_separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addContainerGap())
        );

        wizardInnerPanel.add(interArrayNormalisationPanel, "card7");

        peakPanel.setName("peakPanel"); // NOI18N

        peak_titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        peak_titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        peak_titleLabel.setText("Peak detection");

        peak_distanceLabel.setText("Distance threshold");
        peak_distanceLabel.setToolTipText("The distance beyond which two probes are not considered as neighbours.");

        peak_bindingPeakLabel.setText("Peak threshold");
        peak_bindingPeakLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a peak probe.");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingPeakLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingPeakMeanLabel.setText("x Mean +");
        peak_bindingPeakMeanLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingPeakMeanLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingPeakStddevLabel.setText("x Std deviation");
        peak_bindingPeakStddevLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingPeakStddevLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingExtendLabel.setText("Extend threshold");
        peak_bindingExtendLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingExtendLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingExtendMeanLabel.setText("x Mean +");
        peak_bindingExtendMeanLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingExtendMeanLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingExtendStddevLabel.setText("x Std deviation");
        peak_bindingExtendStddevLabel.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingExtendStddevLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_pValuePeakLabel.setText("Peak threshold");
        peak_pValuePeakLabel.setToolTipText("The threshold of p-value beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_pValueCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_pValuePeakLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_pValueExtendLabel.setText("Extend threshold");
        peak_pValueExtendLabel.setToolTipText("The threshold of p-value beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_pValueCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_pValueExtendLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_distanceTextField.setText("650");
        peak_distanceTextField.setToolTipText("The distance beyond which two probes are not considered as neighbours.");

        peak_pValueExtendTextField.setToolTipText("The threshold of p-value beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_pValueCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_pValueExtendTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_pValuePeakTextField.setToolTipText("The threshold of p-value beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_pValueCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_pValuePeakTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingExtendMeanTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        peak_bindingExtendMeanTextField.setText("1");
        peak_bindingExtendMeanTextField.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingExtendMeanTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingPeakMeanTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        peak_bindingPeakMeanTextField.setText("1");
        peak_bindingPeakMeanTextField.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingPeakMeanTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingPeakStddevTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        peak_bindingPeakStddevTextField.setText("2");
        peak_bindingPeakStddevTextField.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingPeakStddevTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingExtendStddevTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        peak_bindingExtendStddevTextField.setText("1");
        peak_bindingExtendStddevTextField.setToolTipText("The threshold of binding ratio beyond which a probe will be considered as a valid neighbour of a peak probe.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, peak_bindingCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), peak_bindingExtendStddevTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        peak_bindingCheckBox.setSelected(true);
        peak_bindingCheckBox.setText("Binding ratio threshold");
        peak_bindingCheckBox.setToolTipText("Activate the binding ratio thresholds for the peak detection.");

        peak_pValueCheckBox.setText("p-Value threshold");
        peak_pValueCheckBox.setToolTipText("Activate the p-value thresholds for the peak detection.");

        jCheckBox1.setText("Ringo threshold");

        jLabel1.setText("Percentile");

        jTextField1.setText("0.99");

        jSpinner1.setValue(1);

        jLabel2.setText("Window");

        org.jdesktop.layout.GroupLayout peakPanelLayout = new org.jdesktop.layout.GroupLayout(peakPanel);
        peakPanel.setLayout(peakPanelLayout);
        peakPanelLayout.setHorizontalGroup(
            peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(peakPanelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(peakPanelLayout.createSequentialGroup()
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(peak_bindingCheckBox)
                                .add(peak_pValueCheckBox))
                            .addContainerGap(203, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, peakPanelLayout.createSequentialGroup()
                            .add(peak_titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                            .addContainerGap())
                        .add(peakPanelLayout.createSequentialGroup()
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(peak_distanceLabel)
                                    .add(peakPanelLayout.createSequentialGroup()
                                        .add(21, 21, 21)
                                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(peak_pValueExtendLabel)
                                            .add(peak_pValuePeakLabel)))
                                    .add(peakPanelLayout.createSequentialGroup()
                                        .add(21, 21, 21)
                                        .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(peak_bindingExtendLabel)
                                            .add(peak_bindingPeakLabel))))
                                .add(jCheckBox1)
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(peak_pValueExtendTextField)
                                .add(peak_pValuePeakTextField)
                                .add(peak_distanceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                .add(peakPanelLayout.createSequentialGroup()
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, peak_bindingExtendMeanTextField)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, peak_bindingPeakMeanTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(peak_bindingExtendMeanLabel)
                                        .add(peak_bindingPeakMeanLabel)))
                                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(peak_bindingExtendStddevTextField)
                                .add(peak_bindingPeakStddevTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                            .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(peak_bindingPeakStddevLabel)
                                .add(peak_bindingExtendStddevLabel))
                            .add(27, 27, 27)))))
        );
        peakPanelLayout.setVerticalGroup(
            peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peakPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(peak_titleLabel)
                .add(18, 18, 18)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_distanceLabel)
                    .add(peak_distanceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peak_bindingCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_bindingPeakLabel)
                    .add(peak_bindingPeakMeanTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(peak_bindingPeakMeanLabel)
                    .add(peak_bindingPeakStddevTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(peak_bindingPeakStddevLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_bindingExtendLabel)
                    .add(peak_bindingExtendMeanTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(peak_bindingExtendMeanLabel)
                    .add(peak_bindingExtendStddevTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(peak_bindingExtendStddevLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peak_pValueCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_pValuePeakLabel)
                    .add(peak_pValuePeakTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peak_pValueExtendLabel)
                    .add(peak_pValueExtendTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peakPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        wizardInnerPanel.add(peakPanel, "card4");

        output_titleLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        output_titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        output_titleLabel.setText("Output");

        output_formatLabel.setText("Output files format");

        output_gffCheckBox.setSelected(true);
        output_gffCheckBox.setText("GFF");
        output_gffCheckBox.setToolTipText("Format used :\n<Chromosom>   <Source>   <Feature>   <Start>   <End>   <Score>   <Strand>   <Frame>   [Attributes]   [Comments]");

        output_sgrCheckBox.setSelected(true);
        output_sgrCheckBox.setText("SGR");
        output_sgrCheckBox.setToolTipText("SGR format description");

        output_bedCheckBox.setText("BED");
        output_bedCheckBox.setToolTipText("BED format description");

        output_outputDirectoryLabel.setText("Output directory");

        output_outputDirectoryTextField.setText("c:/COCAS");

        output_browseButton.setText("Browse");
        output_browseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                output_browseButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout outputPanelLayout = new org.jdesktop.layout.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputPanelLayout.createSequentialGroup()
                .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(output_outputDirectoryLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(output_outputDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(output_browseButton))
                    .add(outputPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(output_gffCheckBox)
                            .add(output_formatLabel)
                            .add(output_sgrCheckBox)
                            .add(output_bedCheckBox)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, outputPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(output_titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)))
                .addContainerGap())
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(output_titleLabel)
                .add(18, 18, 18)
                .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(output_outputDirectoryLabel)
                    .add(output_browseButton)
                    .add(output_outputDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(output_formatLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(output_gffCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(output_sgrCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(output_bedCheckBox)
                .addContainerGap(169, Short.MAX_VALUE))
        );

        wizardInnerPanel.add(outputPanel, "card3");

        summaryPanel.setName("summaryPanel"); // NOI18N

        summary_title.setFont(new java.awt.Font("Tahoma", 0, 18));
        summary_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        summary_title.setText("Summary");

        summary_experimentNameLabel.setText("Experiment name");

        summary_outputDirectoryLabel.setText("Output directory");

        summary_numberOfSlidesLabel.setText("Number of slides");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, names_analysisNameTextField, org.jdesktop.beansbinding.ELProperty.create("${text}"), summary_experimentTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, output_outputDirectoryTextField, org.jdesktop.beansbinding.ELProperty.create("${text}"), summary_outputDirectoryTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        summary_numberOfSlidesTextField.setEditable(false);
        summary_numberOfSlidesTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, names_numberOfSlidesSpinner, org.jdesktop.beansbinding.ELProperty.create("${value}"), summary_numberOfSlidesTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        summary_saveButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        summary_saveButton.setText("Save parameters");
        summary_saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                summary_saveButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout summaryPanelLayout = new org.jdesktop.layout.GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(
            summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(summaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(summaryPanelLayout.createSequentialGroup()
                        .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(summary_experimentNameLabel)
                            .add(summary_numberOfSlidesLabel)
                            .add(summary_outputDirectoryLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(summary_numberOfSlidesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(summary_outputDirectoryTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                            .add(summary_experimentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, summary_title, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, summary_saveButton))
                .addContainerGap())
        );
        summaryPanelLayout.setVerticalGroup(
            summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(summaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(summary_title)
                .add(18, 18, 18)
                .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(summary_experimentNameLabel)
                    .add(summary_experimentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(summary_outputDirectoryLabel)
                    .add(summary_outputDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(summaryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(summary_numberOfSlidesLabel)
                    .add(summary_numberOfSlidesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 191, Short.MAX_VALUE)
                .add(summary_saveButton)
                .addContainerGap())
        );

        wizardInnerPanel.add(summaryPanel, "card2");

        org.jdesktop.layout.GroupLayout wizardMainPanelLayout = new org.jdesktop.layout.GroupLayout(wizardMainPanel);
        wizardMainPanel.setLayout(wizardMainPanelLayout);
        wizardMainPanelLayout.setHorizontalGroup(
            wizardMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wizardMainPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(wizard_imageLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(wizardInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, wizardMain_separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, wizardMainPanelLayout.createSequentialGroup()
                .addContainerGap(261, Short.MAX_VALUE)
                .add(wizard_backButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wizard_nextButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wizard_cancelButton)
                .addContainerGap())
        );
        wizardMainPanelLayout.setVerticalGroup(
            wizardMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wizardMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(wizardMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(wizardInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(wizard_imageLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wizardMain_separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wizardMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(wizard_cancelButton)
                    .add(wizard_nextButton)
                    .add(wizard_backButton))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wizardMainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wizardMainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void wizard_cancelButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizard_cancelButtonMouseReleased
    System.out.println("Cancel");
    this.dispose();
    
}//GEN-LAST:event_wizard_cancelButtonMouseReleased

private void wizard_nextButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizard_nextButtonMouseReleased
    
    if(wizard_nextButton.getText().equals("Run"))
    {
        wizardComplete=true;
        this.dispose();
    }
    
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if(wizard_nextButton.isEnabled()){
        
        System.out.println("Next");
        if(namesPanel.isVisible() && wizardInnerPanel.getComponentCount() != 6+(Integer)names_numberOfSlidesSpinner.getValue()){
            wizard_nextButton.setEnabled(false);
            slideCreationDialog.setVisible(true);
            slideCreation_progressBar.setVisible(true);
            slideCreation_progressBar.setIndeterminate(false);
            slideCreation_progressBar.setMaximum((Integer)names_numberOfSlidesSpinner.getValue());
            slideCreation_progressBar.setMaximum(0);
            slideCreation_progressBar.setValue(0);
            
            slideCreation_progressBar.setStringPainted(true);
            slideCreationPanel.update(slideCreationPanel.getGraphics());
            SlidePanel newSlidePanel;
            for(int i=wizardInnerPanel.getComponentCount()-6;i < (Integer)names_numberOfSlidesSpinner.getValue();i++){
                System.out.println("Insertion panel n°"+(i+1));
                newSlidePanel = new SlidePanel();
                newSlidePanel.setTitle("Experiment n°"+(i+1));
                wizardInnerPanel.add(newSlidePanel, "experiment"+(i+1), wizardInnerPanel.getComponentCount()-5);
                slideCreation_progressBar.setValue(i+1);
                slideCreationPanel.update(slideCreationPanel.getGraphics());
            }
            slideCreationDialog.setVisible(false);
            wizard_nextButton.setEnabled(true);
            
        }
        cardLayout.next(wizardInnerPanel);
    } 
    if(summaryPanel.isVisible()){
        wizard_nextButton.setText("Run");
        wizard_backButton.setEnabled(true);
        
    }
    else{
        wizard_nextButton.setText("Next >");
        wizard_nextButton.setEnabled(true);
        wizard_backButton.setEnabled(true);
    }
    setCursor(null);
}//GEN-LAST:event_wizard_nextButtonMouseReleased

private void wizard_backButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizard_backButtonMouseReleased
if(wizard_backButton.isEnabled()){
        System.out.println("Back");
        cardLayout.previous(wizardInnerPanel);
    }
    if(namesPanel.isVisible()){
        wizard_nextButton.setText("Next >");
        wizard_nextButton.setEnabled(true);
        wizard_backButton.setEnabled(false);
    }
    else{
        wizard_nextButton.setText("Next >");
        wizard_nextButton.setEnabled(true);
        wizard_backButton.setEnabled(true);
    }
}//GEN-LAST:event_wizard_backButtonMouseReleased

private void intraArrayNormalisation_medianRadioButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intraArrayNormalisation_medianRadioButtonMouseReleased
if(intraArrayNormalisation_medianRadioButton.isSelected()){
        intraArrayNormalisation_explainEditorPane.removeHyperlinkListener(listener); 
        intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>Global normalization assumes that the red and green intensities are related by a constant factor, i.e. R = kG, and the center of the distribution of log ratios is shifted to zero"

                + "<br><br>log2R/G -> log2R/G – c = log2R/(kG)<br><br> where c = log2k is the median. See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/11473024\">Zien A, Aigner T, Zimmer R, Lengauer T. Centralization: a new method for the normalization of gene expression data. Bioinformatics. 2001;17 Suppl 1:S323-31.</a> for details</body></html>");
         intraArrayNormalisation_explainEditorPane.addHyperlinkListener(listener = new WizardHyperLinkListener("http://www.ncbi.nlm.nih.gov/pubmed/17592629"));
        
        
}

}//GEN-LAST:event_intraArrayNormalisation_medianRadioButtonMouseReleased

private void intraArrayNormalisation_lowessRadioButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intraArrayNormalisation_lowessRadioButtonMouseReleased
if(intraArrayNormalisation_lowessRadioButton.isSelected()){
    intraArrayNormalisation_explainEditorPane.removeHyperlinkListener(listener);    
    intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>Lowess (Locally weighted scatter plot smoothing) intensity-dependent normalization performs a fit of the data by subtracting a linear regression curve. <br><br>log2R/G -> log2R/G – c(A) = log2R/[k(A)G] <br><br>where c(A) is the lowess fit to the MA-plot. " +
                "See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/11473024\">Yang YH, Dudoit S, Luu P, Lin DM, Peng V, Ngai J, Speed TP. Normalization for cDNA microarray data: a robust composite method addressing single and multiple slide systematic variation. Nucleic Acids Res. 2002 Feb 15;30(4):e15.</a> for details</body></html>");
                intraArrayNormalisation_explainEditorPane.addHyperlinkListener(listener = new WizardHyperLinkListener("http://www.ncbi.nlm.nih.gov/pubmed/17592629"));
                intraArrayNormalisation_explainEditorPane.setCaretPosition(0);
}
}//GEN-LAST:event_intraArrayNormalisation_lowessRadioButtonMouseReleased

private void intraArrayNormalisation_vsnRadioButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intraArrayNormalisation_vsnRadioButtonMouseReleased
if(intraArrayNormalisation_vsnRadioButton.isSelected()){
    intraArrayNormalisation_explainEditorPane.removeHyperlinkListener(listener);    
    intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>The Variance Stabilisation Normalisation (V.S.N.)" +
                " method builds upon the fact that the variance of microarray data depends on the" +
                " signal intensity and that a transformation can be found after which the variance" +
                " is approximately constant. See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/12169536\">Huber W, von Heydebreck A, Sültmann H, Poustka A, Vingron M. Variance stabilization applied to microarray data calibration and to the quantification of differential expression. Bioinformatics. 2002;18 Suppl 1:S96-104.</a> for details</body></html>");
                intraArrayNormalisation_explainEditorPane.addHyperlinkListener(listener = new WizardHyperLinkListener("http://www.ncbi.nlm.nih.gov/pubmed/12169536"));
}

}//GEN-LAST:event_intraArrayNormalisation_vsnRadioButtonMouseReleased

private void intraArrayNormalisation_pengRadioButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intraArrayNormalisation_pengRadioButtonMouseReleased
if(intraArrayNormalisation_pengRadioButton.isSelected()){
        intraArrayNormalisation_explainEditorPane.removeHyperlinkListener(listener);    
        intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>This normalization uses signal enrichment rotation against intensity according to an angle estimated by PCA, then applies a weighted loess normalization. See <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/17592629\">Peng S, Alekseyenko AA, Larschan E, Kuroda MI, Park PJ. Normalization and experimental design for ChIP-chip data. BMC Bioinformatics. 2007 Jun 25;8:219.</a> for details</body></html>");
        intraArrayNormalisation_explainEditorPane.addHyperlinkListener(listener = new WizardHyperLinkListener("http://www.ncbi.nlm.nih.gov/pubmed/17592629"));
}
}//GEN-LAST:event_intraArrayNormalisation_pengRadioButtonMouseReleased

private void output_browseButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_output_browseButtonMouseReleased
    int returnVal = directoryChooser.showOpenDialog(wizardInnerPanel);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
       File outputDirectory = directoryChooser.getSelectedFile();
       output_outputDirectoryTextField.setText(outputDirectory.getAbsolutePath());
    }
}//GEN-LAST:event_output_browseButtonMouseReleased

private void summary_saveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_summary_saveButtonMouseReleased
    try {
        int returnVal = fileChooser.showOpenDialog(wizardInnerPanel);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           File outputFile = fileChooser.getSelectedFile();
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           saveParametersFile(outputFile);
           setCursor(null);
        }
    } 
    catch(Exception e){
        String error = "";
        StackTraceElement[] ligne = e.getStackTrace();
        error += ""+e.getMessage();
        for(int i=0;i<ligne.length;i++){
            error += "File: "+ligne[i].getFileName()+"  ";
            error += "Class: "+ligne[i].getClassName()+"\n";
            error += "Method: " +ligne[i].getMethodName()+"  ";
            error += "line: "+ligne[i].getLineNumber()+"\n";
        }
        JOptionPane.showMessageDialog(wizardInnerPanel, "Java error:\n"+error, "Java error", JOptionPane.ERROR_MESSAGE);
        setCursor(null);
        return;
    }
}//GEN-LAST:event_summary_saveButtonMouseReleased

private void names_loadButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_names_loadButtonMouseReleased
    File inputFile;
    
    if(fileChooser.showOpenDialog(wizardInnerPanel) == JFileChooser.APPROVE_OPTION) {
        inputFile = fileChooser.getSelectedFile();
        if(!inputFile.exists()){
            JOptionPane.showMessageDialog(wizardInnerPanel, "Specified parameters file\n'"+inputFile.getAbsolutePath()+"'\nnot found !", "File not found !", JOptionPane.ERROR_MESSAGE);
            setCursor(null);
            return;
        }
        try{
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loadParametersFile(inputFile);
            setCursor(null);
        }
        catch(Exception e){
            String error = "";
            StackTraceElement[] ligne = e.getStackTrace();
            error += ""+e.getMessage();
            for(int i=0;i<ligne.length;i++){
                error += "File: "+ligne[i].getFileName()+"  ";
                error += "Class: "+ligne[i].getClassName()+"\n";
                error += "Method: " +ligne[i].getMethodName()+"  ";
                error += "line: "+ligne[i].getLineNumber()+"\n";
            }
            JOptionPane.showMessageDialog(wizardInnerPanel, "Java error:\n"+error, "Java error", JOptionPane.ERROR_MESSAGE);
            setCursor(null);
            return;
        }
    }
}//GEN-LAST:event_names_loadButtonMouseReleased

private void interArrayNormalisation_mergeReplicatesCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_interArrayNormalisation_mergeReplicatesCheckBoxMouseReleased
    // TODO add your handling code here:
    if(interArrayNormalisation_mergeReplicatesCheckBox.isSelected()) {
        intraArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>This option causes all slides entered as one experiment to be treated as replicates of part of a multi-slide design.<br><br>Example:<br><br>251471611301 : chr1-10 Replicate 1 as slide 1<br>251471611302 : chr1-10 Replicate 2 as slide 1<br>251471711301 : chr11-Y Replicate 1 as slide 2<br>251471711302 : chr10-Y Replicate 2 as slide 2<br></body></html>");
    }
}//GEN-LAST:event_interArrayNormalisation_mergeReplicatesCheckBoxMouseReleased

private void interArrayNormalisation_medianRadioButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_interArrayNormalisation_medianRadioButtonMouseReleased
    // TODO add your handling code here:
    if(interArrayNormalisation_medianRadioButton.isSelected()){
        interArrayNormalisation_explainEditorPane.setText("<html><head>" +
                "<style type=\"text/css\">  body {" +
                "    font-size: 11pt;" +
                "    font-family : Tahoma }" +
                "  </style></head>" +
                "<body>The median normalisation simply corrects the data such that all arrays have the same median.</body></html>");
    }
}//GEN-LAST:event_interArrayNormalisation_medianRadioButtonMouseReleased
       

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Wizard dialog = new Wizard(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    public boolean isComplete()
    {
        return wizardComplete;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bgCorrectionMethodComboBox;
    private javax.swing.JLabel bgCorrectionMethodLabel;
    private javax.swing.JFileChooser directoryChooser;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JPanel interArrayNormalisationPanel;
    private javax.swing.JRadioButton interArrayNormalisation_agilentRadioButton;
    private javax.swing.ButtonGroup interArrayNormalisation_buttonGroup;
    private javax.swing.JEditorPane interArrayNormalisation_explainEditorPane;
    private javax.swing.JRadioButton interArrayNormalisation_meanRadioButton;
    private javax.swing.JRadioButton interArrayNormalisation_medianRadioButton;
    private javax.swing.JCheckBox interArrayNormalisation_mergeReplicatesCheckBox;
    private javax.swing.JRadioButton interArrayNormalisation_noneRadioButton;
    private javax.swing.JLabel interArrayNormalisation_normalisationLabel;
    private javax.swing.JLabel interArrayNormalisation_normalisationLabel1;
    private javax.swing.JRadioButton interArrayNormalisation_quantileRadioButton;
    private javax.swing.JSeparator interArrayNormalisation_separator;
    private javax.swing.JLabel interArrayNormalisation_titleLabel;
    private javax.swing.JPanel intraArrayNormalisationPanel;
    private javax.swing.ButtonGroup intraArrayNormalisation_buttonGroup;
    private javax.swing.JEditorPane intraArrayNormalisation_explainEditorPane;
    private javax.swing.JRadioButton intraArrayNormalisation_lowessRadioButton;
    private javax.swing.JRadioButton intraArrayNormalisation_medianRadioButton;
    private javax.swing.JRadioButton intraArrayNormalisation_pengRadioButton;
    private javax.swing.JSeparator intraArrayNormalisation_separator;
    private javax.swing.JLabel intraArrayNormalisation_titleLabel;
    private javax.swing.JRadioButton intraArrayNormalisation_vsnRadioButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.ButtonGroup mergeReplicates_buttonGroup;
    private javax.swing.JPanel namesPanel;
    private javax.swing.JLabel names_TitleLabel;
    private javax.swing.JLabel names_analysisNameLabel;
    private javax.swing.JTextField names_analysisNameTextField;
    private javax.swing.JButton names_loadButton;
    private javax.swing.JLabel names_numberOfSlidesLabel;
    private javax.swing.JSpinner names_numberOfSlidesSpinner;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JCheckBox output_bedCheckBox;
    private javax.swing.JButton output_browseButton;
    private javax.swing.JLabel output_formatLabel;
    private javax.swing.JCheckBox output_gffCheckBox;
    private javax.swing.JLabel output_outputDirectoryLabel;
    private javax.swing.JTextField output_outputDirectoryTextField;
    private javax.swing.JCheckBox output_sgrCheckBox;
    private javax.swing.JLabel output_titleLabel;
    private javax.swing.JPanel peakPanel;
    private javax.swing.JCheckBox peak_bindingCheckBox;
    private javax.swing.JLabel peak_bindingExtendLabel;
    private javax.swing.JLabel peak_bindingExtendMeanLabel;
    private javax.swing.JTextField peak_bindingExtendMeanTextField;
    private javax.swing.JLabel peak_bindingExtendStddevLabel;
    private javax.swing.JTextField peak_bindingExtendStddevTextField;
    private javax.swing.JLabel peak_bindingPeakLabel;
    private javax.swing.JLabel peak_bindingPeakMeanLabel;
    private javax.swing.JTextField peak_bindingPeakMeanTextField;
    private javax.swing.JLabel peak_bindingPeakStddevLabel;
    private javax.swing.JTextField peak_bindingPeakStddevTextField;
    private javax.swing.JLabel peak_distanceLabel;
    private javax.swing.JTextField peak_distanceTextField;
    private javax.swing.JCheckBox peak_pValueCheckBox;
    private javax.swing.JLabel peak_pValueExtendLabel;
    private javax.swing.JTextField peak_pValueExtendTextField;
    private javax.swing.JLabel peak_pValuePeakLabel;
    private javax.swing.JTextField peak_pValuePeakTextField;
    private javax.swing.JLabel peak_titleLabel;
    private javax.swing.JDialog slideCreationDialog;
    private javax.swing.JPanel slideCreationPanel;
    private javax.swing.JProgressBar slideCreation_progressBar;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel summary_experimentNameLabel;
    private javax.swing.JTextField summary_experimentTextField;
    private javax.swing.JLabel summary_numberOfSlidesLabel;
    private javax.swing.JTextField summary_numberOfSlidesTextField;
    private javax.swing.JLabel summary_outputDirectoryLabel;
    private javax.swing.JTextField summary_outputDirectoryTextField;
    private javax.swing.JButton summary_saveButton;
    private javax.swing.JLabel summary_title;
    private javax.swing.JPanel wizardInnerPanel;
    private javax.swing.JPanel wizardMainPanel;
    private javax.swing.JSeparator wizardMain_separator;
    private javax.swing.JButton wizard_backButton;
    private javax.swing.JButton wizard_cancelButton;
    private javax.swing.JLabel wizard_imageLabel;
    private javax.swing.JButton wizard_nextButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
