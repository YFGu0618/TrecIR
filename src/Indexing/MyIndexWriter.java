package Indexing;

import java.io.IOException;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class MyIndexWriter {
	// Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private static final int MAX_IN_BLOCK = 40000;
	private int iNumInBlock;
	private int iDocID; // unique integer docId assigned to each document
	private RawIndex ridxBlock;
	private int iBlockID;
	private String strDataType;
	private BufferedWriter bwWriter;
	private BufferedWriter bwIDNO;
	private OutputStream fosIDNO;


	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		File idno = new File("data//" + type + ".idno");
		fosIDNO = new FileOutputStream(idno,false);
		bwIDNO = new BufferedWriter(new OutputStreamWriter(fosIDNO));
		ridxBlock = new RawIndex();
		strDataType = type;
	}
	
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		Map<String, ArrayList<Integer>> mapInverted = new HashMap<String, ArrayList<Integer>>();
		mapInverted = this.inverse(content); // build inverted index for the doc
		ridxBlock.update(mapInverted); // add inverted doc into index block
		iNumInBlock++; // count total docs indexed in ridxBlock
		bwIDNO.write(iDocID + "," + docno + "\n"); // output docID-docNO to file
		iDocID++; // increment docID for next doc
		if (iNumInBlock == MAX_IN_BLOCK) { // if indexed enough docs, dump partial index
			this.block_to_disk();
		}
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		// write the last block into file
		this.block_to_disk();
		if (bwWriter != null)
			bwWriter.close();
		if (bwIDNO != null)
			bwIDNO.close();
		ridxBlock.clear();
		this.fuse();
		this.build_dict();
	}

	private Map<String, ArrayList<Integer>> inverse (String content) {
		Map<String, ArrayList<Integer>> mapIndex = new HashMap<String, ArrayList<Integer>>();
		// split the preprocessed content by whitespace
		String[] tokens = content.split("\\s");
		for (int i = 0; i < tokens.length; i++) {
			String curToken = tokens[i];
			if (!mapIndex.containsKey(curToken))
				// if the word didn't appear before, put
				// curToken-[iDocID, position] into map
				// the first element is DocID, second is term frequency
				mapIndex.put(curToken, new ArrayList<Integer>(Arrays.asList(iDocID, 1, i)));
			else{
				// if the word exist in the map, write
				// the current position into corresponding entry
				mapIndex.get(curToken).add(i);
				// term frequency +1
				mapIndex.get(curToken).set(1, mapIndex.get(curToken).get(1)+1);
			}
		}
		return mapIndex;
	}

	private void block_to_disk () throws IOException {
		// write the partial index onto disk
		File file = new File("data//." + strDataType + ".ridx" + iBlockID);
		OutputStream fos = new FileOutputStream(file,false);
		bwWriter = new BufferedWriter(new OutputStreamWriter(fos));

		ridxBlock.mapTerm.forEach((k,v) -> {
			try {
				// bwWriter.write(k + " " + v + "\n");
				bwWriter.write(k + " ");
				for (int i = 0; i < v.size(); i++) {
					bwWriter.write(v.get(i).get(0) + ":" + v.get(i).get(1) + ",");
					for (int j = 2; j < v.get(i).size(); j++) {
						//bwWriter.write(v.get(i).get(j) + j==(v.get(i).size()-1)?";":",");
						String s = (j==(v.get(i).size()-1)?";":",");
						bwWriter.write(v.get(i).get(j) + s);
					}
				}
				bwWriter.write("\n");
			} catch (IOException e) {}
		});
		iNumInBlock = 0;
		iBlockID++;
		bwWriter.close();
		ridxBlock.clear();
	}
	private void fuse () throws IOException{
		Map<String,String> mp = new HashMap<String,String>();
		for (int i = 0; i < iBlockID; i++) {
			File f = new File("data//." + strDataType + ".ridx" + i);
			InputStream fis = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = "";
			while ((str = br.readLine()) != null) {
				String[] sp = str.split("\\s");
				if (!mp.containsKey(sp[0]))
					mp.put(sp[0], sp[1]);
				else
					mp.put(sp[0],mp.get(sp[0])+sp[1]);
			}
			f.delete();
		}
		File fidx = new File("data//" + strDataType + ".ridx");
		OutputStream fosidx = new FileOutputStream(fidx,false);
		BufferedWriter bwidx = new BufferedWriter(new OutputStreamWriter(fosidx));
		mp.forEach((k,v) -> {
			try {
				bwidx.write(k + " " + v + "\n");
			} catch (IOException e) {}
		});
		mp.clear();
		bwidx.close();
	}
	private void build_dict() throws IOException{
		File fidx = new File("data//" + strDataType + ".ridx");
		InputStream fisidx = new FileInputStream(fidx);
		BufferedReader bridx = new BufferedReader(new InputStreamReader(fisidx));
		File fdic = new File("data//" + strDataType + ".dict");
		OutputStream fosdic = new FileOutputStream(fdic,false);
		BufferedWriter bwdic = new BufferedWriter(new OutputStreamWriter(fosdic));
		String strLine = "";
		int iLineNum = 0;
		while ((strLine = bridx.readLine()) != null) {
			String[] term = strLine.split("\\s");
			bwdic.write(term[0] + "," + iLineNum++ + "\n");
		}
		bridx.close();
		bwdic.close();
	}
	/**
	 * Index class
	 */
	private static final class RawIndex {
		private Map<String, ArrayList<ArrayList<Integer>>> mapTerm;
		RawIndex () {
			mapTerm = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		}
		// update new doc into index
		private boolean update (Map<String, ArrayList<Integer>> doc) {
			if (doc != null) {
				doc.forEach((k,v) -> {
					if (!mapTerm.containsKey(k))
						mapTerm.put(k, new ArrayList<ArrayList<Integer>>(Arrays.asList(v)));
					else
						mapTerm.get(k).add(v);
				});
				return true;
			}
			return false;
		}

		private void clear() {
			if (!mapTerm.isEmpty())
				mapTerm.clear();
		}
	}
}
