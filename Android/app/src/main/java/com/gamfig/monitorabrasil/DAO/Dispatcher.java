package com.gamfig.monitorabrasil.DAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dispatcher {
	private String url;
	private List<NameValuePair> params;

	public Dispatcher(String url, List<NameValuePair> params) {
		this.url = url;
		this.params = params;
	}

	public String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null which
		 * means there's no more data to read. Each line will appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Faz requisicao via POST
	 * 
	 * @return String resultado
	 */
	public String getInformacaoPOST() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				return result;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Faz requisicao via GET
	 * 
	 * @return String resultado
	 */
	public String getInformacaoGET() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
