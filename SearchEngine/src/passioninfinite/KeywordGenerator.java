package passioninfinite;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class KeywordGenerator {

	private ArrayList<String> stopWords = new ArrayList<>();
	
	private String[] filteredKeywords = {};
	
	public KeywordGenerator(String[] searchKeywords) {
		this.loadStopWords();
		this.filterKeywords(searchKeywords);
	}
	
	private void filterKeywords(String[] keywords) {
		StringBuilder builder = new StringBuilder();
		for(String keyword : keywords) {
			keyword = keyword.trim().toLowerCase();
			if (!this.stopWords.contains(keyword)) {
				builder.append(keyword + "\n");
			}
		}
		this.filteredKeywords = builder.toString().split("\n").clone();
	}
	
	private void loadStopWords() {
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("input/stopWords.txt"));
			while((line = reader.readLine()) != null) {
				this.stopWords.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] run() {
		return this.filteredKeywords;
	}
}
