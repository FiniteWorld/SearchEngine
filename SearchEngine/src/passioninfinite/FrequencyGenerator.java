package passioninfinite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FrequencyGenerator {
	
	private String indexFilePath = "output/meta/index.txt";
	
	private HashMap<String, Integer> keywordGenerator = new HashMap<String, Integer>();
	
	private HashMap<String, LinkedHashMap<String, Integer>> dictionary = new HashMap<String, LinkedHashMap<String, Integer>>();
 
	private Map<String, Double> invertedIndex = new TreeMap<String, Double>();
	
	private HashMap<String, ArrayList<Integer>> vectorMapping = new HashMap<String, ArrayList<Integer>>();
	
	public String[] keywords = {};
	
	public FrequencyGenerator(String[] keywords) {
		this.loadExistingIndexing();
		this.keywords = keywords.clone();
		this.frequencyCountGenerator();
		this.getInvertedIndex();
	}
	

	private void loadExistingIndexing() {
		File indexFile = new File(this.indexFilePath);
		if (!indexFile.exists()) {
			try {
				indexFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			FileReader reader = new FileReader(indexFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String keyword = line.split("#")[0];
				String[] documents = line.split("#")[1].split(",");
				LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
				for (String document : documents) {
					String documentHash = document.split(" ")[0];
					Integer occuerence = Integer.parseInt(document.split(" ")[1]);
					linkedHashMap.put(documentHash, occuerence);
				}
				this.dictionary.put(keyword, linkedHashMap);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	private void frequencyCountGenerator() {
		for(Map.Entry<String, LinkedHashMap<String, Integer>> document : this.dictionary.entrySet()) {
			String keyword = document.getKey();
			Integer count = 0;
			for (@SuppressWarnings("unused") Entry<String, Integer> linkList : document.getValue().entrySet()) {
				count += linkList.getValue();
			}
			this.keywordGenerator.put(keyword, count);
		}
	}
	
	
	private void getInvertedIndex() {
		ArrayList<Integer> vectorOne = new ArrayList<Integer>();
		for (String search : this.keywords) {
			search = search.trim().toLowerCase();
			if (this.keywordGenerator.containsKey(search)) {
				vectorOne.add(this.keywordGenerator.get(search));
			} else {
				vectorOne.add(0);
			}
			LinkedHashMap<String, Integer> documentList = this.dictionary.get(search);
			ArrayList<Integer> arrayList = new ArrayList<>();
			if (documentList != null) {
				for (Map.Entry<String, Integer> document : documentList.entrySet()) {
					if (this.vectorMapping.containsKey(document.getKey())) {
						arrayList = this.vectorMapping.get(document.getKey());
					} else {
						arrayList = new ArrayList<>();						
					}
					arrayList.add(document.getValue());
					this.vectorMapping.put(document.getKey(), arrayList);
				}
			}
		}
		for (Entry<String, ArrayList<Integer>> vectors : this.vectorMapping.entrySet()) {
			this.invertedIndex.put(vectors.getKey(), this.calculateCosineSimilarity(vectorOne, vectors.getValue()));
		}
	}
	
	
	private double calculateCosineSimilarity(ArrayList<Integer> vectorOne, ArrayList<Integer> vectorTwo) {
		if (vectorOne.size() != vectorTwo.size()) {
			int difference = vectorOne.size() - vectorTwo.size();
			difference = Math.abs(difference);
			if (vectorOne.size() > vectorTwo.size()) {
				for (int i=0; i< difference; i++) {
					vectorTwo.add(0);
				}
			} else {
				for (int i=0; i< difference; i++) {
					vectorOne.add(0);
				}
			}
		}
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorOne.size(); i++) {
	    	dotProduct += vectorOne.get(i) * vectorTwo.get(i);
	        normA += Math.pow(vectorOne.get(i), 2);
	        normB += Math.pow(vectorTwo.get(i), 2);
	    }
	    double product = Math.sqrt(normA) * Math.sqrt(normB);
	    if (product != 0) {
	    	return dotProduct / (product);
	    }
	    return 0.0;
	}

	public Map<String, Double> run() {
		return this.invertedIndex;
	}
}
