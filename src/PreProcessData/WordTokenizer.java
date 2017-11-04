package PreProcessData;

import java.util.ArrayList;
/**
 * This is for INFSCI 2140 in 2017 fall
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private ArrayList<String> alchTokens = null;
	// use regular expression to fit for all non-letter non-number characters, replace with whitespace
	private static final String strSymbols2Space = "([[^0-9]&&[^a-z]&&[^A-Z]]+)";
	// use regular expression to fit for symbols which are going to be eliminated
	private static final String strSymbols2None = "(\\')|(\\.)|(\t)";
	private int intIndex = 0;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer(char[] texts) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		alchTokens = new ArrayList<String>();
		// first eliminate unwanted symbols, than delete extra whitespaces
		String strText = new String(texts).replaceAll(strSymbols2None, "").replaceAll(strSymbols2Space, " ").replaceAll("( )+", " ");
		//System.out.println(strText);
		String[] straToken = strText.split(" "); // split long string with internal whitespaces
		for (int i=0; i<straToken.length; i++) { // store the tokens into arraylist
			alchTokens.add(straToken[i]);
		}
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		if (intIndex >= alchTokens.size())
			return null;
		else {
			return alchTokens.get(intIndex++).toCharArray(); // using index to get next token
		}
	}
	
}
