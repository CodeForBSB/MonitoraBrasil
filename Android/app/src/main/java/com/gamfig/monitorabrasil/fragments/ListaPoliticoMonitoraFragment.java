package com.gamfig.monitorabrasil.fragments;


import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.adapter.PoliticoMonitoradoAdapter;

public class ListaPoliticoMonitoraFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// View rootView = inflater.inflate(R.layout.fragment_lista_deputados_monitorado, container, false);
		super.onCreate(savedInstanceState);

		PoliticoMonitoradoAdapter adapterPoliticos = new PoliticoMonitoradoAdapter(getActivity(), R.layout.listview_item_politico_monitorado,
				new DeputadoDAO(getActivity()).buscaPoliticosSalvos(R.string.pref_listadeputados));

		// Set the list adapter for this ListFragment
		setListAdapter(adapterPoliticos);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i(PrincipalActivity.TAG, " ListaPoliticoMonitoraFragment onCreateOptionsMenu()");
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem menuProcura = menu.getItem(0);
		menuProcura.setVisible(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		Log.i(PrincipalActivity.TAG, "Entered lista politicos onPrepareOptionsMenu()");
		super.onPrepareOptionsMenu(menu);
	}

}
