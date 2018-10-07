# Document Collection Processing

## 1. Reading Documents from Collection Files

- **PreProcessData.DocumentCollection** is a general interface for sequentially reading documents from collection files.
- **PreProcessData.TrectextCollection** is the class for trectext format.
- **PreProcessData.TrecwebCollection** is the class for trecweb format.

## 2. Normalize Document Texts

- **PreProcessData.TextTokenizer** is a class for sequentially reading words from a sequence of characters.
- **PreProcessData.TextNormalizer** is the class that transform each word to its lowercase version, and conduct stemming on each word.
- **PreProcessData.StopwordsRemover** is the class that can recognize whether a word is a stop word or not. A stop word list file will be provided, so that the class should take the stop word list file as input.
