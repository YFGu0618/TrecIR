import java.util.Map;
import Indexing.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!!
 * 
 * Main class for running your HW2.
 * 
 */
public class HW2Main {

	public static void main(String[] args) throws Exception {
		HW2Main hm2 = new HW2Main();

		
		long startTime=System.currentTimeMillis();
		hm2.WriteIndex("trecweb");
		long endTime=System.currentTimeMillis();
		System.out.println("index web corpus running time: "+(endTime-startTime)/60000.0+" min"); 
		startTime=System.currentTimeMillis();
		hm2.ReadIndex("trecweb", "acow");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");

		startTime=System.currentTimeMillis();
		hm2.WriteIndex("trectext");
		endTime=System.currentTimeMillis();
		System.out.println("index text corpus running time: "+(endTime-startTime)/60000.0+" min");
		startTime=System.currentTimeMillis();
		hm2.ReadIndex("trectext", "yhoo");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");		
	}

	public void WriteIndex(String dataType) throws Exception {
		// Initiate pre-processed collection file reader
		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader(dataType);
		
		// initiate the output object
		MyIndexWriter output=new MyIndexWriter(dataType);
		
		// initiate a doc object, which will hold document number and document content
		Map<String, String> doc = null;

		int count=0;
		// build index of corpus document by document
		while ((doc = corpus.NextDocument()) != null) {
			// load document number and content of the document
			String docno = doc.get("DOCNO"); 
			String content = doc.get("CONTENT");			
			
			// index this document
			output.IndexADocument(docno, content); 
			
			count++;
			if(count%30000==0)
				System.out.println("finish "+count+" docs");
		}
		System.out.println("totaly document count:  "+count);
		output.Close();
	}
	
	public void ReadIndex(String dataType, String token) throws Exception {
		// Initiate the index file reader
		MyIndexReader ixreader=new MyIndexReader(dataType);
		
		// conduct retrieval
		int df = ixreader.GetDocFreq(token);
		long ctf = ixreader.GetCollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		if(df>0){
			int[][] posting = ixreader.GetPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String docno = ixreader.GetDocno(docid);
				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
			}
		}
		ixreader.Close();
	}
}
