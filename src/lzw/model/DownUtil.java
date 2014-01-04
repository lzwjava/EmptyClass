package lzw.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class DownUtil{
	static String getStringFromUrl(String url){
		HttpClient httpClient=new DefaultHttpClient();
		HttpGet get=new HttpGet(url);
		StringBuilder sb = new StringBuilder();
		try {
			HttpResponse httpResponse = httpClient.execute(get);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(entity.getContent()));
				String line=null;
				while((line=br.readLine())!=null){
					sb.append(line+"\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	static String getStringFromUrl1(String urlStr){
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader
					(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			} 
		} catch (Exception e) {
			System.out.println(e);
		}
		return sb.toString();
	}
	
	static String getStringFromUrl2(String urlStr){
		StringBuilder sb=new StringBuilder();
		try{
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) 
					url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Charset","UTF-8");
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader
					(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return sb.toString();
	}
}