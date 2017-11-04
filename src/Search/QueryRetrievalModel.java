package Search;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private static double MU = 2000.0; // The Dirichlet Prior Smoothing factor
	private static long CORPUS_SIZE;
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
		CORPUS_SIZE = indexReader.CorpusSize();
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		List<Document> results = new ArrayList<>();
		// map for storing the <token, <docid, term_freq>> pair
		Map<String, HashMap<Integer, Integer>> queryResult = new HashMap<>();
		// store tokens in aQuery into String array
		String[] tokens = aQuery.GetQueryContent().split(" ");
		// search for each token then calculate the corresponding scores
		for (String token : tokens) {
			long cf = indexReader.CollectionFreq(token);
			if (cf == 0) {
				System.out.println("Token <" + token + "> not found in corpus!");
				continue;
			}
			int[][] postingList = null;
			// if the token is not in the queryResult, then search
			// for it from the index and put into queryResult
			if(!queryResult.containsKey(token)) {
				postingList = indexReader.getPostingList(token);
				// System.out.println(token + " " + postingList.length);
				// map for storing the <docid, term_freq> pair
				HashMap<Integer, Integer> mpos = new HashMap<>();
				for(int[] posting : postingList) {
					mpos.put(posting[0], posting[1]);
				}
				queryResult.put(token, mpos);
			}
		}
		// map for storing the <docid, score> pair
		Map<Integer, Double> docid_score = new HashMap<>();
		// get the comprehensive docset
		queryResult.forEach((token,map) -> {
			map.forEach((id, tf) -> {
				docid_score.put(id, Double.valueOf(1));
			});
		});
		// query likelihood model, calculate the probability of 
		// each document model generating each query terms
		long startTime = System.currentTimeMillis();
		docid_score.forEach((docid, score) -> {
			int doclen = 0;
			try {
				doclen = indexReader.docLength(docid);
			} catch(Exception e) {};
			// Dirichlet piror smoothing factors
			double c1 = doclen / (doclen + MU);
			double c2 = doclen / (doclen + MU);
			for (String token : tokens) {
				long cf = 0;
				try {
					cf = indexReader.CollectionFreq(token);
				} catch(Exception e) {};
				// if the token doesn't exist in corpus
				// ignore it and go to next token
				if (cf == 0) {
					continue;
				}
				int tf = 0;
				Map<Integer, Integer> mpos = queryResult.getOrDefault(token, null);
				if (mpos != null)
					tf = mpos.getOrDefault(docid, 0);
				double p_doc = (double)tf / doclen; // c(w, D)
				double p1 = c1 * p_doc; // the first part of the smoothing equation
				double p_ref = (double)cf / CORPUS_SIZE; // p(w|REF)
				double p2 = c2 * p_ref; // the second part of the smoothing equation
				score *= (p1 + p2); // the probability is multiplied to the score
			}
			docid_score.put(docid, score);
		});
		long endTime = System.currentTimeMillis(); // end time of running code
		System.out.println("score calculation time: " + (endTime - startTime) / 60000.0 + " min");
		// sort the docid_score map with score
		// and then store it into mResults
		Map<Integer, Double> mResults = docid_score.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(TopN)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
			(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		// put all documents into result list
		mResults.forEach((docid, score) -> {
			Document doc = null;
			try {
				doc = new Document(Integer.toString(docid), indexReader.getDocno(docid),score);
			} catch(Exception e) {};
			results.add(doc);
		});
		return results;
	}
	
}