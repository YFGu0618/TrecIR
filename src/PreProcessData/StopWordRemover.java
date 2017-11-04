package PreProcessData;
import Classes.Path;
import Classes.Stemmer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class StopWordRemover {
	//you can add essential private methods or variables in 2017.
	private BufferedReader brReader = null;
	private HashSet<String> hsstrStopWrod = null;

	public StopWordRemover( ) throws IOException {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		FileInputStream fis = new FileInputStream(Path.StopwordDir);
		brReader = new BufferedReader(new InputStreamReader(fis));
		String strLine = "";
		// build HashSet for stopwords
		hsstrStopWrod = new HashSet<String>();
		while ((strLine = brReader.readLine()) != null) {
			char[] chLine = strLine.toCharArray();
			Stemmer s = new Stemmer(); // should use stemmed stopword to compare with stemmed documents
			s.add(chLine, chLine.length);
			s.stem();
			String str = s.toString();
			hsstrStopWrod.add(str); // add stemmed stopword to HashSet
		}         	            	
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword(char[] word) throws IOException {
		// return true if the input word is a stopword, or false if not
		String strStopWord = new String(word);
		if(hsstrStopWrod.contains(strStopWord))		
			    	return true;
		return false;
	}
}
