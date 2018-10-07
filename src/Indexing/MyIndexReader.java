package Indexing;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

public class MyIndexReader {
  // Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
  private String strDataType;
  private BufferedReader brDict;
  private Map<String, String> mpDict;
  private BufferedReader brIdno;
  private Map<String, String> mpIdno;

  public MyIndexReader(String type) throws IOException {
    // read the index files you generated in task 1
    // remember to close them when you finish using them
    // use appropriate structure to store your index
    String str = "";
    strDataType = type;
    InputStream fisDict = new FileInputStream("data//" + strDataType + ".dict");
    brDict = new BufferedReader(new InputStreamReader(fisDict));
    mpDict = new HashMap<String, String>();
    while ((str = brDict.readLine()) != null) {
      String[] s = str.split(","); // term - line-number (of *.indx file)
      mpDict.put(s[0], s[1]);
    }
    InputStream fisIdno = new FileInputStream("data//" + strDataType + ".idno");
    brIdno = new BufferedReader(new InputStreamReader(fisIdno));
    mpIdno = new HashMap<String, String>();
    while ((str = brIdno.readLine()) != null) {
      String[] s = str.split(","); // docID - docNo
      mpIdno.put(s[0], s[1]);
      mpIdno.put(s[1], s[0]);
    }
  }

  // get the non-negative integer dociId for the requested docNo
  // If the requested docno does not exist in the index, return -1
  public int GetDocid(String docno) {
    return Integer.parseInt(mpIdno.getOrDefault(docno, "-1"));
  }

  // Retrieve the docno for the integer docid
  public String GetDocno(int docid) {
    return mpIdno.get(Integer.toString(docid));
  }

  /**
   * Get the posting list for the requested token.
   *
   * The posting list records the documents' docids the token appears and corresponding frequencies
   * of the term, such as:
   *
   * [docid] [freq] 1 3 5 7 9 1 13 9
   *
   * ...
   *
   * In the returned 2-dimension array, the first dimension is for each document, and the second
   * dimension records the docid and frequency.
   *
   * For example: array[0][0] records the docid of the first document the token appears. array[0][1]
   * records the frequency of the token in the documents with docid = array[0][0] ...
   *
   * NOTE that the returned posting list array should be ranked by docid from the smallest to the
   * largest.
   *
   * @param token
   * @return
   */
  public int[][] GetPostingList(String token) throws IOException {
    String str = this.postingOf(token);
    if (str != "") {
      String[] docs = str.split(";");
      int[][] pl = new int[docs.length][2];
      int i = 0;
      for (String s : docs) {
        if (s != "") {
          pl[i][0] = Integer.parseInt(s.split(":")[0]);
          pl[i][1] = Integer.parseInt(s.split(":")[1].split(",")[0]);
          i++;
        }
      }
      return pl;
    }
    return null;
  }

  // Return the number of documents that contains the token.
  public int GetDocFreq(String token) throws IOException {
    String str = this.postingOf(token);
    if (str != "") {
      String[] docs = str.split(";");
      return docs.length;
    }
    return 0;
  }

  // Return the total number of times the token appears in the collection.
  public long GetCollectionFreq(String token) throws IOException {
    String str = this.postingOf(token);
    if (str != "") {
      String[] docs = str.split(";");
      int iTotal = 0;
      for (String s : docs) {
        if (s != "") {
          iTotal += Integer.parseInt(s.split(":")[1].split(",")[0]);
        }
      }
      return iTotal;
    }
    return 0;
  }

  public String postingOf(String token) throws IOException {
    int iLine = Integer.parseInt(mpDict.getOrDefault(token, "-1"));
    String[] pos = new String[2];
    InputStream fis = new FileInputStream("data//" + strDataType + ".ridx");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    for (int i = 0; i < iLine; i++) {
      br.readLine();
    }
    pos = br.readLine().split("\\s");
    // System.out.println(pos[0]);
    // System.out.println(pos[1]);
    br.close();
    // if (pos[0] == token)
    // return pos[1];
    // return "";
    return pos[1];
  }

  public void Close() throws IOException {
    if (brDict != null)
      brDict.close();
    if (brIdno != null)
      brIdno.close();
    mpDict.clear();
    mpIdno.clear();
  }

}
