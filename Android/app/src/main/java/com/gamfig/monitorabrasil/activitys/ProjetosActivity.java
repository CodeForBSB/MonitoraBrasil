/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Activity para mostrar os projetos a partir do menu.
 * Mostra todos os projetos a partir de qual casa(senado ou camara) foi selecionado
 * ProjetosFragment - criado
 * 
 */
package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Spinner;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.fragments.fitros.FiltroGeral;
import com.gamfig.monitorabrasil.fragments.listviews.ProjetosFragment;

public class ProjetosActivity extends Activity {

	private FragmentManager mFragmentManager;
	private FiltroGeral mFiltro;
	private ProjetosFragment projetosFragment;
	boolean hideMenu = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_projetos);
		if (savedInstanceState == null) {
			projetosFragment = new ProjetosFragment();
			Bundle bundle = getIntent().getExtras();
			projetosFragment.setArguments(bundle);
			mFragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.container, projetosFragment, "listaProjetos");
			fragmentTransaction.commit();
		}
	}

	// abre a tela de filtro
	public void filtrar() {
		// busca valores
		Spinner spinnerProjetos = (Spinner) findViewById(R.id.spinnerProjetos);
		String opcaoFiltro = (String) spinnerProjetos.getItemAtPosition(spinnerProjetos.getSelectedItemPosition());

		Bundle bundle = getIntent().getExtras();
		bundle.putString("filtro", opcaoFiltro);

		mFragmentManager = getFragmentManager();
		projetosFragment.setmBundle(bundle);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.show(projetosFragment);
		// fragmentTransaction.replace(R.id.fragment_container, mPoliticosFragment);
		fragmentTransaction.commit();
		mFragmentManager.popBackStack();
		// buscar os projetos utilizando o filtro
		hideMenu = false;
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lista_projetos, menu);
		MenuItem searchItem = menu.findItem(R.id.ic_procura);
		
		SearchView searchView = (SearchView) searchItem.getActionView();

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO realiza a consulta com o que foi passado
				// pesquisa por palavra chave ou texto da ementa
				if (!query.isEmpty()) {
					projetosFragment.setListShown(false);
					Bundle bundle = getIntent().getExtras();
					bundle.putString("query", query.trim());
					projetosFragment.buscaProjetos(bundle);				
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return false;
			}
		});
		MenuItem filtroMenu = menu.findItem(R.id.ic_filtro);
		MenuItem confirmaMenu = menu.findItem(R.id.ic_play);
		if(hideMenu){
			searchItem.setVisible(false);
			filtroMenu.setVisible(false);
			confirmaMenu.setVisible(true);
		}else{
			searchItem.setVisible(true);
			filtroMenu.setVisible(true);
			confirmaMenu.setVisible(false);
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		hideMenu=false;
		invalidateOptionsMenu();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.ic_filtro) {
			mFiltro = new FiltroGeral();
            mFiltro.setLayout(R.layout.fragment_filtro_projeto);
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.drawable.slide_in_up, R.drawable.fade_out);
			fragmentTransaction.replace(R.id.container, mFiltro);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			hideMenu=true;
			invalidateOptionsMenu();

			return true;
		}
		//aplicar filtro
		if (id == R.id.ic_play) {
			filtrar();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
