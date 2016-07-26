/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Activity inicial. Busca informacoes do servidor.
 * Caso seja o primeiro acesso, cria o usuario. 
 * Caso tenha atualizacao na lista de politicos, busca e salva nova lista.
 * 
 */
package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.DataBaseHelper;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends Activity {

	public ProgressBar pb = null;
	private Bundle bundle;
    private DataBaseHelper dbh;
    private PoliticoDAO politicoDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //printKeyHash(SplashActivity.this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
        setContentView(R.layout.activity_splash);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.VISIBLE);
        bundle = getIntent().getExtras();

        dbh = new DataBaseHelper(SplashActivity.this);
        try {
            politicoDAO = new PoliticoDAO(dbh.getConnectionSource());

            //verifica se eh a primeira vez
            if(isPrimeiraVez()){
                //salva usuario
                salvaUsuario();

            }else{
                //verifica se tem atualizacao
                verificaAtualizacao();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }

	}

    @Override
    public void onDestroy(){
        super.onDestroy();
        dbh.close();
    }

    public void verificaAtualizacao(){
        AppController.getInstance().setIdUsuario();
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
                                    try {
                                        dbh.clearTables(dbh.getConnectionSource());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    //buscar lista atualizada de parlamentares
                                    buscaParlamentares();

                                }else{
                                    Date dataUltimaAtualizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dt);
                                    if (dataAtualizacao.after(dataUltimaAtualizacao)) {
                                        try {
                                            dbh.clearTables(dbh.getConnectionSource());
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        //salva data
                                        salvaData(json.getString("dtAtualizacao"));
                                        //buscar lista atualizada de parlamentares
                                        buscaParlamentares();
                                    }else{
                                        inicializaApp();
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

        };
        request.setTag("tag");
        ((AppController) getApplicationContext()).getRq().add(request);

    }

    public void salvaData(String data){

        Log.i("monitora", "salvaData()"+data);
        SharedPreferences.Editor editor = ((AppController) getApplicationContext()).getSharedPref().edit();
        editor.putString(getString(R.string.id_key_dt_atualicao), data);
        editor.commit();
    }

    public String getDtAtualiacao() {
        String dt = ((AppController) getApplicationContext()).getSharedPref().
                getString(getString(R.string.id_key_dt_atualicao), null);
        return dt;
    }

    public void inicializaApp(){
        //inicia o intent
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public boolean isPrimeiraVez(){
        int id = ((AppController) getApplicationContext()).getSharedPref().
                getInt(getString(R.string.id_key_idcadastro_novo), 0);

        if(id>0)
            return false;
        else
            return true;
    }

    public void salvaUsuario(){
        Log.i("monitora", "salvaUsuario()");
        Usuario user = new UserDAO(getApplicationContext()).salvaUsuarioNovo();
        SharedPreferences.Editor editor = AppController.getInstance().getSharedPref().edit();
        editor.putInt(getString(R.string.id_key_idcadastro_novo), user.getId());
        Gson gson = new Gson();
        editor.putString(getString(R.string.id_key_user), gson.toJson(user));
        editor.commit();
        salvaData(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        AppController.getInstance().setIdUsuario();
        inicializaApp();
    }

    public void buscaParlamentares(){
        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/politico_getall.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Politico> politicos = gson.fromJson(response, new TypeToken<ArrayList<Politico>>() {
                        }.getType());
                        for(Politico p : politicos){
                            try {
                                politicoDAO.create(p);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        inicializaApp();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb.setVisibility(View.GONE);
                    }
                }) {

        };
        request.setTag("tag");
        AppController.getInstance().addToRequestQueue(request);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
