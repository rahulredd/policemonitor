package google.api.search;


import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import au.com.bytecode.opencsv.CSVWriter;


public class RetrieveArticles {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException, JSONException, URISyntaxException {
		// TODO Auto-generated method stub

		String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"; // Change this to your company's name and bot homepage!
		String csv = "data.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(csv),',',CSVWriter.NO_QUOTE_CHARACTER);
		List<String> urls = new ArrayList<String>();
		List<List> allCities = new ArrayList<List>();
		List<String> cities = new ArrayList<String>();
		
		cities.add(“Chicago);
		cities.add("NewYork");
		cities.add("Miami");
		cities.add("Denver");
		cities.add("Phoenix");
		cities.add("Dallas");
		cities.add("LosAngeles");

		System.setProperty("jsse.enableSNIExtension", "false");

		GoogleQuery googleQuery = new GoogleQuery();
		for(int i = 0; i < cities.size(); i++) {
			String search = "shootings against unarmed miniorities in "+ cities.get(i)+ " in 2014";
			List<String> list = googleQuery.getLatandLong(cities.get(i));
			int count = 0;
			for(int k = 0; k < 30; k=k+10) {
				JSONArray ja = googleQuery.makeQuery(k, search);
				
				for(int j = 0; j <ja.length(); j++) {
					JSONObject jo = ja.getJSONObject(j);
					String title = jo.getString("titleNoFormatting");
					String url  = jo.getString("unescapedUrl");	
					Connection connection = Jsoup.connect(url).userAgent(userAgent);
					connection.timeout(1000000000); // timeout in millis
					Document doc = connection.ignoreContentType(true).get();
					if(doc.text()!=null && doc.text().contains("police") && doc.text().contains("grand jury")) {
						if(urls.contains(url)) {
							continue;
						} else {
							urls.add(url);
							System.out.println(url);
							count++;
						}
					}
				}
			}
			list.add(String.valueOf(count));
			System.out.println(count);
			
			StringBuilder sb = new StringBuilder();
			for(String str: list) {
				if(sb.length()!=0) {
					sb.append(",");
				}	
				sb.append(str);
			}
			writer.writeNext(sb.toString().split(","));
			allCities.add(list);
		}
		
		writer.close();
		googleQuery.FileUpload(csv);
		Thread.sleep(10000);
		Desktop.getDesktop().browse(new URI("http://rahulproject.t15.org/map.html"));
	}
}

