package Search;

import Classes.Query;
import Classes.Path;
import PreProcessData.StopWordRemover;
import PreProcessData.WordNormalizer;
import PreProcessData.WordTokenizer;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ExtractQuery {

	private BufferedReader brReader;
	private String line;

	public ExtractQuery() throws IOException{
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming.
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		FileInputStream fis = new FileInputStream(Path.TopicDir);
		brReader = new BufferedReader(new InputStreamReader(fis));
	}
	
	public boolean hasNext() throws IOException  {
		// next line is not null means has next topic
		if((line = brReader.readLine()) != null)
			return true;
		if(brReader != null)
			brReader.close();
		return false;
	}
	
	public Query next() throws IOException  {
		Query qNext = new Query();
		// *_n represents the normalized *
		String title = "", title_n = "";
		String desc = "", desc_n = "";
		String narr = "", narr_n = "";
		WordTokenizer tokenizer_title, tokenizer_desc, tokenizer_narr;
		StopWordRemover stopwordRemover;
		WordNormalizer normalizer;
		while(!line.contains("<top>")) // read in lines until <top> start of topic block
			line = brReader.readLine();
		line = brReader.readLine(); // read in the line with <num> tag
		qNext.SetTopicId(line.substring(line.indexOf(":")+2, line.length()));
		line = brReader.readLine(); // read in the line with <title> tag
		title = line.substring(line.indexOf(">")+2, line.length());
		line = brReader.readLine(); // read in the line with <desc> tag
		line = brReader.readLine();
		while (!line.contains("<narr>")) {// quit the loop until <narr>
			desc += line; // append lines in description to desc
			line = brReader.readLine();
		}
		line = brReader.readLine();
		while (!line.contains("</top>")) {// quit the loop until </top>
			narr += line;
			line = brReader.readLine();
		}
		tokenizer_title = new WordTokenizer(title.toCharArray());
		tokenizer_desc = new WordTokenizer(desc.toCharArray());
		tokenizer_narr = new WordTokenizer(narr.toCharArray());
		stopwordRemover = new StopWordRemover();
		normalizer = new WordNormalizer();
		// initiate a word object, which can hold a word
		char[] word = null;
		// process the document word by word iteratively
		while ((word = tokenizer_title.nextWord()) != null) {
			word = normalizer.lowercase(word);
			if (!stopwordRemover.isStopword(word))
				title_n += normalizer.stem(word) + " ";
		}
		while ((word = tokenizer_desc.nextWord()) != null) {
			word = normalizer.lowercase(word);
			if (!stopwordRemover.isStopword(word))
				desc_n += normalizer.stem(word) + " ";
		}
		while ((word = tokenizer_narr.nextWord()) != null) {
			word = normalizer.lowercase(word);
			if (!stopwordRemover.isStopword(word))
				narr_n += normalizer.stem(word) + " ";
		}
		qNext.setQueryTitle(title_n);
		qNext.setQueryDesc(desc_n);
		qNext.setQueryNarr(narr_n);

		// tried to use the title+desc+narr as query
		// but it took about 15min to finish the task
		// thus the compromise is using title as query
		// qNext.SetQueryContent(title_n + desc_n + narr_n);
		qNext.SetQueryContent(title_n);
		return qNext;
	}
}
