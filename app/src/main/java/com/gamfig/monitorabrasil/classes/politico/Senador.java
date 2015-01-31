package com.gamfig.monitorabrasil.classes.politico;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.classes.Politico;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Senador extends PoliticoFactory {

	public Senador(Bundle data, Activity activity) {
		super(data, activity);
		// TODO Auto-generated constructor stub
	}

	public List<Politico> getListaPoliticos() {
		Bundle data = getData();
		String uf = data.getString("uf");
		String partido = data.getString("partido");

		// busca offline
		if (null == uf && partido == null) {
			List<Politico> listaPoliticos = new DeputadoDAO(getActivity()).buscaPoliticosSalvos(R.string.pref_listasenadores);
			if (listaPoliticos != null)
				return listaPoliticos;
		}

		String jsonPoliticos = DeputadoDAO.buscaSenadores(partido, uf);
		Gson gson = new Gson();
		List<Politico> politicos = gson.fromJson(jsonPoliticos, new TypeToken<ArrayList<Politico>>() {
		}.getType());
		if (politicos.size() == 81) {
			// salvar offline
			// salvar
			SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(getActivity().getString(R.string.pref_listasenadores), gson.toJson(politicos));
			editor.commit();
		}
		return politicos;
	}
}
