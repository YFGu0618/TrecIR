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
public class TrecwebCollection implements DocumentCollection {
  // you can add essential private methods or variables
  private BufferedReader brReader = null;
  // use regular expression to fit for the HTML tags and other undesirable symbols
  private static final String strHTMLTag = "(<(.*?)>)|(\\[(.*?)\\])|(\\&\\#?\\S+( ))|(( )\\S+>( ))";

  // YOU SHOULD IMPLEMENT THIS METHOD
  public TrecwebCollection() throws IOException {
    // This constructor should open the file in Path.DataWebDir
    // and also should make preparation for function nextDocument()
    // you cannot load the whole corpus into memory here!!
    FileInputStream fis = new FileInputStream(Path.DataWebDir);
    brReader = new BufferedReader(new InputStreamReader(fis));
  }

  // YOU SHOULD IMPLEMENT THIS METHOD
  public Map<String, Object> nextDocument() throws IOException {
    // this method should load one document from the corpus, and return this document's number and
    // content.
    // the returned document should never be returned again.
    // when no document left, return null
    // NT: the returned content of the document should be cleaned, all html tags should be removed.
    // NTT: remember to close the file that you opened, when you do not use it any more
    //
    Map<String, Object> mapDoc = new HashMap<String, Object>(); // for storing the DOCNO-CONTENT
                                                                // pairs
    String strLine = ""; // for storing lines read in by brReader
    String strLine_NoHTML = ""; // for storing lines without HTML tags
    String strDocID = ""; // DOCID
    String strDocText = ""; // CONTENT
    if ((strLine = brReader.readLine()) != null) {

      while (!strLine.equals("<DOC>")) { // read lines until find <DOC>
        strLine = brReader.readLine();
      }
      while (strLine.equals("<DOC>")) { // found <DOC>
        strLine = brReader.readLine(); // the next line to <DOC> is <DOCNO>
        strDocID = strLine.substring(7, 24); // substring(7,24) is the DOCNO
      }

      while (!strLine.equals("</DOCHDR>")) { // read lines until </DOCHDR>, after which content
                                             // begins
        strLine = brReader.readLine();
      }
      strLine = brReader.readLine();
      while (!strLine.equals("</DOC>")) { // if not end of current doc
        // eliminate all HTML tags
        strLine_NoHTML = strLine.replaceAll(strHTMLTag, " "); // eliminate HTML tags
        strDocText += " " + strLine_NoHTML; // add cleaned line to CONTENT
        strLine = brReader.readLine(); // read next line
      }
      // do HTML tag elimination again, because there are some multi-line tags
      // then replace multi-whitespace with single whitespace
      mapDoc.put(strDocID,
          strDocText.replaceAll(strHTMLTag, " ").replaceAll("( )+", " ").toCharArray());
      return mapDoc;
    }
    if (brReader != null)
      brReader.close();
    return null;
  }
}
