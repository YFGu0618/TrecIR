package PseudoRFSearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

  // map for storing the <docid, <term, term_freq>>
  private Map<Integer, HashMap<String, Integer>> queryResult;
  // map for storing the <term, collection_freq>
  private Map<String, Long> termFreq;

  protected MyIndexReader indexReader;
  private static double MU = 2000.0; // The Dirichlet Prior Smoothing factor
  private static long CORPUS_SIZE;

  public PseudoRFRetrievalModel(MyIndexReader indexReader) {
    this.indexReader = indexReader;
    CORPUS_SIZE = this.indexReader.CorpusSize();
  }

  /**
   * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4. The returned
   * results (retrieved documents) should be ranked by the score (from the most relevant to the
   * least).
   *
   * @param aQuery query to be searched for.
   * @param TopN   maximum number of returned document
   * @param TopK   count of feedback documents
   * @param alpha  parameter of relevance feedback model
   * @return TopN most relevant document, in List structure
   * @throws Exception
   */
  public List<Document> RetrieveQuery(Query aQuery, int TopN, int TopK, double alpha)
      throws Exception {
    // this method will return the retrieval result of the given Query, and this result is enhanced
    // with pseudo relevance feedback
    // (1) you should first use the original retrieval model to get TopK documents, which will be
    // regarded as feedback documents
    // (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback
    // documents
    // (3) implement the relevance feedback model for each token: combine the each query token's
    // original retrieval score P(token|document) with its score in feedback documents
    // P(token|feedback model)
    // (4) for each document, use the query likelihood language model to get the whole query's new
    // score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')


    // get P(token|feedback documents)
    HashMap<String, Double> TokenRFScore = GetTokenRFScore(aQuery, TopK);
    // sort all retrieved documents from most relevant to least, and return TopN
    List<Document> results = new ArrayList<Document>();

    // store tokens in aQuery into String array
    String[] tokens = aQuery.GetQueryContent().split(" ");
    List<DocScore> lResults = new ArrayList<>();
    queryResult.forEach((docid, ttf) -> {
      int doclen = 0;
      double score = 1.0;
      try {
        doclen = indexReader.docLength(docid);
      } catch (Exception e) {
      } ;
      // Dirichlet piror smoothing
      // p(w|D) = (|D|/(|D|+MU))*(c(w,D)/|D|) + (MU/(|D|+MU))*p(w|REF)
      // score c1*p_doc + c2*p_ref
      double c1 = doclen / (doclen + MU);
      double c2 = MU / (doclen + MU);
      for (String token : tokens) {
        long cf = termFreq.get(token);
        // ignore if the token doesn't exist in corpus
        if (cf == 0)
          continue;
        int tf = ttf.getOrDefault(token, 0);
        double p_doc = (double) tf / doclen; // c(w, D)
        double p_ref = (double) cf / CORPUS_SIZE; // p(w|REF)
        // the probability is multiplied to the score
        score *= alpha * (c1 * p_doc + c2 * p_ref) + (1 - alpha) * TokenRFScore.get(token);
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
      } catch (Exception e) {
      } ;
      results.add(doc);
    }

    return results;
  }

  public HashMap<String, Double> GetTokenRFScore(Query aQuery, int TopK) throws Exception {
    // for each token in the query, you should calculate token's score in feedback documents:
    // P(token|feedback documents)
    // use Dirichlet smoothing
    // save <token, score> in HashMap TokenRFScore, and return it
    HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();

    // store tokens in aQuery into String array
    String[] tokens = aQuery.GetQueryContent().split(" ");
    List<Document> feedbackDocs = new QueryRetrievalModel(indexReader).retrieveQuery(aQuery, TopK);

    queryResult = new HashMap<>();
    termFreq = new HashMap<>();

    // search for each token then calculate the corresponding scores
    for (String token : tokens) {
      long cf = indexReader.CollectionFreq(token);
      termFreq.put(token, cf);
      if (cf == 0)
        continue;
      int[][] postingList = indexReader.getPostingList(token);
      for (int[] posting : postingList) {
        if (!queryResult.containsKey(posting[0])) {
          HashMap<String, Integer> ttf = new HashMap<>();
          ttf.put(token, posting[1]);
          queryResult.put(posting[0], ttf);
        } else
          queryResult.get(posting[0]).put(token, posting[1]);
      }
    }

    // combine TopK docs into pseudoDoc
    int len = 0;
    Map<String, Integer> pseudoDoc = new HashMap<>();
    for (Document doc : feedbackDocs) {
      queryResult.get(Integer.parseInt(doc.docid())).forEach((term, tf) -> {
        if (pseudoDoc.containsKey(term)) {
          pseudoDoc.put(term, tf + pseudoDoc.get(term));
        } else
          pseudoDoc.put(term, tf);
      });
      len += indexReader.docLength(Integer.parseInt(doc.docid()));
    }
    // Dirichlet piror smoothing
    // p(w|D) = (|D|/(|D|+MU))*(c(w,D)/|D|) + (MU/(|D|+MU))*p(w|REF)
    // score c1*p_doc + c2*p_ref
    final int pseudoLen = len;
    double c1 = pseudoLen / (pseudoLen + MU);
    double c2 = MU / (pseudoLen + MU);
    // calculate the probability of pseudoDoc generating each term
    pseudoDoc.forEach((token, tf) -> {
      long cf = termFreq.get(token);
      double p_doc = (double) tf / pseudoLen; // c(w, D)
      double p_ref = (double) cf / CORPUS_SIZE; // p(w|REF)
      double score = c1 * p_doc + c2 * p_ref;
      TokenRFScore.put(token, score); // the probability is multiplied to the score
    });

    return TokenRFScore;
  }

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
      if (arg0.score != arg1.score)
        return arg0.score < arg1.score ? 1 : -1;
      else
        return 1;
    }
  } // end of DocScoreComparator

}
