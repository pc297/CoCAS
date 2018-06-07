/****************************
 * @author Adrien Jeanniard *
 * @date 29 févr. 2008      *
 ****************************/

package GFFLib;

import java.util.Comparator;

public class GFFComparator implements Comparator{
    public int compare(Object o1, Object o2){
	GFFData d1 = (GFFData)o1;
	GFFData d2 = (GFFData)o2;
	
	if( d1.seqname.compareToIgnoreCase(d2.seqname) < 0 ) return -1;
	if( d1.seqname.compareToIgnoreCase(d2.seqname) > 0 ) return 1;

	if( d1.start < d2.start) return -1;
	if( d1.start > d2.start) return 1;
	
	return (d1.end < d2.end ? -1 : (d1.end == d2.end ? 0 : 1));
    }
}

