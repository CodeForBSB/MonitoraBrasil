package com.gamfig.monitorabrasil.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.gamfig.monitorabrasil.classes.Evento;
import com.gamfig.monitorabrasil.classes.Hashtag;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class MonitoraDAO {

	public MonitoraDAO() {
	}





	/**
	 * busca os projetos mais votados ou mais comentados
	 * 
	 * @param tipo
	 * @return
	 */
	public List<Projeto> buscaProjetos(String string) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/getinfomain.php?acao=" + string);

		// Request parameters and other properties.

		HttpResponse response;
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();

				String result = DeputadoDAO.convertStreamToString(instream);
				Gson gson = new Gson();

				List<Projeto> projetos = gson.fromJson(result, new TypeToken<ArrayList<Projeto>>() {
				}.getType());

				return projetos;
			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
			e.printStackTrace();
		} catch (JsonParseException e) {
		}
		return null;
	}

	/**
	 * busca os politicos que mais gastam
	 * 
	 * @return
	 */
	public List<Politico> buscaPoliticosMaisGastam() {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/getinfomain.php?acao=maisgastam");

		// Request parameters and other properties.

		HttpResponse response;
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();

				String result = DeputadoDAO.convertStreamToString(instream);
				Gson gson = new Gson();

				List<Politico> politicos = gson.fromJson(result, new TypeToken<ArrayList<Politico>>() {
				}.getType());

				return politicos;
			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
			e.printStackTrace();
		} catch (JsonParseException e) {
		}
		return null;

	}

}
