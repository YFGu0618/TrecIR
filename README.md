# TrecText & TrecWeb Information Retrieval

## 1. Document Collection Processing

#### 1.1 Reading Documents from Collection Files
 - **PreProcessData.DocumentCollection** is a general interface for sequentially reading documents from collection files.
 - **PreProcessData.TrectextCollection** is the class for trectext format.
 - **PreProcessData.TrecwebCollection** is the class for trecweb format.

#### 1.2 Normalize Document Texts
 - **PreProcessData.TextTokenizer** is a class for sequentially reading words from a sequence of characters.
 - **PreProcessData.TextNormalizer** is the class that transform each word to its lowercase version, and conduct stemming on each word. 
 - **PreProcessData.StopwordsRemover** is the class that can recognize whether a word is a stop word or not. A stop word list file will be provided, so that the class should take the stop word list file as input.

## 2. Collection Index Construction

#### 2.1 Build an index
 - **Indexing.PreProcessedCorpusReader** access to the _result.trectext_ and _result.trecweb_, and return document one by one through the _nextDocument()_.
 - **Indexing.MyIndexWriter** has one essential method _IndexADocument(String docno, String content)_ to create index for a document represented by the **docno** and the **content**. 

#### 2.2 Retrieve posting lists of tokens from an index
 - **Indexing.MyIndexReader** has the following methods:
     - _MyIndexReader()_: read the index file.
     - _int GetDocid( String docno )_ and _String getDocno( int docid )_: provides transformation between string docnos and integer docids.
     - _int[][] GetPostingList( String token )_: retrieve posting list of the token as a 2-dimension array (see comments in MyIndexReader for the structure of the array)
     - _int GetDocFreq( String token )_: get the document frequency of the token.
     - _long GetCollectionFreq( String token )_: get the collection frequency of the token.

## 3. Retrieval Models

#### 3.1 Automatically translate topic statements to queries
 - **Search.ExtractQuery** queries are extracted and preprocessed from TREC style topic file "topics.txt". The topic file “topics.txt” contains four TREC style topics.
 - **Classes.Query** stores query information, including the topic id and a representation of the query.

#### 3.2 Implementing the Statistical Language Model
 - **Search.QueryRetrievalModel** implements the method _retrieveQuery(Query aQuery, int TopN)_, which retrieves the input query and returns the top N retrieved documents as a list of **Classes.Document** objects. 
 - **IndexingLucene.MyIndexReader** use Apache Lucene to achieve same functionalities as **MyIndexReader** in 2.2.
```
queryid   Q0 documentid    rank score   runid  
```



## 4. Relevance Feedback Model
(to be finished)
