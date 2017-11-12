package Search;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private static double MU = 2000.0; // The Dirichlet Prior Smoothing factor
	private static long CORPUS_SIZE;
	
	public QueryRetrievalModel(MyIndexReader indexReader) {
		this.indexReader = indexReader;
		CORPUS_SIZE = this.indexReader.CorpusSize();
	}

	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param  aQuery      query to be searched for.
	 * @param  TopN        maximum number of returned document
	 * @return             most relevant document, in List structure
	 * @throws Exception   
	 */
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws Exception {
		List<Document> results = new ArrayList<>();

		// map for storing the <docid, <term, term_freq>>
		Map<Integer, HashMap<String, Integer>> queryResult = new HashMap<>();
		// map for storing the <term, collection_freq>
		Map<String, Long> termFreq = new HashMap<>();
		// store tokens in aQuery into String array
		String[] tokens = aQuery.GetQueryContent().split(" ");
		// search for each token then calculate the corresponding scores
		for (String token : tokens) {
			long cf = indexReader.CollectionFreq(token);
			termFreq.put(token, cf);
			if (cf == 0) {
				System.out.println("Token <" + token + "> not found in corpus!");
				continue;
			}
			int[][] postingList = indexReader.getPostingList(token);
			for(int[] posting : postingList) {
				if(!queryResult.containsKey(posting[0])) {
					HashMap<String, Integer> ttf = new HashMap<>();
					ttf.put(token, posting[1]);
					queryResult.put(posting[0], ttf);
				}
				else
					queryResult.get(posting[0]).put(token, posting[1]);
			}
		}

		// query likelihood model, calculate the probability of 
		// each document model generating each query terms
		List<DocScore> lResults = new ArrayList<>();
		// List<DocScore> lResults = new ArrayList<>();
		queryResult.forEach((docid, ttf) -> {
			int doclen = 0;
			double score = 1.0;
			try {
				doclen = indexReader.docLength(docid);
			} catch(Exception e) {};
			// Dirichlet piror smoothing
			// p(w|D) = (|D|/(|D|+MU))*(c(w,D)/|D|) + (MU/(|D|+MU))*p(w|REF)
			// score                c1*p_doc        +            c2*p_ref
			double c1 = doclen / (doclen + MU);
			double c2 = MU / (doclen + MU);
			for (String token : tokens) {
				long cf = termFreq.get(token);
				// ignore if the token doesn't exist in corpus
				if (cf == 0)
					continue;
				int tf = ttf.getOrDefault(token, 0);
				double p_doc = (double)tf / doclen; // c(w, D)
				double p_ref = (double)cf / CORPUS_SIZE; // p(w|REF)
				score *= (c1*p_doc + c2*p_ref); // the probability is multiplied to the score
			}
			DocScore tmpDS = new DocScore(docid, score);
			lResults.add(tmpDS);
		}); // end of queryResult.forEach()

		// sort the List with DocScoreComparator()
		Collections.sort(lResults, new DocScoreComparator());

		// put all documents into result list
		for (int cnt = 0; cnt < TopN; cnt++) {
			DocScore ds = lResults.get(cnt);
			Document doc = null;
			try {
				int id = ds.getId();
				doc = new Document(Integer.toString(id), indexReader.getDocno(id), ds.getScore());
			} catch(Exception e) {};
			results.add(doc);
		}
		return results;
	} // end of retrieveQuery



	// store docid and corresponding score
	private class DocScore {
		private int docid;
		private double score;
		DocScore(int docid, double score) {
			this.docid = docid;
			this.score = score;
		}
		public int getId() {
			return this.docid;
		}
		public double getScore() {
			return this.score;
		}
	} // end of DocScore

	// comparator for sorting the result List<DocScore>
	private class DocScoreComparator implements Comparator<DocScore> {
		public int compare(DocScore arg0, DocScore arg1) {
			if( arg0.score != arg1.score)
				return arg0.score < arg1.score ? 1:-1;
			else
				return 1;
		}
	} // end of DocScoreComparator	
}