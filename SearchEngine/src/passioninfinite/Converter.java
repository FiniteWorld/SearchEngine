package passioninfinite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;

import rabinkarp.RabinKarp;

public class Converter {

	private String outputPath = "output/txt_files/";

	private String inputPath = "output/html_files/";

	private String keywordsPath = "output/meta/keywords.txt";

	private ArrayList<String> stopWords = new ArrayList<>();

	private ArrayList<String> keywords = new ArrayList<>();

	private String indexFilePath = "output/meta/index.txt";

	private HashMap<String, LinkedHashMap<String, Integer>> dictionary = new HashMap<String, LinkedHashMap<String, Integer>>();

	public Converter() {
		this.loadStopWords();
		this.loadKeywords();
		this.loadExistingIndexing();
	}

	public void run() {
		File[] allFiles = new File(inputPath).listFiles();
		for (File file : allFiles) {
			this.htmlToText(file);
		}

		this.updateIndexing();
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

	private void htmlToText(File file) {
		org.jsoup.nodes.Document document;

		try {
			document = Jsoup.parse(file, "UTF-8");
			String text = document.text();

			String fileName = this.outputPath + file.getName().replaceAll(".html", "") + ".txt";
			File outputFile = new File(fileName);
			if (!outputFile.exists()) {
				try {
					outputFile.createNewFile();
					System.out.println("Generating Text File: " + fileName);
					PrintStream writer = new PrintStream(outputFile);
					writer.print(text);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			String[] keywords = text.split("([^a-zA-Z']+)'*\\1*");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.keywordsPath, true));

			for (String keyword : keywords) {
				keyword = keyword.trim().toLowerCase();
				if (!this.stopWords.contains(keyword)) {
					if (!this.keywords.contains(keyword)) {
						bufferedWriter.write(keyword);
						bufferedWriter.write(System.lineSeparator());
						this.keywords.add(keyword);
					}
				}
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateIndexing() {
		org.jsoup.nodes.Document document;
		File[] allFiles = new File(inputPath).listFiles();
		try {
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(new File(this.indexFilePath)));

			for (String keyword : this.keywords) {
				writer.write(keyword + "#");
				System.out.println("Indexing for keyword: " + keyword);

				for (File file : allFiles) {
					try {
						document = Jsoup.parse(file, "UTF-8");
						String text = document.text().toLowerCase();
						RabinKarp rabinKarp = new RabinKarp(keyword);
						Integer count = rabinKarp.search(text);

						writer.write(file.getName() + " " + count + ",");

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadKeywords() {
		File keywordFile = new File(this.keywordsPath);
		if (!keywordFile.exists()) {
			try {
				keywordFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			BufferedReader bufferedReader = null;
			String line;
			try {
				bufferedReader = new BufferedReader(new FileReader(keywordFile));
				while ((line = bufferedReader.readLine()) != null) {
					this.keywords.add(line.trim());
				}
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadStopWords() {
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("input/stopWords.txt"));
			while ((line = reader.readLine()) != null) {
				this.stopWords.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, LinkedHashMap<String, Integer>> getIndexMapping() {
		return this.dictionary;
	}
}
