package GFFPeak;

/****************************
 * @author Adrien Jeanniard *
 * @date 29 févr. 2008      *
 ****************************/



import java.io.*;
import GFFLib.*;



public class GFFPeak {
    
    public static int peakDetection(String inputFileName, String outputFileName, int distance, double bindingPeakThreshold, double pValuePeakThreshold, double bindingExtendThreshold, double pValueExtendThreshold) throws Exception{
	int nPeak = 0;
	String ligne = "";
	GFFData currentData;
	GFFRegion region = new GFFRegion();
	BufferedReader inputFile = null;
        FileWriter outputFile = null;
	boolean isPeak = false;
	
	inputFile = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
        outputFile = new FileWriter(outputFileName);
        ligne = inputFile.readLine();
        //On vire les lignes vides ou de commentaire
        while(ligne.startsWith("##") || ligne.equals("")) ligne = inputFile.readLine();
        //Pour chaque ligne non vide du fichier .gff
        while (ligne != null) {
            currentData = new GFFData(ligne);
            //Si la sonde est potentiellement intéressante
            if(currentData.score >= bindingExtendThreshold && currentData.getPValue() <= pValueExtendThreshold ){
                //On regarde si on peut l'intégrer à la région en cours
                if(!region.isEmpty() && currentData.seqname.equals(region.seqname) && (currentData.start - region.end) <= distance){
                    region.add(currentData);
                    if(currentData.score >= bindingPeakThreshold && currentData.getPValue() <= pValuePeakThreshold){
                        isPeak = true;
                    }
                }
                //Sinon on traite la région en cours et on en créé une nouvelle
                else{
                    if(!region.isEmpty() && isPeak && region.size() >= 3){
                        nPeak++;
                        for(int i = 0 ; i < region.gffDatas.size() ; i++) {outputFile.write(region.get(i).toString()+"\tPeak_n°"+nPeak+"\n");System.out.println(region.gffDatas.get(i).toString());}
                    }
                    //On vide la région pour en créer une nouvelle
                    region.clear();
                    isPeak = false;
                    region.add(currentData);
                    if(currentData.score >= bindingPeakThreshold && currentData.getPValue() <= pValuePeakThreshold){
                        isPeak = true;
                    }
                }
            }
            //Si la sonde n'est pas intéressante
            else{
                //Si une région est en cours de construction on la traite
                if(!region.isEmpty() && isPeak && region.size() >= 3)
                {
                    nPeak++;
                    for(int i = 0 ; i < region.gffDatas.size() ; i++) {outputFile.write(region.get(i).toString()+"\tPeak_n°"+nPeak+"\n");System.out.println(region.gffDatas.get(i).toString());}
                }
                //Dans tous les cas on nettoie
                region.clear();
                isPeak = false;
            }
            ligne = inputFile.readLine();
        }
        //Quand on arrive à la fin du fichier on traite la dernière région
        if(!region.isEmpty() && isPeak && region.size() >= 3){
            nPeak++;
            for(int i = 0 ; i < region.gffDatas.size() ; i++){ outputFile.write(region.get(i).toString()+"\tPeak_n°"+nPeak+"\n");System.out.println(region.gffDatas.get(i).toString());}
        }
        //on nettoie
        region.clear();
        isPeak = false;
        inputFile.close();
        outputFile.close();
        return nPeak;
    }
    
    //Proceed peak detection and area calculation in the same time (equivalent to GFFPeak.peakDetection followed by GFFArea.areaCalulation
    public static int GPS(String inputFileName, String outputFileName, int distance, double bindingPeakThreshold, double pValuePeakThreshold, double bindingExtendThreshold, double pValueExtendThreshold, int AREA_TYPE) throws Exception{
	int nPeak = 0;
	String ligne = "";
	GFFData currentData;
	GFFRegion region = new GFFRegion();
	BufferedReader inputFile = null;
        FileWriter outputAreaTabFile = null;
        FileWriter outputAreaGFFFile = null;
        FileWriter outputGFFFile = null;
	boolean isPeak = false;
	
	inputFile = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
        outputGFFFile = new FileWriter(outputFileName);
        outputAreaGFFFile = new FileWriter(outputFileName.replace(".gff", ".area.gff"));
        outputAreaTabFile = new FileWriter(outputFileName.replace(".gff", ".area.txt"));

        outputAreaTabFile.write(GFFRegion.areaFileHeader()+"\n");
        ligne = inputFile.readLine();
        //On vire les lignes vides ou de commentaire
        while(ligne.startsWith("##") || ligne.equals("")) ligne = inputFile.readLine();
        //Pour chaque ligne non vide du fichier .gff
        while (ligne != null) {
            currentData = new GFFData(ligne);
            //Si la sonde est potentiellement intéressante
            if(currentData.score >= bindingExtendThreshold && currentData.getPValue() <= pValueExtendThreshold ){
                //On regarde si on peut l'intégrer à la région en cours
                if(!region.isEmpty() && currentData.seqname.equals(region.seqname) && (currentData.start - region.end) <= distance){
                    region.add(currentData);
                    if(currentData.score >= bindingPeakThreshold && currentData.getPValue() <= pValuePeakThreshold){
                        isPeak = true;
                    }
                }
                //Sinon on traite la région en cours et on en créé une nouvelle
                else{
                    if(!region.isEmpty() && isPeak && region.size() >= 3){
                        nPeak++;
                        
                        region.setOverlapsAtMean();
                        region.peaksAndArea();
                        region.setScore(AREA_TYPE);
                        for(GFFData data : region.gffDatas){
                            outputGFFFile.write(data.toString()+"\tPeak_n°"+nPeak+"\n");
                        }
                        outputAreaTabFile.write(region.toAreaString()+"\tPeak_n°"+nPeak+"\n");
                        outputAreaGFFFile.write(region.toString()+"\tPeak_n°"+nPeak+"\n");
                    }
                    //On vide la région pour en créer une nouvelle
                    region.clear();
                    isPeak = false;
                    region.add(currentData);
                    if(currentData.score >= bindingPeakThreshold && currentData.getPValue() <= pValuePeakThreshold){
                        isPeak = true;
                    }
                }
            }
            //Si la sonde n'est pas intéressante
            else{
                //Si une région est en cours de construction on la traite
                if(!region.isEmpty() && isPeak && region.size() >= 3){
                    nPeak++;
                    region.setOverlapsAtMean();
                    region.peaksAndArea();
                    region.setScore(AREA_TYPE);
                    for(GFFData data : region.gffDatas){
                        outputGFFFile.write(data.toString()+"\tPeak_n°"+nPeak+"\n");
                    }
                    outputAreaTabFile.write(region.toAreaString()+"\tPeak_n°"+nPeak+"\n");
                    outputAreaGFFFile.write(region.toString()+"\tPeak_n°"+nPeak+"\n");
                }
                //Dans tous les cas on nettoie
                region.clear();
                isPeak = false;
            }
            ligne = inputFile.readLine();
        }
        //Quand on arrive à la fin du fichier on traite la dernière région
        if(!region.isEmpty() && isPeak && region.size() >= 3){
            nPeak++;
            region.setOverlapsAtMean();
            region.peaksAndArea();
            region.setScore(AREA_TYPE);
            for(GFFData data : region.gffDatas){
                outputGFFFile.write(data.toString()+"\tPeak_n°"+nPeak+"\n");
            }
            outputAreaTabFile.write(region.toAreaString()+"\tPeak_n°"+nPeak+"\n");
            outputAreaGFFFile.write(region.toString()+"\tPeak_n°"+nPeak+"\n");
        }
        //on nettoie
        region.clear();
        isPeak = false;
        inputFile.close();
        outputGFFFile.close();
        outputAreaTabFile.close();
        outputAreaGFFFile.close();
        
        return nPeak;
    }
    
    public static void main(String[] args)throws Exception{
	String inputFileName = "";
	String outputFileName = "";
	String lastOption = "";
        int distance=650;
        double bindingPeakThreshold = Double.NaN, bindingExtendThreshold = Double.NaN, pValuePeakThreshold = Double.POSITIVE_INFINITY, pValueExtendThreshold = Double.POSITIVE_INFINITY;
	
	
	if (args.length!=0) {
            inputFileName = args[0];
            outputFileName = inputFileName.replace(".gff","")+".peak.gff";
            for (int i = 1; i < args.length; i++) {
                if (args[i].charAt(0) == '-') {
                    lastOption = args[i];
                    continue;
                }
                if (lastOption.equals("-o")) {
                    outputFileName = args[i];
                    continue;
                }
                if (lastOption.equals("-d")) {
                    try {
                        distance = Integer.parseInt(args[i]);
                    } catch (Exception e) {
                        System.out.println("Bad format for parameter -pb (int required)");
                        return;
                    }
                    continue;
                }
                if (lastOption.equals("-pb")) {
                    try {
                        bindingPeakThreshold = Double.parseDouble(args[i]);
                    } catch (Exception e) {
                        System.out.println("Bad format for parameter -pb (float required)");
                        return;
                    }
                    continue;
                }
                if (lastOption.equals("-eb")) {
                    try {
                        bindingExtendThreshold = Double.parseDouble(args[i]);
                    } catch (Exception e) {
                        System.out.println("Bad format for parameter -eb (float required)");
                        return;
                    }
                    continue;
                }
                if (lastOption.equals("-pp")) {
                    try {
                        pValuePeakThreshold = Double.parseDouble(args[i]);
                    } catch (Exception e) {
                        System.out.println("Bad format for parameter -pp (float required)");
                        return;
                    }
                    continue;
                }
                if (lastOption.equals("-ep")) {
                    try {
                        pValueExtendThreshold = Double.parseDouble(args[i]);
                    } catch (Exception e) {
                        System.out.println("Bad format for parameter -eb (float required)");
                        return;
                    }
                    continue;
                }
                System.out.println(args[i] + " n'est pas un argument valide.");
            }
            peakDetection(inputFileName, outputFileName, distance, bindingPeakThreshold, pValuePeakThreshold, bindingExtendThreshold, pValueExtendThreshold);

        } 
        else {
            System.out.println("Error - no parameters");
        }
        
    }
}
 