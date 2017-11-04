package Indexing;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import Classes.Path;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PreProcessedCorpusReader {

	private BufferedReader brReader;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, OR download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp.
		// Close the file when you do not use it any more
		if (type == "trecweb") {
			InputStream fis = new FileInputStream(Path.ResultWebDir);
			brReader = new BufferedReader(new InputStreamReader(fis));
		}
		if (type == "trectext") {
			InputStream fis = new FileInputStream(Path.ResultTextDir);
			brReader = new BufferedReader(new InputStreamReader(fis));
		}
	}
	
	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		Map<String, String> mapDoc = new HashMap<String, String>(); // for storing the DOCNO-CONTENT pairs
		String strDocID = ""; // DOCID
		String strDocText = "";  // CONTENT 
		if ((strDocID = brReader.readLine()) != null) {
			strDocText = brReader.readLine();
			mapDoc.put("DOCNO", strDocID); // put DOCNO-CONTENT pairs into map
			mapDoc.put("CONTENT", strDocText);
			return mapDoc;
		}
		if (brReader != null)
			brReader.close();
		return null;
	}

}
