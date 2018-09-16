package passioninfinite;

import java.util.Map;
import java.util.Scanner;

public class Search {

	public static void main(String[] args) {
//		System.out.println("What do you want to do?");
//		System.out.println("1. Crawl url to get into search engine.");
//		System.out.println("2. Perform a search.");
//		System.out.println("Enter your choice:");
		Scanner scanner = new Scanner(System.in);
		int caseNumber = 2;
//		scanner.nextLine();
		
		switch(caseNumber) {
		case 1:
//			System.out.println("Enter url to crawl:");
//			String url = scanner.nextLine();

//			Crawler crawler = new Crawler(url);
//			crawler.run();
//			Converter converter = new Converter();
//			converter.run();
			break;
		case 2:
			System.out.println("Enter your search query: ");
			String query = scanner.nextLine();

			String[] keywords = query.split(" ");
			
			long startTime = System.currentTimeMillis();
			KeywordGenerator generator = new KeywordGenerator(keywords);
			String[] searchKeywords = generator.run();
			
			FrequencyGenerator frequencyMapper = new FrequencyGenerator(searchKeywords);
			Map<String, Double> documents = frequencyMapper.run();
			
			BestFinder finder = new BestFinder(documents);
			finder.run();
			
			long endTime = System.currentTimeMillis();
			System.out.println("\n\n\nTime taken to perform search is "+(endTime - startTime)+" ms");
			break;
		default:
			System.out.println("Enter vaild choice!");
			break;
		}
		scanner.close();
	}
}
