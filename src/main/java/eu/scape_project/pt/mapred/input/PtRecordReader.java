package eu.scape_project.pt.mapred.input;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

/*
 * Extends LineRecordReader to ignore lines starting with '#'
 * 
 * @author Rainer Schmidt [rschmidt13]
 */
public class PtRecordReader extends LineRecordReader {

	public static char COMMENT_CHARACTER = '#';
	
	@Override
	public boolean nextKeyValue() throws IOException { 
		boolean ret = super.nextKeyValue();
		Text currentVal = getCurrentValue();
		if(	currentVal != null && currentVal.toString().trim().equals("") || 
			currentVal != null && currentVal.toString().startsWith(new Character(COMMENT_CHARACTER).toString())) 
		{
			//System.out.println("PTRecordReader caught: "+currentVal.toString());
			ret = this.nextKeyValue();
		}
		return ret;
	}
}
