package GFFLib;

/****************************
 * @author Adrien Jeanniard *
 * @date 28 mars 2008	    *
 ****************************/



import java.util.ArrayList;



public class GFFRegion extends GFFData {
    public static int EFFECTIVE = 0;
    public static int POSITIVE = 1;
    public static int NEGATIVE = 2;
    
    public ArrayList<GFFData> gffDatas;
    public double positiveArea, negativeArea, minPeak, maxPeak;
        
    public GFFRegion(){
	super();
        this.seqname = "Reg.Name";
	this.gffDatas = new ArrayList<GFFData>();
        this.positiveArea = 0;
        this.negativeArea = 0;
        this.maxPeak = 0;
        this.minPeak = 0;
    }
    
    public GFFRegion(ArrayList<GFFData> gffDatas){
	this();	
	if(!gffDatas.isEmpty()){
	    this.gffDatas = gffDatas;
	    this.seqname = this.gffDatas.get(0).seqname;
	    this.source = this.gffDatas.get(0).source;
	    this.feature = this.gffDatas.get(0).feature;
	    this.start = this.gffDatas.get(0).start;
	    this.end = this.gffDatas.get(gffDatas.size()-1).end;
	    this.score = Double.NaN;
	    this.strand = this.gffDatas.get(0).strand;
	    this.frame = this.gffDatas.get(0).frame;
	    this.attributes = this.gffDatas.get(0).attributes;
            this.comments = this.gffDatas.get(0).comments;
	    
	}
        
    }
    
    //Add the specified GFFData to the GFFRegion with refresh of the GFFRegion coordinates
    public void add(GFFData NewData){
	if(this.isEmpty()){
	    gffDatas.add(NewData);
	    seqname = NewData.seqname;
	    source = NewData.source;
	    start = NewData.start;
	    end = NewData.end;
	    feature = NewData.feature;
	    strand = NewData.strand;
	    frame = NewData.frame;
	    attributes = NewData.attributes;
            comments = NewData.comments;
	}
	else{
	    boolean inserted=false;
	    for( int i=0 ; i<gffDatas.size() ; i++ ){
		if(NewData.start<gffDatas.get(i).start){
		    gffDatas.add(i,NewData);
		    inserted=true;
		    break;
		}
	    }
	    if(!inserted) gffDatas.add(NewData);
	    start = gffDatas.get(0).start;
	    end = gffDatas.get(gffDatas.size()-1).end;
	}
	
    }
    //Remove the GFFData at specified index in the calling GFFRegion
    public GFFData remove(int index){
	GFFData oldData = gffDatas.remove(index);
	start = gffDatas.get(0).start;
	end = gffDatas.get(gffDatas.size()-1).end;
	return oldData;
    }
    //Erase all datas in the calling region
    @Override
    public void clear(){
	this.gffDatas.clear();
	this.seqname = "Reg.Name";
	this.source = "Source";
	this.feature = "Feature";
	this.start = 0;
	this.end = 0;
	this.score = 0;
	this.strand = ".";
	this.frame = ".";
	this.attributes = "Attributes";
        this.comments = "Comments";
        this.positiveArea = 0;
        this.negativeArea = 0;
        this.maxPeak = 0;
        this.minPeak = 0;
    }
    //Return the number of GFFData in the calling GFFRegion
    public int size(){
	return gffDatas.size();
    }
    public void setScore(int AREA_TYPE){
	if(AREA_TYPE == EFFECTIVE) score = positiveArea - negativeArea;
	if(AREA_TYPE == POSITIVE) score = positiveArea;
	if(AREA_TYPE == NEGATIVE) score = -negativeArea;
    }
    //Automatically create new GFFData in overlapping reg
    public void setOverlapsAtMean(){
	for(int i = 1 ; i<gffDatas.size() ; i++){
	    if(gffDatas.get(i).start <= gffDatas.get(i-1).end){
		GFFData inter = new GFFData();
		inter.seqname = gffDatas.get(i).seqname;
		inter.feature = gffDatas.get(i).feature;
		inter.source = gffDatas.get(i).source;
		inter.start = gffDatas.get(i).start;
		inter.end = gffDatas.get(i-1).end;
		inter.score = (gffDatas.get(i).score+gffDatas.get(i-1).score)/2;
		inter.strand = gffDatas.get(i).strand;
		inter.frame = gffDatas.get(i).frame;
		inter.attributes = gffDatas.get(i).attributes;
		
		int temp = gffDatas.get(i).start;
		gffDatas.get(i).start = gffDatas.get(i-1).end +1;
		gffDatas.get(i-1).end = temp -1;
		
		gffDatas.add(i, inter);
	    }
	}
    }
    //Return true if calling region is empty
    public boolean isEmpty(){
	return gffDatas.isEmpty();
    }
    //Return the GFFData at specified index in the calling GFFRegion
    public GFFData get(int i){
        return gffDatas.get(i);
    }
    
    //Calcule l'aire du trapèze formé par le segment [(x1,y1);(x2,y2)] et l'axe des absisses
    //Renvoit un Double[2] avec l'aire positive d'une part et l'aire négative d'autre part
    public static double[] aireSousSegment(double x1, double y1, double x2, double y2){
	double[] results = new double[2];
	double a, b, x0, y0;
	
	//y1 et y2 sont tous deux >0, on est dans le cas d'un trapèze au dessus de l'axe des absisses.
	if( y1 >= 0 && y2 >= 0 ){
	    results[1] = 0;
	    results[0] = Math.abs(y1+y2)*Math.abs(x1-x2+1)/2;
	    return results;
	}
	//y1 et y2 sont tous deux <0, on est dans le cas d'un trapèze en dessous de l'axe des absisses.
	else{
	    if( y1 <= 0 && y2 <= 0 ){
		results[1] = Math.abs(y1+y2)*Math.abs(x1-x2+1)/2;
		results[0] = 0;
		return results;
	    }
	    else{
		//Si y1 et Y2 sont de signes opposés, l'aire à calculer n'est plus un trapèze mais deux triangles
		//(un positif et un négatif).
		//On calcule donc le points d'ordonnée 0 sur le segment. 
		if(x2-x1 == 0){
		    for(int i = 0 ; i < 4 ; i++) results[i]=0;
		    return results;
		}

		a = (y2-y1)/(x2-x1+1);
		b = y1-a*x1;
		x0 = -b/a;
		y0 = 0;
		//Puis l'aire des deux triangles.
		if(y1 >= 0){
		    results[1] = aireSousSegment(x2,y2,x0,y0)[1];
		    results[0] = aireSousSegment(x1,y1,x0,y0)[0];
		}
		else{
		    results[1] = aireSousSegment(x1,y1,x0,y0)[1];
		    results[0] = aireSousSegment(x2,y2,x0,y0)[0];
		}
		return results;
	    }
	}
    }
    
    //Cette méthode calcule les différentes aires sous la région.
    public void peaksAndArea(){
	double[] results = new double[2];
	
	minPeak = gffDatas.get(0).score;
	maxPeak = gffDatas.get(0).score;
	results = aireSousSegment(gffDatas.get(0).start, gffDatas.get(0).score, gffDatas.get(0).end, gffDatas.get(0).score);
	positiveArea = results[0];
	negativeArea = results[1];
	//Pour chaque autre segment...
	for( int i = 1 ; i < gffDatas.size() ; i++ ){
	    //On met à jour les pics min/max.
	    if(gffDatas.get(i).score < minPeak) minPeak = gffDatas.get(i).score;
	    if(gffDatas.get(i).score > maxPeak) maxPeak = gffDatas.get(i).score;
	    //On calcule l'aire intermédiaire entre le segment précédent et le segment en cours.
	    results = aireSousSegment(gffDatas.get(i-1).end, gffDatas.get(i-1).score, gffDatas.get(i).start, gffDatas.get(i).score);
	    positiveArea += results[0];
	    negativeArea += results[1];
	    //On calcule l'aire sous le segment en cours.
	    results = aireSousSegment(gffDatas.get(i).start, gffDatas.get(i).score, gffDatas.get(i).end, gffDatas.get(i).score);
	    positiveArea += results[0];
	    negativeArea += results[1];
	}
    }
    
    public static String areaFileHeader(){
	return "Reg.Name\tSource\tFeature\tStart\tEnd\t#probes\tEffectiveArea\tPositiveArea\tNegativeArea\tMax.Peak\tMin.Peak\tAreaPerBase";
    }
    
    public String toAreaString(){
        return seqname+"\t"+source+"\t"+feature+"\t"+start+"\t"+end+"\t"+size()+"\t"+(positiveArea-negativeArea)+"\t"+positiveArea+"\t"+negativeArea+"\t"+maxPeak+"\t"+minPeak+"\t"+(positiveArea-negativeArea)/(end-start);
    }
    
    public String toString(boolean all){
        String toString ="";
        if(all) for(int i = 0 ; i < gffDatas.size() ; i++) toString+= gffDatas.get(i).toString()+"\n";
        else toString = this.toString();
        return toString;
    }
}

