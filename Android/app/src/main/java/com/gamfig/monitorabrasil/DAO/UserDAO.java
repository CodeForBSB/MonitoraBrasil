package com.gamfig.monitorabrasil.DAO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Comentario;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class UserDAO {
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
    }

    public UserDAO() {
        // TODO Auto-generated constructor stub
    }

    public void salvaProjetoMonitorado(Projeto projeto, boolean isChecked) {
        HashMap<Integer, Projeto> projetos = new HashMap<Integer, Projeto>();
        Gson gson = new Gson();
        String projetosJson = getFromShared(R.string.id_key_projetos_fav);
        if (projetosJson != null) {
            projetos = gson.fromJson(projetosJson, new TypeToken<HashMap<Integer, Projeto>>() {
            }.getType());
        }
        if (isChecked)
            projetos.put(projeto.getId(), projeto);
        else {
            if (projetos.containsKey(projeto.getId())) {
                projetos.remove(projeto.getId());
            }

        }
        String projetosFav = gson.toJson(projetos);
        salvaInSharedPreferences(projetosFav, R.string.id_key_projetos_fav);
    }

    public Projeto buscaProjetoSalvo(Projeto projeto) {
        HashMap<Integer, Projeto> projetos = new HashMap<Integer, Projeto>();
        Gson gson = new Gson();
        String projetosJson = getFromShared(R.string.id_key_projetos_fav);
        if (projetosJson != null) {
            projetos = gson.fromJson(projetosJson, new TypeToken<HashMap<Integer, Projeto>>() {
            }.getType());
            if (projetos.containsKey(projeto.getId()))
                return projetos.get(projeto.getId());
        } else {
            return null;
        }
        return null;
    }

    public boolean estaMonitorado(int idProjeto) {
        HashMap<Integer, Projeto> projetos = new HashMap<Integer, Projeto>();
        Gson gson = new Gson();
        String projetosJson = getFromShared(R.string.id_key_projetos_fav);
        if (projetosJson != null) {
            projetos = gson.fromJson(projetosJson, new TypeToken<HashMap<Integer, Projeto>>() {
            }.getType());
            if (projetos.containsKey(idProjeto))
                return true;
        } else {
            return false;
        }
        return false;
    }

    // salva o politico monitorado
    public void salvaMonitorado(Politico pol, boolean isChecked) {

        Map<Integer, Politico> politicosFav = getPoliticosFavoritos();

        // List<Politico> politicosFav = getPoliticosFavoritos();
        // inserir no sharedPref o array de favoritos o idCadastro
        // do politico

        if (politicosFav == null) {
            politicosFav = new HashMap<Integer, Politico>();
        }

        Politico p = new Politico();
        p.setNome(pol.getNome());
        p.setIdCadastro(pol.getIdCadastro());
        p.setTwitter(pol.getTwitter());
        p.setTipo(pol.getTipo());

        if (isChecked) {
            if (politicosFav.get(pol.getIdCadastro()) == null)
                politicosFav.put(pol.getIdCadastro(), p);
        } else {
            // deleta o politico favorito
            politicosFav.remove(pol.getIdCadastro());

        }

        // salva o conjunto
        Gson gson = new Gson();
        String polFav = gson.toJson(politicosFav);
        salvaInSharedPreferences(polFav, R.string.id_key_favoritos);
        int idUser = new UserDAO(context).getIdUser();
        String acao = "del";
        if (isChecked)
            acao = "add";
        new DeputadoDAO(context).marcaFavorito(idUser, pol.getIdCadastro(), acao);
    }

    public boolean isPoliticoMonitorado(int id) {
        Map<Integer, Politico> politicosFav = getPoliticosFavoritos();
        if (politicosFav == null) {
            return false;
        }
        return politicosFav.containsKey(id);
    }

    private void salvaInSharedPreferences(String objeto, int key) {
        AppController appController = AppController.getInstance();
        SharedPreferences.Editor editor = appController.getSharedPref().edit();
        editor.putString(appController.getString(key), objeto);
        editor.commit();
    }

    public void salvaUserCompleto(Usuario user) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getApplicationContext().getString(R.string.id_key_user), gson.toJson(user));
        editor.commit();
    }

    public void salvaVoto(String voto, String idProposta) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);

        HashMap<String, String> votos = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_votos), null),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
        if (votos == null)
            votos = new HashMap<String, String>();
        votos.put(idProposta, voto);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getApplicationContext().getString(R.string.id_key_votos), gson.toJson(votos));
        editor.commit();
    }

    public float buscaAvaliacaoSalva(int idPolitico) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
        HashMap<Integer, Float> avaliacoes = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_avaliacoes), null),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
        if (avaliacoes == null)
            return 0;
        if (avaliacoes.containsKey(idPolitico))
            return avaliacoes.get(idPolitico);
        return 0;
    }

    public void salvaAvaliacaoPolitico(int idPolitico, float rating) {
        Log.i(PrincipalActivity.TAG, String.valueOf(idPolitico) + " " + String.valueOf(rating));
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
        HashMap<Integer, Float> avaliacoes = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_avaliacoes), null),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
        if (avaliacoes == null)
            avaliacoes = new HashMap<Integer, Float>();
        avaliacoes.put(idPolitico, rating);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getApplicationContext().getString(R.string.id_key_votos), gson.toJson(avaliacoes));
        editor.commit();

    }

    public void salvaComentarioMonitorado(Comentario comentario) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);

        List<Integer> comentarios = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_comentarios), null),
                new TypeToken<List<Integer>>() {
                }.getType());
        if (comentarios == null)
            comentarios = new ArrayList<Integer>();
        if (comentario.isMonitarado()) {
            comentarios.add(Integer.valueOf(comentario.getId()));
        } else {
            comentarios.remove(Integer.valueOf(comentario.getId()));
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getApplicationContext().getString(R.string.id_key_comentarios), gson.toJson(comentarios));
        editor.commit();

    }

    /**
     * Verifica se o comentario � monitorado
     *
     * @param idComentario
     * @return
     */
    public boolean isMonitorados(int idComentario) {
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);

        List<Integer> comentarios = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_comentarios), null),
                new TypeToken<List<Integer>>() {
                }.getType());
        if (comentarios == null) {
            return false;
        }
        return comentarios.contains(Integer.valueOf(idComentario));
    }

    public HashMap<String, String> buscaVotos() {
        Gson gson = new Gson();

        context.getApplicationContext();
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
        return gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_votos), null), new TypeToken<HashMap<String, String>>() {
        }.getType());

    }

    public String getFromShared(int key) {
        try {
            context.getApplicationContext();
            SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                    Context.MODE_PRIVATE);
            return sharedPref.getString(context.getApplicationContext().getString(key), null);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;

    }

    public Map<Integer, Politico> getPoliticosFavoritos() {
        // salvaInSharedPreferences(new HashMap<Integer, Politico>());
        Gson gson = new Gson();

        Map<Integer, Politico> politicosFav = new HashMap<Integer, Politico>();
        try {
            // inserir no sharedPref o array de favoritos o idCadastro
            // do politico

            context.getApplicationContext();
            SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                    Context.MODE_PRIVATE);
            politicosFav = gson.fromJson(sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_favoritos), gson.toJson(politicosFav)),
                    new TypeToken<Map<Integer, Politico>>() {
                    }.getType());


            //veriricar se tem politico que nao esta mais no cargo
            if(politicosFav.size()>0){
                limpaFavoritos(politicosFav);
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

        return politicosFav;
    }

    private void limpaFavoritos( Map<Integer, Politico> politicosFav){
        int size = politicosFav.size();

        try {
            PoliticoDAO politicoDAO = new PoliticoDAO(AppController.getInstance().getDbh().getConnectionSource());

            for (Map.Entry<Integer, Politico> entry : politicosFav.entrySet())
            {
                int id = entry.getKey();
                if(null == politicoDAO.getPolitico(id)){
                    //retirar do sharedPreferences
                    politicosFav.remove(id);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(size > politicosFav.size()){
            Gson gson = new Gson();
            String polFav = gson.toJson(politicosFav);
            salvaInSharedPreferences(polFav, R.string.id_key_favoritos);
        }



    }

    private File cacheDir;

    // salva foto do deputado
    // id==0 eh a foto do usuario
    public void salvaFotoDeputado(int idDeputado, Bitmap bmp) {
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir, "data/MonitoraBrasil");
        } else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        File file = new File(cacheDir, idDeputado + ".mbr");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Bitmap buscaFotoCache(int idDeputado) {
        String filename = idDeputado + ".mbr";
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir, "data/MonitoraBrasil");
        } else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();
        File f = new File(cacheDir, filename);
        // Is the bitmap in our cache?
        Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
        if (bitmap != null)
            return bitmap;
        return null;

    }

    public List<Politico> atualizaListaFavoritos(List<Politico> listaPoliticos) {
        Map<Integer, Politico> politicosFavoritos = getPoliticosFavoritos();
        if (politicosFavoritos != null) {
            for (Politico politico : listaPoliticos) {
                if (politicosFavoritos.get(politico.getIdCadastro()) != null)
                    listaPoliticos.get(listaPoliticos.indexOf(politico)).setMonitorado(true);
            }
        }
        return listaPoliticos;
    }



    public Usuario salvaUsuarioNovo() {

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/insere_user_novo.php");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("so", "Android"));
        HttpResponse response;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = DeputadoDAO.convertStreamToString(instream);
                instream.close();
                Gson gson = new Gson();

                // retorna o id criado
                return gson.fromJson(result, Usuario.class);
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {

            e.printStackTrace();
            e.printStackTrace();
        }
        return null;
    }

    public Usuario getUserCompleto() {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(sharedPref.getString(context.getString(R.string.id_key_user), null), Usuario.class);
    }

    public int getIdUser() {
        try{
            return AppController.getInstance().getSharedPref().getInt(context.getString(R.string.id_key_idcadastro_novo), 0);

        }catch (Exception e){
            Crashlytics.logException(e);
            Crashlytics.log("getIdUser()");
        }
        return 0;
    }

    public void atualizaGcm(String regid) {
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/atualizagcm.php");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("gcmid", regid));
        params.add(new BasicNameValuePair("id", String.valueOf(getIdUser())));
        HttpResponse response;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                DeputadoDAO.convertStreamToString(instream);

                // retorna o id criado
                return;
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {

            e.printStackTrace();
            e.printStackTrace();
        }
        return;

    }

    public void atualizaUsuario(Usuario user) {
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/user_atualiza.php");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(9);
        params.add(new BasicNameValuePair("id", String.valueOf(getIdUser())));
        params.add(new BasicNameValuePair("nome", user.getNome()));
        params.add(new BasicNameValuePair("email", user.getEmail()));
        params.add(new BasicNameValuePair("faixaetaria", user.getFaixaEtaria()));
        params.add(new BasicNameValuePair("sexo", String.valueOf(user.getSexo())));
        params.add(new BasicNameValuePair("uf", user.getUf()));
        params.add(new BasicNameValuePair("idgoogle", user.getIdGoogle()));
        params.add(new BasicNameValuePair("idfacebook", user.getIdFacebook()));
        params.add(new BasicNameValuePair("gcm", user.getReceberNotificacao()));

        Log.i(PrincipalActivity.TAG, user.getReceberNotificacao());
        HttpResponse response;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                DeputadoDAO.convertStreamToString(instream);

                salvaUserCompleto(user);
                return;
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {

            e.printStackTrace();
            e.printStackTrace();
        }
        return;

    }

    public void buscaPontuacao() {
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost(DeputadoDAO.url + "rest/busca_pontos.php");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        Usuario user = getUserCompleto();
        params.add(new BasicNameValuePair("id", String.valueOf(user.getId())));
        HttpResponse response;
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();

                String result = DeputadoDAO.convertStreamToString(instream);
                Gson gson = new Gson();

                // retorna o id criado
                Usuario pontos = gson.fromJson(result, Usuario.class);
                user.setPontos(pontos.getPontos());
                user.setNrComentarios(pontos.getNrComentarios());
                user.setNrVotos(pontos.getNrVotos());
                salvaUserCompleto(user);
                return;
            }

        } catch (ClientProtocolException e) {
        } catch (IOException e) {

            e.printStackTrace();
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }
        return;

    }

    public JSONObject temAtualizacao() {
        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/get_configuracao.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (json != null) {
                            try {
                                Date dataAtualizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(json.getString("dtAtualizacao"));
                                String dt = getDtAtualiacao();
                                if(null == dt){
                                    //buscar lista atualizada de parlamentares

                                }else{
                                    Date dataUltimaAtualizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dt);
                                    if (dataAtualizacao.after(dataUltimaAtualizacao)) {
                                        //buscar lista atualizada de parlamentares

                                    }
                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }
        };
        request.setTag("tag");

        ((AppController) this.context.getApplicationContext()).getRq().add(request);

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        String result = new Dispatcher(DeputadoDAO.url + "rest/get_configuracao.php", params).getInformacaoPOST();
        try {
            if(result != null)
                return new JSONObject(result);
            else
                return null;
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    /**
     * busca a data de atualizacao
     */
    public String getDtAtualiacao() {
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);

        String dt = sharedPref.getString(context.getApplicationContext().getString(R.string.id_key_dt_atualicao), null);
        return dt;
    }

    public boolean jaAbriu(int idKeySobredebate) {
        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getString(R.string.id_key_preferencias),
                Context.MODE_PRIVATE);
        boolean jaAbriu = sharedPref.getBoolean(context.getApplicationContext().getString(idKeySobredebate), false);
        if (!jaAbriu) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(context.getApplicationContext().getString(idKeySobredebate), true);
            editor.commit();
        }
        return jaAbriu;
    }

    // Busca a pontuacaoo do usuario
    public Usuario getPontuacao(Usuario user) {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("id", String.valueOf(user.getId())));
        String result = new Dispatcher(DeputadoDAO.url + "rest/get_user_pontuacao.php", params).getInformacaoPOST();
        try {
            Gson gson = new Gson();
            return gson.fromJson(result, Usuario.class);
        } catch (JsonParseException e) {

        }
        return user;
    }

    /**
     * Buscar ranking de usuarios por p�gina
     *
     * @param pagina
     */
    public List<Usuario> buscaRankingUsers(int pagina) {

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("pag", String.valueOf(pagina)));
        String result = new Dispatcher(DeputadoDAO.url + "rest/rank_user.php", params).getInformacaoPOST();
        try {
            Gson gson = new Gson();
            return gson.fromJson(result, new TypeToken<ArrayList<Usuario>>() {
            }.getType());
        } catch (JsonParseException e) {

        }
        return null;

    }

    /**
     * insere like ou unlike em comentarios
     *
     * @param idComentario
     * @param idUser
     * @param like
     *            = 1 unlike = -1
     */
    public void likeComentario(int idComentario, int idUser, int like) {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("acao", String.valueOf(like)));
        params.add(new BasicNameValuePair("idUser", String.valueOf(idUser)));
        params.add(new BasicNameValuePair("idComentario", String.valueOf(idComentario)));
        new Dispatcher(DeputadoDAO.url + "rest/like_comentario.php", params).getInformacaoPOST();

        HashMap<Integer, Integer> likes = new HashMap<Integer, Integer>();
        Gson gson = new Gson();
        String likesJson = getFromShared(R.string.id_key_likes_comentarios);
        if (likesJson != null) {
            likes = gson.fromJson(likesJson, new TypeToken<HashMap<Integer, Integer>>() {
            }.getType());
        }
        likes.put(idComentario, like);

        likesJson = gson.toJson(likes);
        salvaInSharedPreferences(likesJson, R.string.id_key_likes_comentarios);

        return;

    }

    public HashMap<Integer, Integer> buscaLikes() {
        Gson gson = new Gson();
        String likesJson = getFromShared(R.string.id_key_likes_comentarios);
        if (likesJson != null) {
            return gson.fromJson(likesJson, new TypeToken<HashMap<Integer, Integer>>() {
            }.getType());
        }
        return null;
    }

}
