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

public class Deputado extends PoliticoFactory {

	public Deputado(Bundle data, Activity activity) {
		super(data, activity);
		// TODO Auto-generated constructor stub
	}

	public List<Politico> getListaPoliticos() {
		Bundle data = getData();
		String uf = data.getString("uf");
		String partido = data.getString("partido");
		String query = data.getString("query");

		// busca offline
		if (uf == null && partido == null && query == null) {
			List<Politico> listaPoliticos = new DeputadoDAO(getActivity()).buscaPoliticosSalvos(R.string.pref_listadeputados);
			if (listaPoliticos != null)
				return listaPoliticos;
		}
		String jsonPoliticos = DeputadoDAO.buscaDeputados(partido, uf, "1", query, "");
		Gson gson = new Gson();
		List<Politico> politicos = gson.fromJson(jsonPoliticos, new TypeToken<ArrayList<Politico>>() {
		}.getType());
		// salvar offline se buscar todos
		if (politicos.size() == 514) {
			// salvar
			SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getString(R.string.id_key_preferencias), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(getActivity().getString(R.string.pref_listadeputados), gson.toJson(politicos));
			editor.commit();
		}

		return politicos;
	}
	// offline
	// listaPoliticos = new DeputadoDAO(getActivity()).buscaPoliticos();
	//
	// getActivity().setTitle("Deputados Federais");
	// if (listaPoliticos == null) {
	// // busca os politicos
	// new BuscaPoliticos(this, casa).execute();
	//
	// } else {
	// PoliticoAdapter adapterPoliticos = new PoliticoAdapter(getActivity(), R.layout.listview_item_politico, listaPoliticos);
	// // Set the list adapter for this ListFragment
	// setListAdapter(adapterPoliticos);
	// }

}
