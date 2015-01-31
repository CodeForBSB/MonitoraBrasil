package com.gamfig.monitorabrasil.pojo;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.SplashActivity;
import com.gamfig.monitorabrasil.classes.Presenca;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PreferenciasUtil {
	static final String TAG = "MonitoraBrasil";
	Context mContext;

	public PreferenciasUtil() {

	}

	public PreferenciasUtil(Context context) {
		mContext = context;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(context.getString(R.string.id_key_reg_id), "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(context.getString(R.string.id_key_app_verssion), Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			// atualizar o gcmid
			new AtualizarGCMID(registrationId, getUser(context)).execute();
			return "";
		}
		return registrationId;
	}

	

	public String getUser(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);

		return sharedPref.getString("userId", null);
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	public void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(context.getString(R.string.id_key_reg_id), regId);
		editor.putInt(context.getString(R.string.id_key_app_verssion), appVersion);
		editor.commit();
	}

	/**
	 * Enviar Duvida
	 * 
	 * @author Guto
	 * 
	 */

	public class AtualizarGCMID extends AsyncTask<Void, Void, Void> {

		private String gcmId;
		private String user;

		public AtualizarGCMID(String texto, String string) {
			this.gcmId = texto;
			this.user = string;
		}

		protected void onPostExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public String getUserCompleto(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);

		return sharedPref.getString("user", null);
	}

	public void salvaPresenca(List<Presenca> presencas, Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = sharedPref.edit();
		Gson gson = new Gson();
		editor.putString("presenca", gson.toJson(presencas, new TypeToken<List<Presenca>>() {
		}.getType()));
		editor.commit();

	}

	public List<Presenca> getPresenca(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
		Gson gson = new Gson();
		return gson.fromJson(sharedPref.getString("presenca", null), new TypeToken<List<Presenca>>() {
		}.getType());
	}

	public boolean isPrimeiraVez(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
		if (!sharedPref.getBoolean("isPrimeiraVez", false)) {
			// salvar e retornar false
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("isPrimeiraVez", true);
			editor.commit();

			return false;
		}

		return true;
	}

}
