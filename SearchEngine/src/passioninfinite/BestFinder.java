package passioninfinite;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BestFinder {
	
	private Map<String, Double> links = new TreeMap<String, Double>();
	
	public BestFinder(Map<String, Double> links) {
		this.links = links;
	}
	
	public void run() {
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(this.links.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		this.display(list);
	}
	

	private void display(List<Entry<String, Double>> links) {
		int count = 10;
		
		if (links.size() < count) {
			for(int i=0; i<links.size(); i++) {
				if (links.get(i).getValue() != 0) {
					System.out.println(this.filterFileNameToUrl(links.get(i).getKey()));
				}
			}
		} else {
			for(int i=0; i< count; i++) {
				if (links.get(i).getValue() != 0) {
					System.out.println(this.filterFileNameToUrl(links.get(i).getKey()));
				}
			}
		}
	}
	
	private String filterFileNameToUrl(String fileName) {
		fileName = fileName.replace(".html", "");
		return Encrypter.decrypt(fileName, "ACCProject");
	}

}
