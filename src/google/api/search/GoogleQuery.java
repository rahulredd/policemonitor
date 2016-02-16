package google.api.search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class GoogleQuery {

	public JSONArray makeQuery(int i ,String query) throws IOException, JSONException   {
	//public List<JSONObject> makeQuery(String query) throws IOException, JSONException   {


		System.out.println("Querying for " + query);
		query = URLEncoder.encode(query, "UTF-8");
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
			URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?start="+i+"&rsz=large&v=1.0&q=" + query);
			URLConnection connection = url.openConnection();
			// Get the JSON response
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			while((line = reader.readLine()) != null) {
				builder.append(line);
			}

			String response = builder.toString();
			JSONObject json = new JSONObject(response);
			jsonList.add(json);
			System.out.println("Total results = " +json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount"));
			JSONArray ja = json.getJSONObject("responseData").getJSONArray("results");
			System.out.println("\nResults:");
			return ja;

	}
	public List<String> getLatandLong(String city) throws IOException, JSONException {
		List<String> list = new ArrayList<String>();

		String link = "http://maps.googleapis.com/maps/api/geocode/json?address="+city;
		URL url = new URL(link);
		URLConnection connection = url.openConnection();
		// Get the JSON response
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		String response = builder.toString();
		JSONObject json = new JSONObject(response);
		JSONArray ja = json.getJSONArray("results");
		JSONObject val = ja.getJSONObject(0);
		String lat =  val.getJSONObject("geometry").getJSONObject("location").get("lat").toString();
		String lng =  val.getJSONObject("geometry").getJSONObject("location").get("lng").toString();
		list.add(city);
		list.add(lat);
		list.add(lng);

		return list;
	}
	public void FileUpload(String csv) {
		String server = "rahulproject.t15.org";
		int port = 21;
		String user = "u138698187";
		String pass = "123456";

		FTPClient ftpClient = new FTPClient();
		try {

			 ftpClient.connect(server, port);
			 boolean log = ftpClient.login(user, pass);
			 System.out.println(log);
			 
			ftpClient.enterLocalPassiveMode();
			
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// APPROACH #1: uploads first file using an InputStream
			String firstLocalFile = csv;

			FileInputStream inputStream = new FileInputStream(firstLocalFile);

			System.out.println("Start uploading csv");
			boolean done = ftpClient.storeFile("data.csv", inputStream);
			System.out.println(done);
			inputStream.close();
			if (done) {
				System.out.println("The csv is uploaded successfully.");
			}
		}
	   catch (IOException ex) {
          System.out.println("Error: " + ex.getMessage());
          ex.printStackTrace();
      } finally {
          try {
              if (ftpClient.isConnected()) {
                  ftpClient.logout();
                  ftpClient.disconnect();
              }
          } catch (IOException ex) {
              ex.printStackTrace();
          }
      }
	}
	

}

