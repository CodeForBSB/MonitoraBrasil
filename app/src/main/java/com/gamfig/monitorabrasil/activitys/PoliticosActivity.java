package com.gamfig.monitorabrasil.activitys;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.adapter.PoliticoAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.dialog.DialogComentario;
import com.gamfig.monitorabrasil.fragments.ficha.PoliticoDetalheFragment;
import com.gamfig.monitorabrasil.fragments.fitros.FiltroGeral;
import com.gamfig.monitorabrasil.fragments.listviews.PoliticosFragment;

public class PoliticosActivity extends Activity implements PoliticosFragment.SelectionListener {

	private PoliticosFragment mPoliticosFragment; // fragment que carrega a lista de politicos
	private PoliticoDetalheFragment mFichaFragment; // fragment que carrega a ficha do politico selecionado
	private FragmentManager mFragmentManager;
	private FiltroGeral mFiltro;
	boolean hideMenu = false;
	boolean showMenuPolitico = false;
	private Politico politico;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_lista_politicos);

		Log.i(PrincipalActivity.TAG, "Activity = onCreate");
		if (!isInTwoPaneMode()) {
			// if (savedInstanceState == null) {
			// getFragmentManager().beginTransaction().add(R.id.fragment_container, new PlaceholderFragment()).commit();
			// }

			mPoliticosFragment = new PoliticosFragment();
			mPoliticosFragment.setArguments(getIntent().getExtras());

			// TODO 1 - add the FriendsFragment to the fragment_container
			mFragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fragment_container, mPoliticosFragment);
			fragmentTransaction.commit();

		} else {

			// Otherwise, save a reference to the FeedFragment for later use

//			mFichaFragment = (PoliticoDetalheFragment) getFragmentManager().findFragmentById(R.id.politico_frag);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i(PrincipalActivity.TAG, "Activity - onResume");
	}

	private boolean isInTwoPaneMode() {

		return findViewById(R.id.fragment_container) == null;

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public void onItemSelected(int position, ListView l, View view) {
		// If there is no FeedFragment instance, then create one

		if (mFichaFragment == null)
			mFichaFragment = new PoliticoDetalheFragment();

		// pega politico que foi selecionado
		politico = (Politico) l.getAdapter().getItem(position);
		setTitle(politico.getNomeParlamentar());

		Bundle bundle = new Bundle();
		bundle.putInt("idPolitico", politico.getIdCadastro());
		bundle.putString("nome",politico.getNomeParlamentar());
		if(politico.getTwitter() != null){
			bundle.putString("twitter", politico.getTwitter());
		}
		String titulo = getActionBar().getTitle().toString();
		if (titulo.equals("Senadores")) {
			bundle.putString("casa", "senado");
		} else {
			bundle.putString("casa", "camara");
		}

		mFichaFragment.setArguments(bundle);

		// If in single-pane mode, replace single visible Fragment

		if (!isInTwoPaneMode()) {

			// TODO 2 - replace the fragment_container with the FeedFragment
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.drawable.enter_anim, R.drawable.exit_anim, R.drawable.enter_anim_back,
					R.drawable.exit_anim_back);
			fragmentTransaction.replace(R.id.fragment_container, mFichaFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			showMenuPolitico = true;
			invalidateOptionsMenu();

			// execute transaction now
			// getFragmentManager().executePendingTransactions();

		}

		// Update Twitter feed display on FriendFragment
		mFichaFragment.updateFeedDisplay(position);

	}

	@Override
	public void onBackPressed() {
		hideMenu = false;
		showMenuPolitico = false;
		invalidateOptionsMenu();
		super.onBackPressed();
	}

	public void filtrar() {
		// busca valores
		Spinner spinnerUF = (Spinner) findViewById(R.id.spinnerUf);
		String uf = (String) spinnerUF.getItemAtPosition(spinnerUF.getSelectedItemPosition());
		Spinner spinnerPartido = (Spinner) findViewById(R.id.spinnerPartido);
		String partido = (String) spinnerPartido.getItemAtPosition(spinnerPartido.getSelectedItemPosition());

		Bundle bundle = getIntent().getExtras();
		bundle.putString("uf", uf);
		bundle.putString("partido", partido);

		// mPoliticosFragment.atualizaAdapter(bundle);

		// mPoliticosFragment = new PoliticosFragment();
		// mPoliticosFragment.setArguments(bundle);

		// TODO 1 - add the FriendsFragment to the fragment_container
		mFragmentManager = getFragmentManager();
		mPoliticosFragment.setmBundle(bundle);
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.show(mPoliticosFragment);
		// fragmentTransaction.replace(R.id.fragment_container, mPoliticosFragment);
		fragmentTransaction.commit();
		mFragmentManager.popBackStack();

		hideMenu = false;
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (showMenuPolitico) {
			getMenuInflater().inflate(R.menu.ficha_politico, menu);
		} else {
			getMenuInflater().inflate(R.menu.lista_politicos, menu);
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
						PoliticoAdapter padpt = (PoliticoAdapter) mPoliticosFragment.getListView().getAdapter();
						PoliticoAdapter adapterInicial = (PoliticoAdapter) mPoliticosFragment.getListView().getAdapter();

						PoliticoAdapter adapterPoliticos;
						if (TextUtils.isEmpty(newText)) {

							mPoliticosFragment.getListView().setAdapter(adapterInicial);
							// adapterPoliticos.getFilter().filter("");
							// lv.clearTextFilter();
						} else {
							// adapterPoliticos.getFilter().filter(newText.toString());
							newText = newText.toString().toLowerCase();
							List<Politico> founded = new ArrayList<Politico>();
							if (newText != null && newText.toString().length() > 0) {
								for (Politico item : padpt.getData()) {
									if (item.getNome().toString().toLowerCase().contains(newText)) {
										founded.add(item);
									}
								}
							}
							adapterPoliticos = new PoliticoAdapter(mPoliticosFragment.getActivity(), R.layout.listview_item_politico, founded);
							mPoliticosFragment.getListView().setAdapter(adapterPoliticos);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					return false;
				}
			});
			MenuItem filtroMenu = menu.findItem(R.id.ic_filtro);
			MenuItem confirmaMenu = menu.findItem(R.id.ic_play);
			if (hideMenu) {
				searchItem.setVisible(false);
				filtroMenu.setVisible(false);
				confirmaMenu.setVisible(true);
			} else {
				searchItem.setVisible(true);
				filtroMenu.setVisible(true);
				confirmaMenu.setVisible(false);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.ic_filtro:
			mFiltro = new FiltroGeral();
            mFiltro.setLayout(R.layout.fragment_filtro_politico);
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.drawable.slide_in_up, R.drawable.fade_out);
			fragmentTransaction.replace(R.id.fragment_container, mFiltro);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();

			hideMenu = true;
			invalidateOptionsMenu();

			break;

		// aplicar filtro
		case R.id.ic_play:
			filtrar();

			break;

		// icones da ficha do politico
		case R.id.ic_share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);

			String mensagem = "Ficha Dep. " + politico.getNomeParlamentar() + "\n";
			mensagem = mensagem + getString(R.string.url_share) + String.valueOf(politico.getIdCadastro()) + " #monitoraBrasil";
			sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		case R.id.ic_comment:
			DialogFragment dialog = new DialogComentario(new UserDAO(getApplicationContext()).getIdUser(), politico.getIdCadastro(), "Comente", 1);
			dialog.show(getFragmentManager(), "Cometario");

			break;
		}

		return super.onOptionsItemSelected(item);

	}

	// acoes dos botoes da ficha
	public void abreGrafico(View v) {
        try{
            Button btn = (Button) v;
            ViewFlipper mVf = (ViewFlipper) findViewById(R.id.viewFlipper1);

            mVf.setInAnimation(this, android.R.anim.fade_in);
            mVf.setOutAnimation(this, android.R.anim.fade_out);

            if (mVf.getDisplayedChild() == 0) {
                mVf.showNext();
                btn.setText("Números");
            } else {
                mVf.showPrevious();
                btn.setText("Gráfico");
            }
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }


    }

	public void abreFonte(View v) {
		new AlertDialog.Builder(this).setTitle(R.string.fonte).setMessage(R.string.msg_fonte_tbrasil).setIcon(R.drawable.ic_action_about)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
					}
				}).show();

	}

	public void compartilharBio(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		TextView txtBio = (TextView) findViewById(R.id.txtBiografia);
		// pega nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);

		// pega twitter
		TextView txtTwitter = (TextView) findViewById(R.id.txtTwitterFicha);


		String mensagem = "Mini-biografia: " + txtNome.getText().toString() + " " + txtTwitter.getText().toString() + "\n";
		mensagem = mensagem + txtBio.getText().toString() + "\n" + getString(R.string.url_share) + String.valueOf(politico.getIdCadastro())
				+ "\n #MonitoraBrasil";
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	public void compartilharProcessos(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		TextView txtProcessos = (TextView) findViewById(R.id.txtProcessos);
		// pega nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);

		// pega twitter
		TextView txtTwitter = (TextView) findViewById(R.id.txtTwitterFicha);


		String mensagem = "Processos: " + txtNome.getText().toString() + " " + txtTwitter.getText().toString();
		mensagem = mensagem + txtProcessos.getText().toString() + "\n" + getString(R.string.url_share) + String.valueOf(politico.getIdCadastro())
				+ "\n #MonitoraBrasil";
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_lista_politicos, container, false);
			return rootView;
		}
	}

}
