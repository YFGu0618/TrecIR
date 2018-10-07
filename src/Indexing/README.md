# Collection Index Construction

## 1. Build an index

- **Indexing.PreProcessedCorpusReader** access to the _result.trectext_ and _result.trecweb_, and return document one by one through the _nextDocument()_.
- **Indexing.MyIndexWriter** has one essential method _IndexADocument(String docno, String content)_ to create index for a document represented by the **docno** and the **content**.

## 2. Retrieve posting lists of tokens from an index

- **Indexing.MyIndexReader** has the following methods:
  - _MyIndexReader()_: read the index file.
  - _int GetDocid(String docno )_ and _String getDocno(int docid )_: provides transformation between string docnos and integer docids.
  - _int[][] GetPostingList(String token )_: retrieve posting list of the token as a 2-dimension array (see comments in MyIndexReader for the structure of the array)
  - _int GetDocFreq(String token )_: get the document frequency of the token.
  - _long GetCollectionFreq(String token )_: get the collection frequency of the token.
