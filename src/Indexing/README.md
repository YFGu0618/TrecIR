# Collection Index Construction

## 1. Build an index

- **Indexing.PreProcessedCorpusReader** access to the `result.trectext` and `result.trecweb`, and return document one by one through the `nextDocument()`.
- **Indexing.MyIndexWriter** has one essential method `IndexADocument(String docno, String content)` to create index for a document represented by the `docno` and the `content`.

## 2. Retrieve posting lists of tokens from an index

- **Indexing.MyIndexReader** has the following methods:
  - `MyIndexReader()`: read the index file.
  - `int GetDocid(String docno )` and `String getDocno(int docid )`: provides transformation between string `docno`s and integer `docid`s.
  - `int[][] GetPostingList(String token )`: retrieve posting list of the token as a 2-dimension array (see comments in `MyIndexReader` for the structure of the array)
  - `int GetDocFreq(String token )`: get the document frequency of the token.
  - `long GetCollectionFreq(String token )`: get the collection frequency of the token.
