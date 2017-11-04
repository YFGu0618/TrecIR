package PreProcessData;

import java.io.IOException;
import java.util.Map;
import Classes.Path;

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This is for INFSCI 2140 in 2017
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader brReader = null;
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		FileInputStream fis = new FileInputStream(Path.DataTextDir);
		brReader = new BufferedReader(new InputStreamReader(fis));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		Map<String, Object> mapDoc = new HashMap<String, Object>(); // for storing the DOCNO-CONTENT pairs
		String strLine = ""; // for storing lines read in by brReader
		String strDocID = ""; // DOCID
		String strDocText = "";  // CONTENT 
		if((strLine = brReader.readLine()) != null) {

			while (!strLine.equals("<DOC>")) { // read lines until find <DOC>
				strLine = brReader.readLine();
			}
			while (strLine.equals("<DOC>")) { // found <DOC>
				strLine = brReader.readLine(); // the next line to <DOC> is <DOCNO>
				strDocID = strLine.substring(8,24); // substring(8,24) is the DOCNO
			}

			while (!strLine.equals("<TEXT>")) { // read lines until <TEXT>
				strLine = brReader.readLine();
			}
			strLine = brReader.readLine();
			while (!strLine.equals("</TEXT>")) { // if not end of text
				strDocText += " " + strLine; // add new line to CONTENT
				strLine = brReader.readLine(); // read the next line
			}
			while (!strLine.equals("</DOC>")) { // to reach the end of current doc
				strLine = brReader.readLine();
			}
			mapDoc.put(strDocID, strDocText.toCharArray()); // put DOCNO-CONTENT pairs into map
			return mapDoc;
		}
		if (brReader != null)
			brReader.close();
		return null;
	}
}
