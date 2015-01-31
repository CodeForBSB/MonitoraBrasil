package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.adapter.CotaListaAdapter;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.MediaCotas;
import com.gamfig.monitorabrasil.classes.Politico;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CotasFragment extends ListFragment {

	private static final String TAG = "MonitoraBrasil";

	List<Politico> politicos;
	private ListView lv;
	String jsonPoliticos;
	private CotaListaAdapter adapterPoliticos = null;

	private CotaListaAdapter cotaListaAdapter = null; // data original
	private List<Politico> listaPoliticos = new ArrayList<Politico>();
	List<MediaCotas> mediaCotas = new ArrayList<MediaCotas>();
	public Bundle data;
	private boolean isDefaultSelection;

    public CotasFragment() {
        politicos = new ArrayList<>();
    }

    public CotaListaAdapter getCotaListaAdapter() {
		return cotaListaAdapter;
	}

	public void setCotaListaAdapter(CotaListaAdapter cotaListaAdapter) {
		this.cotaListaAdapter = cotaListaAdapter;
	}

	public interface SelectionListener {
		public void onItemSelected(int position);
	}

	private SelectionListener mCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isDefaultSelection = true;
		// Check for previously saved state
		if (savedInstanceState == null) {
			data = getArguments();

		}

	}

	// @Override
	// public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	// if (savedInstanceState == null) {
	//
	// }else{
	//
	// }
	// return null;
	// }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save state information with a collection of key-value pairs
		// 4 lines of code, one for every count variable

		Log.i(TAG, "onSaveInstanceState");
		Gson gson = new Gson();

		savedInstanceState.putString("listaPoliticos", gson.toJson(politicos));
		savedInstanceState.putString("medias", gson.toJson(mediaCotas));

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");

		// Make sure that the hosting Activity has implemented
		// the SelectionListener callback interface. We need this
		// because when an item in this ListFragment is selected,
		// the hosting Activity's onItemSelected() method will be called.

		try {

			mCallback = (SelectionListener) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SelectionListener");
		}
	}

	// Note: ListFragments come with a default onCreateView() method.
	// For other Fragments you'll normally implement this method.
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState)

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "Entered onActivityCreated()");

		// When using two-pane layout, configure the ListView to highlight the
		// selected list item

		if (isInTwoPaneMode()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		} else {
			Log.i(TAG, "NAVIGATION_MODE_STANDARD");
			ActionBar actionBar = getActivity().getActionBar();

            if (actionBar != null) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        }
		View headerView = View.inflate(this.getActivity().getApplicationContext(), R.layout.header_filtro_cota_view, null);
		getListView().addHeaderView(headerView);

		Spinner spinnerTiposCotas = (Spinner) headerView.findViewById(R.id.spinner1);
		final CotasFragment mActivity = this;
		spinnerTiposCotas.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if (isDefaultSelection) { // If spinner initializes
					isDefaultSelection = false;
				} else {
					setListShown(false);
					TextView txt = (TextView) selectedItemView;
					new BuscaCotas(mActivity, txt.getText().toString()).execute();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (savedInstanceState != null) {

			Gson gson = new Gson();
			politicos = gson.fromJson(savedInstanceState.getString("listaPoliticos"), new TypeToken<ArrayList<Politico>>() {
			}.getType());
			mediaCotas = gson.fromJson(savedInstanceState.getString("medias"), new TypeToken<ArrayList<MediaCotas>>() {
			}.getType());
			if (politicos.size() > 0) {

				lv = getListView();
				adapterPoliticos = new CotaListaAdapter(getActivity(), R.layout.listview_item_cota_detalhada, politicos, mediaCotas);

				lv.setAdapter(adapterPoliticos);

				setListShown(true);

				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Politico politico = (Politico) parent.getItemAtPosition(position);
						if (politico != null) {
							if (politico.getIdCadastro() > 0) {
								// abrir detalhes

								Intent intent = new Intent();
								intent.setClass(getActivity(), FichaActivity.class);
								intent.putExtra("idPolitico", politico.getIdCadastro());
								intent.putExtra("twitter", politico.getTwitter());
								intent.putExtra("cota", "true");
								intent.putExtra("casa", "d");//so tem de deputado
								startActivity(intent);

							}
						}
					}
				});
			}

		} else {
			// buscar dados offline
			listaPoliticos = new DeputadoDAO(getActivity()).buscaCotasOffline();

			if (listaPoliticos == null) {
				// busca os politicos
				// lv = getListView();
				new BuscaCotas(this, "").execute();

			}
		}

	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		// Notify the hosting Activity that a selection has been made.

		mCallback.onItemSelected(position);

	}

	// If there is a FeedFragment, then the layout is two-pane
	private boolean isInTwoPaneMode() {

		return getFragmentManager().findFragmentById(R.id.container) == null;

	}

	public class BuscaCotas extends AsyncTask<Void, Void, Map<String, Object>> {

		private CotasFragment mActivity;
		private String query;
		ProgressBar pbar;

		public BuscaCotas(CotasFragment politicosFragment, String q) {
			mActivity = politicosFragment;
			query = q;
		}

		protected void onPreExecute() {
		}

		@Override
		protected Map<String, Object> doInBackground(Void... params) {

			// TODO salvar a pesquisa

			String idSubcota = null;
			// pegar idSubcota
			if (!query.equals("Todas") && !query.equals("Filtrar") && query.length() > 0) {
				String[] sucota = query.split("-");
				idSubcota = sucota[1];

				politicos = new ArrayList<Politico>();
				mediaCotas = new ArrayList<MediaCotas>();

			}

			Log.i("MonitoraBrasil", query);

			new DeputadoDAO();
			// verificar se tem atualizacao na lista de deputados
			// if (DeputadoDAO.temAtualizacao(dtAtualizacao)) {
			// parametro 1 diz que sao todos deputados

			return DeputadoDAO.buscaCotas(idSubcota);
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(Map<String, Object> results) {

			if (results != null) {
				try {
					politicos = (List<Politico>) results.get("politicos");
					mediaCotas = (List<MediaCotas>) results.get("media");
					// ordernar o resultado
					Collections.sort(politicos, new Comparator<Politico>() {
						@Override
						public int compare(Politico arg0, Politico arg1) {
							double total1 = 0;
							double total2 = 0;
							for (Cota cota : arg0.getCotas()) {
								total1 += cota.getValor();
							}
							for (Cota cota : arg1.getCotas()) {
								total2 += cota.getValor();
							}

							return Double.compare(total2, total1);
						}
					});

					int i = 1;
					for (Politico p : politicos) {
						p.setPosicao(i);
						i++;
					}

					// salva politicos no shared
					// new Politicod

					lv = mActivity.getListView();
					if (adapterPoliticos != null) {
						adapterPoliticos.clear();
						adapterPoliticos.notifyDataSetChanged();
					}
					adapterPoliticos = new CotaListaAdapter(mActivity.getActivity(), R.layout.listview_item_cota_detalhada, politicos, mediaCotas);
					setCotaListaAdapter(adapterPoliticos);
					lv.setAdapter(adapterPoliticos);

					mActivity.setListShown(true);

					lv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							Politico politico = (Politico) parent.getItemAtPosition(position);
							if (politico != null) {
								if (politico.getIdCadastro() > 0) {

									// abrir detalhes

									Intent intent = new Intent();
									intent.setClass(getActivity(), FichaActivity.class);
									intent.putExtra("idPolitico", politico.getIdCadastro());
									intent.putExtra("twitter", politico.getTwitter());
									intent.putExtra("casa", "d");//so tem de deputado
									intent.putExtra("cota", "true");
									startActivity(intent);
								}
							}
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
					// Toast.makeText(mActivity.getListView().getContext(), "Naoo foi possivel recuperar lista :(", Toast.LENGTH_SHORT).show();
				}

			}
		}

	}
}
