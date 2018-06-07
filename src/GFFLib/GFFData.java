    /****************************
 * @author Adrien Jeanniard *
 *                          *
 ****************************/

package GFFLib;

import java.text.DecimalFormat;
/* Format GFF version2:
 * <seqname> <source> <feature> <start> <end> <score> <strand> <frame> [attributes] [comments]
 */
public class GFFData{
    public String seqname;
    public String source;
    public String feature;
    public int start;
    public int end;
    public double score;
    public String strand;
    public String frame;
    public String attributes;
    public String comments;
    
    public GFFData(){
	seqname = "";
	source = "";
	feature = "";
	start = -1;
	end = -1;
	score = -1;
	strand = "";
	frame = "";
	attributes = "";
        comments = "";
    }
    public GFFData(String s) throws Exception{
	try {
	    String[] parse = s.split("\t");
	    if(parse.length < 8) throw new Exception("Incorrect number of columns in GFF line (9 columns required)");
	    seqname = parse[0];
	    source = parse[1];
	    feature = parse[2];
	    start = (int) Double.parseDouble(parse[3]);
	    end = (int) Double.parseDouble(parse[4]);
	    if(parse[5].isEmpty()) score = 0;
            else score = Double.parseDouble(parse[5]);
	    strand = parse[6];
	    frame = parse[7];
	    if(parse.length > 8) attributes = parse[8];
            else attributes = "";
            if(parse.length > 9) comments = parse[9];
            else comments = "";

	    if (start > end) {
		int temp;
		temp = start;
		start = end;
		end = temp;
	    }

	} 
        catch (Exception e) {
	    throw new Exception("Bad gff format\n@:"+s);
	}

    }
    
    //Return the p-value
    public double getPValue(){
	double pValue;
        try{
            pValue = Double.parseDouble(frame);
        }
        catch(NumberFormatException e){
            return Double.POSITIVE_INFINITY;
        }
        return pValue;
    }
    //Erase all datas in the calling region
    public void clear(){
	seqname = "";
	source = "";
	feature = "";
	start = 0;
	end = 0;
	score = 0;
	strand = "";
	frame = "";
	attributes = "";
        comments = "";
    }
    @Override
    public String toString(){
//        DecimalFormat format=new DecimalFormat(".#########");
//        return seqname+"\t"+source+"\t"+feature+"\t"+start+"\t"+end+"\t"+format.format(score)+"\t"+strand+"\t"+frame+"\t"+attributes;
        String s;
        s = seqname+"\t"+source+"\t"+feature+"\t"+start+"\t"+end+"\t"+score+"\t"+strand+"\t"+frame;
        if(!attributes.isEmpty()){
            s+= "\t"+attributes;
            if(!comments.isEmpty()){
                s += "\t"+comments;
            }
        }
        else{
            if(!comments.isEmpty())
                s += "\t\t"+comments;
        }   
        return s;
    }
}
