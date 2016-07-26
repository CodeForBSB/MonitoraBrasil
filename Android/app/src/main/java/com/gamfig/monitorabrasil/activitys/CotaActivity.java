package com.gamfig.monitorabrasil.activitys;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.HeaderViewListAdapter;
import android.widget.SearchView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.CotaListaAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.fragments.listviews.CotasFragment;

public class CotaActivity extends Activity implements CotasFragment.SelectionListener {

	private CotasFragment cotaFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cota_parlamentar);
		setTitle("Cota Parlamentar");
		cotaFragment = new CotasFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.container, cotaFragment, "listaPoliticoMonitora");
		fragmentTransaction.commit();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putString("listaPoliticos", "teste");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.politicos, menu);
		MenuItem searchItem = menu.findItem(R.id.ic_procura);
		SearchView searchView = (SearchView) searchItem.getActionView();

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				try {
					CotaListaAdapter cotaAdapter = ((CotaListaAdapter) ((HeaderViewListAdapter) cotaFragment.getListView().getAdapter())
							.getWrappedAdapter());

					CotaListaAdapter adapterCotas;
					if (TextUtils.isEmpty(newText)) {
						adapterCotas = new CotaListaAdapter(cotaFragment.getActivity(), R.layout.listview_item_cota_detalhada, cotaAdapter.getData(),
								cotaAdapter.getMedias());
						cotaFragment.getListView().setAdapter(cotaFragment.getCotaListaAdapter());

						// adapterPoliticos.getFilter().filter("");
						// lv.clearTextFilter();
					} else {
						// adapterPoliticos.getFilter().filter(newText.toString());
						newText = newText.toString().toLowerCase();
						List<Politico> founded = new ArrayList<Politico>();
						if (newText != null && newText.toString().length() > 0) {
							for (Politico item : cotaAdapter.getData()) {
								if (item.getNome().toString().toLowerCase().contains(newText)) {
									founded.add(item);
								}
							}
							adapterCotas = new CotaListaAdapter(cotaFragment.getActivity(), R.layout.listview_item_cota_detalhada, founded,
									cotaAdapter.getMedias());
							cotaFragment.getListView().setAdapter(adapterCotas);
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemSelected(int position) {

		Log.i("MONITORA", "Entered onItemSelected(" + position + ")");

	}
}
