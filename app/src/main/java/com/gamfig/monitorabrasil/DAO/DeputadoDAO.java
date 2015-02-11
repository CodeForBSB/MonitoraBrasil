
package com.gamfig.monitorabrasil.DAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.classes.Bem;
import com.gamfig.monitorabrasil.classes.Comentario;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.Doacao;
import com.gamfig.monitorabrasil.classes.Doador;
import com.gamfig.monitorabrasil.classes.Hashtag;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.MediaCotas;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.classes.Votacao;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class DeputadoDAO {
	private Context context;
	public final static String url = "http://www.gamfig.com/mbrasilwsdl/";

	public DeputadoDAO(Context context) {
		this.context = context;
	}

	public DeputadoDAO() {
	}

	public Politico buscaPolitico(Politico politico) {
		int casa = 0;
		if (politico.getTipoParlamentar().equals("s")) {
			casa = R.string.pref_listasenadores;
		} else {
			casa = R.string.pref_listadeputados;
		}
		List<Politico> politicos = buscaPoliticosSalvos(casa);
		for (Politico politico2 : politicos) {
			if (politico.getNome() != null)
				if (politico.getNome().equals(politico2.getNome())) {
					return politico2;
				}
			if (politico.getIdCadastro() == politico2.getIdCadastro())
				return politico2;
			if (politico.getTwitter() != null) {
				if (politico.getTwitter().equals(politico2.getTwitter()))
					return politico2;
			}
		}
		return null;
	}

	/**
	 * Busca os politicos salvos - Deputados ou Senadores
	 * 
	 * @param casa
	 * @return
	 */
	public List<Politico> buscaPoliticosSalvos(int casa) {
		List<Politico> listaPoliticos;
		Gson gson = new Gson();
		// verificar se ja tem os deputados salvos
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
		String jsonPoliticos = sp.getString(context.getString(casa), null);

		if (jsonPoliticos == null) {
			return null;
		} else {
			listaPoliticos = gson.fromJson(jsonPoliticos, new TypeToken<ArrayList<Politico>>() {
			}.getType());
		}

		// atualiza os politicos que est�o nos favoritos
		UserDAO userDao = new UserDAO(context);
		listaPoliticos = userDao.atualizaListaFavoritos(listaPoliticos);
		return listaPoliticos;
	}

	/**
	 * Busca as cotas salvas previamente
	 * 
	 * @return
	 */
	public List<Politico> buscaCotasOffline() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null which means there's no more data to
		 * read. Each line will appended to a StringBuilder and returned as String.
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
	 * Busca Pol�ticos
	 * 
	 * @param partido
	 * @param uf
	 * @param i
	 * @param query
	 * @param idUser
	 * @return string com o json dos politicos
	 * 
	 */
	public static String buscaDeputados(String partido, String uf, String i, String query, String idUser) {
		HttpClient httpclient = new DefaultHttpClient();
		String param = "tipo=" + i;
		if (i.equals("2")) {
			param += "&iduser=" + idUser;
		}
		if (null != partido)
			if (!partido.equals("Todos os Partidos")) {
				param += "&partido=" + partido;
			}
		if (null != uf)
			if (!uf.equals("Brasil")) {
				param += "&uf=" + uf;
			}
		if (query != null)
			if (!query.equals("")) {
				param += "&query=" + query;
			}
		HttpGet httpget = new HttpGet(url + "buscadeputados.php?" + param);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONObject json = new JSONObject(result);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);

				return valArray.getString(0);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return null;
	}

	public Drawable getFoto(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Busca o time line.
	 * 
	 * @param query
	 * @param tipo
	 *            se tipo = 1 timeline do politico se tipo = 2 timeline do grupo
	 * @return
	 */
	public static ArrayList<Twitter> buscaTimeLine(String query, int tipo) {
		String urlTipo = "";
		switch (tipo) {
		case 1:
			urlTipo = "index";
			break;
		case 2:
			urlTipo = "getlistatwitter";

		default:
			break;
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "proxytwitter/" + urlTipo + ".php?q=" + query);
		HttpResponse response;
		ArrayList<Twitter> tweets;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				tweets = gson.fromJson(result, new TypeToken<ArrayList<Twitter>>() {
				}.getType());
				// busca imagens
				int i = 0;
				for (Twitter twitter : tweets) {
					tweets.get(i).setFoto(Imagens.getImageBitmap(twitter.getUrlFoto()));
					i++;
				}

				return tweets;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return null;

	}

	public static Politico buscaPolitico(int idCadastro, String casa) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "rest/politico_busca.php?idcadastro=" + String.valueOf(idCadastro) + "&casa=" + casa);
		HttpResponse response;
		Politico politico;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				politico = gson.fromJson(result, Politico.class);

				return politico;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
		}
		return null;

	}

	public Projeto buscaProjeto(int i, String casa) {
		HttpClient httpclient = new DefaultHttpClient();
		String urlComplemento;
		if (casa == null) {
			casa = "c";
		}
		if (casa.equals("s")) {
			urlComplemento = "rest/senado/get_projeto_id.php?id=";
		} else {
			urlComplemento = "rest/proposicao_getbyid.php?id=";
		}
		HttpGet httpget = new HttpGet(url + urlComplemento + i);
		HttpResponse response;
		Projeto projeto;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				projeto = gson.fromJson(result, Projeto.class);

				return projeto;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			// } catch (JSONException e) {
			//
			// e.printStackTrace();
		}
		return null;
	}

	public static String buscaRanking(String partido, String uf, String tpRank, String ano) {
		HttpClient httpclient = new DefaultHttpClient();
		String param = "&ano=" + ano;
		if (!partido.equals("Todos os Partidos")) {
			param += "&partido=" + partido;
		}
		if (!uf.equals("Brasil")) {
			param += "&uf=" + uf;
		}

		HttpGet httpget = new HttpGet(url + "buscarank.php?tprank=" + tpRank + param);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONObject json = new JSONObject(result);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);

				return valArray.getString(0);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return null;
	}

	public static Usuario enviaUser(JSONObject jsonObject, String registrationId) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "insereuser.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("json", jsonObject.toString()));
		params.add(new BasicNameValuePair("gcmId", registrationId));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				Usuario user = gson.fromJson(result, Usuario.class);
				return user;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
			e.printStackTrace();
		}
		return null;
	}

	public void marcaFavorito(int idUser, int idPolitico, String acao) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "admfavorito.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("idfacebook", String.valueOf(idUser)));
		params.add(new BasicNameValuePair("idpolitico", String.valueOf(idPolitico)));
		params.add(new BasicNameValuePair("acao", acao));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();

			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void marcaProjetoFavorito(int idUser, int i, String acao) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "admfavoritoprojeto.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("iduser", String.valueOf(idUser)));
		params.add(new BasicNameValuePair("idprojeto", String.valueOf(i)));
		params.add(new BasicNameValuePair("acao", acao));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void salvaAvaliacaoPolitico(String id, String idProjeto, String acao) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "admfavoritoprojeto.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("idfacebook", id));
		params.add(new BasicNameValuePair("idprojeto", idProjeto));
		params.add(new BasicNameValuePair("acao", acao));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public ArrayList<Projeto> buscaProjetos(String idUser) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "buscaprojetosfavoritos.php?idcadastro=" + idUser);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Projeto> projetos = gson.fromJson(result, new TypeToken<ArrayList<Projeto>>() {
				}.getType());

				return projetos;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return null;

	}

	public void avaliaPolitico(String id, int idCadastro, float notaPolitico) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "avaliapolitico.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("iduser", id));
		params.add(new BasicNameValuePair("idpolitico", String.valueOf(idCadastro)));
		params.add(new BasicNameValuePair("nota", String.valueOf(notaPolitico)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void avaliaProjeto(String idUser, int idProjeto, float notaProjeto) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "avaliaprojeto.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("iduser", idUser));
		params.add(new BasicNameValuePair("idprojeto", String.valueOf(idProjeto)));
		params.add(new BasicNameValuePair("nota", String.valueOf(notaProjeto)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static boolean temAtualizacao(String dtAtualizacao) {
		if (dtAtualizacao == null)
			return true;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "getDataAtualizacao.php?idcadastro=" + dtAtualizacao);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();

				return Boolean.valueOf(result);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return false;
	}

	public void insereVoto(String mVoto, String mIdProposta, int mIdUser) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "rest/votaproposta.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("voto", mVoto));
		params.add(new BasicNameValuePair("idProposta", mIdProposta));
		params.add(new BasicNameValuePair("idUser", String.valueOf(mIdUser)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public ArrayList<Projeto> buscaProjetosVoto(int page, String filtroPar, String uf, String idUser, int idAutor, String casa) {
		String filtro = "";

		if (casa == null) {
			casa = "c";
		}
		if (casa.equals("senado"))
			casa = "s";
		if (idUser == null)
			idUser = "0";
		if (filtroPar.equals("Selecione um filtro")) {
			filtro = "0";
		} else if (filtroPar.equals("Projetos que votei")) {
			filtro = "1";
		} else if (filtroPar.equals("Projetos que n�o votei")) {
			filtro = "2";
		} else if (filtroPar.equals("Projetos Monitorados")) {
			filtro = "3";
		} else if (filtroPar.equals("Projetos Votados na C�mara")) {
			filtro = "4";
		} else if (filtroPar.equals("Projetos mais Comentados")) {
			filtro = "5";
		} else if (filtroPar.equals("Projetos mais Recentes")) {
			filtro = "6";
		} else if (filtroPar.equals("Projetos mais Votados")) {
			filtro = "7";
		} else if (filtroPar.equals("8") && !casa.equals("s")) {
			filtro = "8&idAutor=" + idAutor;
			uf = "Brasil";
		}
		HttpClient httpclient = new DefaultHttpClient();
		String urlCompleta = url + "buscaprojetosvoto.php?page=" + page + "&filtro=" + filtro + "&uf=" + uf + "&iduser=" + idUser + "&idusuario=" + idUser;
		if (casa.equals("s")) {
			if (filtroPar.equals("8"))
				urlCompleta = url + "rest/senado/get_projetos_autor.php?id=" + idAutor;
			else
				urlCompleta = url + "rest/senado/get_projetos_pesquisa.php?mes=" + page;

		}
		Log.i(PrincipalActivity.TAG, urlCompleta);
		HttpGet httpget = new HttpGet(urlCompleta);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Projeto> projetos = gson.fromJson(result, new TypeToken<ArrayList<Projeto>>() {
				}.getType());

				return projetos;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insereComentario(Comentario comentario, String tpComentario) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "rest/inserecomentario.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(5);
		params.add(new BasicNameValuePair("comentario", comentario.getComent()));
		params.add(new BasicNameValuePair("idProposta", comentario.getId()));
		params.add(new BasicNameValuePair("idUser", String.valueOf(comentario.getUser().getId())));
		params.add(new BasicNameValuePair("tipo", tpComentario));
		if (comentario.isMonitarado()) {
			params.add(new BasicNameValuePair("monitorar", "1"));
		}
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

	}

	public void mudaMonintoraComentario(Comentario comentario) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "rest/mudamonitoracomentario.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("idProposta", comentario.getId()));
		params.add(new BasicNameValuePair("idUser", String.valueOf(comentario.getUser().getId())));
		if (comentario.isMonitarado()) {
			params.add(new BasicNameValuePair("monitorar", "1"));
		} else {
			params.add(new BasicNameValuePair("monitorar", "0"));
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Comentario> buscaComentarios(String idProposta, String tpComentario) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "buscacomentarios.php?id=" + idProposta+"&tipo="+tpComentario);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Comentario> projetos = gson.fromJson(result, new TypeToken<ArrayList<Comentario>>() {
				}.getType());

				return buscaComentariosNovos(idProposta, projetos);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Comentario> buscaComentariosNovos(String idProposta, ArrayList<Comentario> comentarios) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "buscacomentariosnovo.php?id=" + idProposta);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Comentario> comentariosNovos = gson.fromJson(result, new TypeToken<ArrayList<Comentario>>() {
				}.getType());
				if (comentarios != null && comentariosNovos != null) {
					comentarios.addAll(comentariosNovos);
				} else {
					if (comentariosNovos != null)
						comentarios = comentariosNovos;
				}
				return comentarios;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Votacao buscaVotacao(String idProposta, String partido, String uf, String voto) {

		if (partido.equals("Todos os Partidos")) {
			partido = "0";
		}

		HttpClient httpclient = new DefaultHttpClient();
		String link = url + "buscavotacao.php?id=" + idProposta + "&partido=" + partido + "&uf=" + uf + "&voto=" + voto;
		HttpGet httpget = new HttpGet(link);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				Votacao votacao = gson.fromJson(result, new TypeToken<Votacao>() {
				}.getType());

				return votacao;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Usuario atualizaUser(int idUser, String registrationId) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "atualizauser.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("idUser", String.valueOf(idUser)));
		params.add(new BasicNameValuePair("gcmId", registrationId));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				Usuario user = gson.fromJson(result, Usuario.class);
				return user;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insereHashtagPolitico(int idUser, String hashtag, int idPolitico) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "hashtag_insere.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("iduser", String.valueOf(idUser)));
		params.add(new BasicNameValuePair("hashtag", hashtag));
		params.add(new BasicNameValuePair("idpolitico", String.valueOf(idPolitico)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// InputStream instream = entity.getContent();
				// String result = convertStreamToString(instream);
				// instream.close();
				// Gson gson = new Gson();
				// List<String> retorno = gson.fromJson(result, new TypeToken<List<String>>() {
				// }.getType());
				return;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return;
	}

	public ArrayList<Doacao> buscaDoacoes(int idCadastro) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "doacao_busca.php?id=" + idCadastro);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Doacao> doacoes = gson.fromJson(result, new TypeToken<ArrayList<Doacao>>() {
				}.getType());

				return doacoes;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * busca as infos da api da tbrasil de um politico
	 *
	 * @return jsonobject
	 */
	public JSONObject getInformacoesTBrasilDeputado(int idTbrasil) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "rest/tbrasil_getexcelencia.php?id=" + idTbrasil);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONObject json = new JSONObject(result);

				return json;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Doador> buscaDoacoesEspecifica(int idCadastro, int idTipo) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "getdoacaodetalhe.php?id=" + idCadastro + "&tipo=" + idTipo);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Doador> doadores = gson.fromJson(result, new TypeToken<ArrayList<Doador>>() {
				}.getType());

				return doadores;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> buscaCotas(String idSubcota) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "rest/cota_getlista.php");

		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("idSubcota", String.valueOf(idSubcota)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream instream = entity.getContent();
			String result = convertStreamToString(instream);
			Gson gson = new Gson();

			Map<String, Object> retorno = new HashMap<String, Object>();

			// pegar os deputados
			JSONObject json = new JSONObject(result);
			ArrayList<Politico> politicos = gson.fromJson(json.getJSONArray("deputados").toString(), new TypeToken<ArrayList<Politico>>() {
			}.getType());
			// pegar a media
			ArrayList<MediaCotas> media = gson.fromJson(json.getJSONArray("media").toString(), new TypeToken<ArrayList<MediaCotas>>() {
			}.getType());

			retorno.put("politicos", politicos);
			retorno.put("media", media);
			instream.close();

			return retorno;

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Cota> buscaCotasAgrupada(int idCadastro) {
		HttpClient httpclient = new DefaultHttpClient();
		String param = "?idcadastro=" + String.valueOf(idCadastro);

		HttpGet httpget = new HttpGet(url + "rest/cota_busca.php" + param);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Cota> cotas = gson.fromJson(result, new TypeToken<ArrayList<Cota>>() {
				}.getType());

				return cotas;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Projeto> buscaProjetosQuery(String query) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url + "buscaprojetoquery.php");

		Log.i(PrincipalActivity.TAG, query);
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);

		params.add(new BasicNameValuePair("query", query));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Projeto> projetos = gson.fromJson(result, new TypeToken<ArrayList<Projeto>>() {
				}.getType());

				return projetos;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * busca hashtag do politico
	 */
	public ArrayList<Hashtag> buscaHashtagPolitico(int idPolitico) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url + "hashtag_busca.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("idpolitico", String.valueOf(idPolitico)));
		HttpResponse response;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				Gson gson = new Gson();
				ArrayList<Hashtag> hashtags = gson.fromJson(result, new TypeToken<ArrayList<Hashtag>>() {
				}.getType());

				return hashtags;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void enviaLikeHash(String voto, int idHashtag, int idUser) {

		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url + "hashtag_inserelike.php");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("voto", voto));
		params.add(new BasicNameValuePair("idHashtag", String.valueOf(idHashtag)));
		params.add(new BasicNameValuePair("iduser", String.valueOf(idUser)));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpclient.execute(httppost);

			return;

		} catch (ClientProtocolException e) {
		} catch (IOException e) {

			e.printStackTrace();
			e.printStackTrace();
		}
	}

	

	public static String buscaSenadores(String partido, String uf) {
		HttpClient httpclient = new DefaultHttpClient();
		String param = "";
		if (null != partido)
			if (!partido.equals("Todos os Partidos")) {
				param += "&partido=" + partido;
			}
		if (null != uf)
			if (!uf.equals("Brasil")) {
				param += "&uf=" + uf;
			}
		HttpGet httpget = new HttpGet(url + "rest/senado/get_senadores.php?" + param);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Log.i(TAG,response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				JSONObject json = new JSONObject(result);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);

				return valArray.getString(0);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * busca os bens da api da tbrasil de um politico
	 * 
	 * @param idCadastro
	 * @param idTipo
	 * @return List<Bem>
	 */
	public static List<Bem> buscaBens(int idTbrasil) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "rest/tbrasil_get_bensexcelencia.php?id=" + idTbrasil);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				instream.close();
				List<Bem> bens = new ArrayList<Bem>();
				Gson gson = new Gson();
				bens = gson.fromJson(result, new TypeToken<ArrayList<Bem>>() {
				}.getType());

				return bens;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
