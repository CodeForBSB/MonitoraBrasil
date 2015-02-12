/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Activity que monta o menu e carrega a home como principal (ResumoInicialFragment)
 * 
 */
package com.gamfig.monitorabrasil.activitys;

import java.io.IOException;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.NavigationDrawerFragment;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.adapter.PoliticoMonitoradoAdapter;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.gamfig.monitorabrasil.dialog.DialogComentario;
import com.gamfig.monitorabrasil.fragments.PontuacaoFragment;
import com.gamfig.monitorabrasil.fragments.ResumoInicialFragment;
import com.gamfig.monitorabrasil.fragments.listviews.PoliticosFragment;
import com.gamfig.monitorabrasil.pojo.PreferenciasUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import io.fabric.sdk.android.Fabric;

public class PrincipalActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, PoliticosFragment.SelectionListener {
	public static final String TAG = "MonitoraBrasil";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	GoogleCloudMessaging gcm;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	String SENDER_ID = "490567268994";
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		setContentView(R.layout.activity_pricipal);


		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		// busca o gcm
		salvaGCM();

		// busca a pontuacao
		new BuscaPontuacao().execute();

		// verificar se veio de um push
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			// veio de um projeto
			if (bundle.getString("projeto") != null) {
				int idProjeto = Integer.parseInt(bundle.getString("projeto"));
				// abrir o projeto


				Intent intent = new Intent();
				Projeto projeto = new Projeto(idProjeto);
				Gson gson = new Gson();
				intent.putExtra("projeto", gson.toJson(projeto));
				intent.setClass(this, ProjetoDetalheActivity.class);
				startActivity(intent);
			}

			// veio de um politico
			if (bundle.getString("politico") != null) {
				// abrir o projeto


				Intent intent = new Intent();
				intent.putExtra("idPolitico", Integer.valueOf(bundle.getString("politico")));
				intent.putExtra("casa", bundle.getString("casa"));
				intent.setClass(this, FichaActivity.class);
				startActivity(intent);
			}
		}



	}

	public void comentar(View v) {

		DialogFragment dialog = new DialogComentario(new UserDAO(getApplicationContext()).getIdUser(), 0, "Comente");
		dialog.show(getFragmentManager(), "Cometario");
	}

	public void salvaGCM() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = new PreferenciasUtil().getRegistrationId(context);
			if (regid.isEmpty()) {
				new RegisterInBackground().execute();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	// quando um item do menu eh selecionado, aqui eh feito o dispatcher
	// 1 - eleicoes (retirado)
	// 10 - presidente
	// 11 - governador
	// 12 - senador
	// 13 - dep. fed
	// 14 - dep est

	// 1 - camara
	// 10 - deputados
	// 11 - cota
	// 12 - projetos

	// 2- senado
	// 20 senadores
	// 21 - projetos

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		Fragment fragment2open;
		Intent intent;
		switch (position) {

		case 11:


			intent = new Intent();
			intent.setClass(this, CotaActivity.class);
			startActivity(intent);
			break;

		// tela principal
		case 0:

			setTitle("Monitora, Brasil!");
			// fragment2open = HomeFragment.newInstance(position);
			fragment2open = ResumoInicialFragment.newInstance(position);
			abreFragment(fragment2open);
			break;

		// tela vote nos projetos
		case 12:


			intent = new Intent();
			intent.putExtra("casa", "c");
			intent.setClass(this, ProjetosActivity.class);
			startActivity(intent);
			break;

		// tela de lista de deputados
		case 10:


			intent = new Intent();
			intent.putExtra("casa", "camara");
			intent.setClass(this, PoliticosActivity.class);
			startActivity(intent);
			break;

		// tela de lista de senadores
		case 20:


			intent = new Intent();
			intent.putExtra("casa", "senado");
			intent.setClass(this, PoliticosActivity.class);
			startActivity(intent);
			break;

		// tela vote nos projetos
		case 21:


			intent = new Intent();
			intent.putExtra("casa", "s");
			intent.setClass(this, ProjetosActivity.class);
			startActivity(intent);
			break;

		// tela o que estao falando
		case 3:

            intent = new Intent();
            intent.setClass(this, TwittterActivity.class);
            startActivity(intent);

			break;

		// pontua��o
		case 4:
			setTitle("Sua pontuação");
			// fragment2open = HomeFragment.newInstance(position);
			fragment2open = new PontuacaoFragment();
			abreFragment(fragment2open);
			break;

		// tela de lista de politicos
		case 5:

			intent = new Intent();
			intent.setClass(this, RankUserActivity.class);
			startActivity(intent);
			break;

		}

	}

	private void abreFragment(Fragment fragment2open) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.container, fragment2open);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;
		case 6:
			mTitle = getString(R.string.title_section6);
			break;
		}
	}

	// @Override
	// public void onBackPressed() {
	// super.onBackPressed();
	// // invalidateOptionsMenu();
	// }

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	public PoliticoMonitoradoAdapter adapterPoliticos;
	public PoliticoMonitoradoAdapter adapterPoliticosInicio;
	public Context context = this;
	ShareActionProvider mShareActionProvider;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.principal, menu);
		// restoreActionBar();
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			Log.i(TAG, "Principal onCreateOptionsMenu");
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.

			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public void enviarSugestao() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "fale@monitorabrasil.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "Fale Conosco");
		i.putExtra(Intent.EXTRA_TEXT, "");
		try {
			startActivity(Intent.createChooser(i, "Enviar email..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(PrincipalActivity.this, "N�o h� email configurado no dispositivo.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent intent;
		switch (id) {
		case R.id.action_fale:
			enviarSugestao();
			break;
		case R.id.action_perfil:

			intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.action_sobre:

			intent = new Intent();
			intent.setClass(this, SobreActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.menu_item_share) {

			compartilhar(null);
			return true;
		}
		if (id == R.id.menu_item_comente) {

			comentar(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void compartilhar(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_compartilhe));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	public void onItemSelected(int position) {

	}

	public class BuscaPontuacao extends AsyncTask<Void, Void, Void> {

		public BuscaPontuacao() {
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			new UserDAO(getApplicationContext()).buscaPontuacao();
			return null;
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's shared preferences.
	 */
	public class RegisterInBackground extends AsyncTask<Void, Void, String> {

		public RegisterInBackground() {
		}

		@Override
		protected String doInBackground(Void... arg0) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;

				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.
				// The request to your server should be authenticated if your
				// app
				// is using accounts.
				sendRegistrationIdToBackend();

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message using the 'from' address in the message.

				// Persist the regID - no need to register again.
				new PreferenciasUtil().storeRegistrationId(context, regid);

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

	}

	private void sendRegistrationIdToBackend() {
		// autaliza o idGcm
		new UserDAO(getApplicationContext()).atualizaGcm(regid);
		Log.i(TAG, "regId: " + regid);
	}

	@Override
	public void onItemSelected(int position, ListView l, View view) {
		// TODO Auto-generated method stub

	}
}
