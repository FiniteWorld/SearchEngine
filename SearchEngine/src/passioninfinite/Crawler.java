package passioninfinite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {

	private String link;

	private StringBuilder content = new StringBuilder();

	private BufferedReader bufferedReader;
	
	private String pattern = "<loc>(.+)?</loc>";
	
	private Pattern regex;
	
	private File links = null;

	public Crawler(String link) {
		this.link = link;
		regex = Pattern.compile(this.pattern);
		String linkPath = Paths.get("output/meta/links.txt").toString();
		this.links = new File(linkPath);
		if(!this.links.exists()) {
			try {
				this.links.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		this.parseURLs();
	}
	
	private void parseURLs() {
		URL url = null;
		HttpURLConnection connection = null;

		try {
			url = new URL(this.link);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		try {
			connection = (HttpURLConnection) url.openConnection();
			this.bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			FileWriter writer = new FileWriter(this.links, true);
			while((line = this.bufferedReader.readLine()) != null) {
				Matcher matcher = this.regex.matcher(line);
				if (matcher.find()) {
					URL checkUrl = new URL(matcher.group(1));
					HttpURLConnection checkConnection = (HttpURLConnection) checkUrl.openConnection();
					if (checkConnection.getResponseCode() == 200) {
						writer.write(matcher.group(1));
						writer.write(System.lineSeparator());
						System.out.println("Crawled: "+matcher.group(1));
					} else {
						checkConnection.disconnect();
					}
				}
			}
			writer.close();
			this.bufferedReader.close();			
			this.generateHTMLFiles(this.links);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	private void generateHTMLFiles(File links) {
		try {
			this.bufferedReader = new BufferedReader(new FileReader(links));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		String line;
		try {
			while((line = this.bufferedReader.readLine()) != null) {
				String fileName = this.filterUrlToFileName(line);
				File htmlFile = new File("output/html_files/"+fileName+".html");
				if (!htmlFile.exists()) {
					URL url = new URL(line);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					if (connection.getResponseCode() == 200) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String response;
						while((response = reader.readLine()) != null) {
							this.content.append(response);
						}
						reader.close();
						htmlFile.createNewFile();
						System.out.println("Generating HTML File :"+fileName);
						FileWriter writer = new FileWriter(htmlFile);
						writer.write(this.content.toString());
						writer.close();
					} else {
						connection.disconnect();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String filterUrlToFileName(String url) {
		return Encrypter.encrypt(url, "ACCProject");
	}
}
