package com.gamfig.monitorabrasil.classes.politico;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.gamfig.monitorabrasil.classes.Politico;

public class PoliticoFactory {
	private Bundle data;
	private String casa;
	private Activity activity;

	public PoliticoFactory(Bundle data, Activity activity) {
		this.data = data;
		this.setActivity(activity);
	}

	public List<Politico> buscaPoliticos() {
		PoliticoFactory politico;
		// senado ou camara
		casa = data.getString("casa");

		if (casa == null) {
			casa = "camara";
		}
		if (casa.equals("senado")) {
			politico = new Senador(this.data,getActivity());
		} else {
			politico = new Deputado(this.data,getActivity());
		}
		return politico.getListaPoliticos();

	}

	public List<Politico> getListaPoliticos() {
		// TODO Auto-generated method stub
		return null;
	}

	public Bundle getData() {
		return data;
	}

	public void setData(Bundle data) {
		this.data = data;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
