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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

	public ProgressBar pb = null;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		setContentView(R.layout.activity_splash);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.VISIBLE);
		bundle = getIntent().getExtras();
		new iniciar().execute();

	}



	public class iniciar extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// busca o json de configuracao
			JSONObject json = new UserDAO(getApplicationContext()).temAtualizacao();

			// verifica se tem debate
			// salvaDebate(json);

			// verifica se eh o primeiro acesso
			if (new UserDAO(getApplicationContext()).isPrimeiraVez()) {
				// baixar lista de deputados
				new BuscaPoliticos().execute();

			} else {
				// verifica se tem atualizacao
				try {
					if (temAtualizacao(json)) {
						// baixar lista de deputados
						new BuscaPoliticos(true).execute();
					} else {
						Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
						if (null != bundle)
							intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;
		}

	}

	public class BuscaPoliticos extends AsyncTask<Void, Void, String> {

		private String deputados;
		private String senadores;
		Usuario user;
		boolean atualizaPoliticos = false;

		public BuscaPoliticos() {

		}

		public BuscaPoliticos(boolean b) {
			this.atualizaPoliticos = b;
		}

		@Override
		protected String doInBackground(Void... params) {
			new DeputadoDAO(getApplicationContext());
			// busca lista de deputados
			deputados = DeputadoDAO.buscaDeputados("Todos os Partidos", "Brasil", "1", "", "");

			// busca lista de senadores
			senadores = DeputadoDAO.buscaSenadores(null, null);

			if (!atualizaPoliticos) {
				// salva usuario como visitante
				user = new UserDAO(getApplicationContext()).salvaUsuarioNovo();
			}
			// buscar as cotas do deputados
			return "ok";

		}

		protected void onPostExecute(String result) {
			try {
				Gson gson = new Gson();
				Context context = getApplicationContext();
				// salvar os politicos
				SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(context.getString(R.string.pref_listadeputados), deputados);
				editor.putString(context.getString(R.string.pref_listasenadores), senadores);
				if (!atualizaPoliticos) {
					// salva usuario
					editor.putInt(getString(R.string.id_key_idcadastro_novo), user.getId());
					editor.putString(getString(R.string.id_key_user), gson.toJson(user));

				}
				// data de atualizacao
				editor.putString(context.getApplicationContext().getString(R.string.id_key_dt_atualicao), new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
				editor.commit();

				Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
				startActivity(intent);

				finish();
			} catch (Exception e) {
				Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
				startActivity(intent);
				finish();
			}

		}

	}

	public boolean temAtualizacao(JSONObject json) throws JSONException {
		Date dataAtualizacao;
		if (json != null) {
			try {
				dataAtualizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(json.getString("dtAtualizacao"));
				String dt = new UserDAO(getApplicationContext()).getDtAtualiacao();

				if (dt == null) {
					return true;
				}
				Date dataUltimaAtualizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dt);
				if (dataAtualizacao.after(dataUltimaAtualizacao)) {
					return true;
				} else {
					return false;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;

	}

}
