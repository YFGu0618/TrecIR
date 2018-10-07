# TrecText & TrecWeb Information Retrieval

- [TrecText & TrecWeb Information Retrieval](#trectext--trecweb-information-retrieval)
  - [1. Document Collection Processing](#1-document-collection-processing)
    - [1.1 Reading Documents from Collection Files](#11-reading-documents-from-collection-files)
    - [1.2 Normalize Document Texts](#12-normalize-document-texts)
  - [2. Collection Index Construction](#2-collection-index-construction)
    - [2.1 Build an index](#21-build-an-index)
    - [2.2 Retrieve posting lists of tokens from an index](#22-retrieve-posting-lists-of-tokens-from-an-index)
  - [3. Retrieval Models](#3-retrieval-models)
    - [3.1 Automatically translate topic statements to queries](#31-automatically-translate-topic-statements-to-queries)
    - [3.2 Implementing the Statistical Language Model](#32-implementing-the-statistical-language-model)
  - [4. Relevance Feedback Model](#4-relevance-feedback-model)

## 1. Document Collection Processing

### 1.1 Reading Documents from Collection Files

The trectext format is widely used to store textual documents such as news articles. Each document starts with `<DOC>` and ends with `</DOC>`; `<DOCNO>` stores the unique identifier of each document; `<TEXT>` stores the main content of the document.

The trecweb format is used to store web documents. Each document starts with `<DOC>` and ends with `</DOC>`; `<DOCNO>` stores the unique identifier of each document; <DOCHDR> usually stores the http response header information of accessing the web document; the content of the web document is stored between `</DOCHDR>` and `</DOC>`.

### 1.2 Normalize Document Texts

Normalization of documents includes following steps:

1. Tokenize document texts into individual word
2. Normalize all the words into their lowercase characters
3. Filter stop words.

## 2. Collection Index Construction

### 2.1 Build an index

The collection index is based on the inverted file structure. Usually, it has at least the following two components:

- Dictionary term file: this file contains all the index terms, their collection frequency (i.e., how many time this term appear in the whole collection), and a pointer to their corresponding posting information in the posting file.
- Posting file: this file contains the corresponding pointer that can link entries in dictionary term file to that in the posting file. This file also includes a repeated set of information that indicates the document id that the term is in, the term frequency (i.e., how many time this term appears in this document), and other information you may want to put into the postings.

### 2.2 Retrieve posting lists of tokens from an index

After building the index, implemented methods that is able to read the index and retrieve the posting list given a token.

## 3. Retrieval Models

### 3.1 Automatically translate topic statements to queries

Given set of topic statements, implemented a module to translate the topic statement information into a set of queries that can be recognized by retrieval module, in which each query corresponds to a search topic and consists of a query content and a query id. Tokenization, normalization and stop-word removing is also conducted on each query.

### 3.2 Implementing the Statistical Language Model

Implemented module able to read the index you built in 2 using document collection file “docset.trectext”, and return documents based on the ranking of the documents generated by retrieval model. The retrieval model is the query likelihood model with Dirichlet Prior Smoothing.

## 4. Relevance Feedback Model

Enhance the retrieval model with pseudo relevance feedback:

1. Obtain feedback documents: conduct the initial search using the query likelihood retrieval model with Dirichlet smoothing, and obtain top K documents where K is a parameter set by the system. These K documents are treated as the relevant documents;
2. For each query term qi in the query, calculate the probability of the feedback documents generating this term, i.e., P(qi | feedback documents). Here all feedback documents are treated as one big pseudo document;
3. Then for each query term qi, the probability of one document D generating it based on relevance feedback is a linear combination of the original probability P(qi | D) and P(qi | feedback documents), where parameter α is used as the coefficient of P(qi | D) and 1-α is used as the coefficient for P(qi | feedback documents);
4. The probability of the query generated by the document is all the probabilities of each query term multiplying together;
5. Sort top N documents based on the probability generated in step 4.
