# Retrieval Models

## 1. Automatically translate topic statements to queries

- **Search.ExtractQuery** queries are extracted and preprocessed from TREC style topic file `topics.txt`. The topic file `topics.txt` contains four TREC style topics.
- **Classes.Query** stores query information, including the topic id and a representation of the query.

## 2. Implementing the Statistical Language Model

- **Search.QueryRetrievalModel** implements the method `retrieveQuery(Query aQuery, int TopN)`, which retrieves the input query and returns the top `N` retrieved documents as a list of `Classes.Document` objects.
- **IndexingLucene.MyIndexReader** use Apache Lucene to achieve same functionalities as `MyIndexReader`.

```bash
queryid    Q0    documentid    rank    score    runid
```
