/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Fragment que carrega os projetos - camara ou senado
 * Chamado por
 *  	- activity ListaProjetosActivity
 * 
 */
package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.adapter.ProjetoVotoAdapter;
import com.gamfig.monitorabrasil.classes.Projeto;

public class ProjetosFragment extends ListFragment implements OnScrollListener {

	ProjetoVotoAdapter adapter;
	RelativeLayout rlPb;

	int idPolitico;
	String casa;
	int currentPage;
	boolean isSearching = false;
	boolean chegouFim = false;
	private Bundle mBundle;

	public ProjetosFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// buscar as infos do deputado
		Bundle bundle = this.getArguments();
		try {
			idPolitico = bundle.getInt("idPolitico");
			if (bundle.containsKey("casa")) {
				casa = bundle.getString("casa");
				if (idPolitico > 0)
					chegouFim = true;
			} else {
				casa = "c";
			}

		} catch (Exception e) {
			idPolitico = 0;
		}

		getListView().setOnScrollListener(this);
		if (mBundle == null) {
			mBundle = bundle;
		}
		// busca os projetos
		new buscaProjetos(this, mBundle).execute();
		setListShown(false);

	}

	public void buscaProjetos(Bundle bundle) {
		new buscaProjetos(this, bundle).execute();

	}

	/**
	 * BUSCA PROJETOS
	 * 
	 * @author 89741803168
	 * 
	 */
	public class buscaProjetos extends AsyncTask<Void, Void, ArrayList<Projeto>> {

		ArrayList<Projeto> projetos;
		Bundle bundle;
		String query;
		ProjetosFragment projetosFragment;

		public buscaProjetos(ProjetosFragment projetosFragment, Bundle bundle) {
			this.projetosFragment = projetosFragment;
			this.bundle = bundle;
		}

		public buscaProjetos(ProjetosFragment projetosFragment, String query2) {
			this.projetosFragment = projetosFragment;
			this.query = query2;
		}

		public buscaProjetos(ProjetosFragment projetosFragment2) {
			this.projetosFragment = projetosFragment2;
		}

		@Override
		protected ArrayList<Projeto> doInBackground(Void... params) {

			Log.i(PrincipalActivity.TAG, "doInBackground");
			// setListShown(false);
			currentPage = 0;
			if (bundle == null) {
				bundle = new Bundle();
			}

			// pesquisa textual
			if (null != bundle.getString("query")) {
				return new DeputadoDAO().buscaProjetosQuery(bundle.getString("query"));
			} else {
				// buscar os projetos da lista do user
				if (idPolitico > 0) {
					return new DeputadoDAO().buscaProjetosVoto(0, "8", null, null, idPolitico, casa);
				} else {
					if (casa.equals("s")) {
						Calendar cal = Calendar.getInstance();
						int month = cal.get(Calendar.MONTH);
						currentPage = month + 1;
					}

					String filtro = bundle.getString("filtro");
					if (null == filtro) {
						filtro = "Projetos mais Recentes";
					}
					String idUser = String.valueOf(new UserDAO(getActivity()).getIdUser());
					return new DeputadoDAO().buscaProjetosVoto(currentPage, filtro, null, idUser, idPolitico, casa);
				}
			}

		}

		protected void onPostExecute(ArrayList<Projeto> projetosDeputado) {

			// VERIFICA SE EST� NA ABA DE PROJETOS
			try {
				setListShown(true);
				if (projetosDeputado != null) {
					adapter = new ProjetoVotoAdapter(projetosFragment.getActivity(), R.layout.listview_item_projetos, projetosDeputado, getActivity()
							.getFragmentManager(), casa);
					setListAdapter(adapter);

				} else {
					ArrayList<Projeto> projetos = new ArrayList<Projeto>();
					Projeto projeto = new Projeto();
					projeto.setNome("Não há registros de projetos");
					projeto.setId(-1);
					projetos.add(projeto);
					// ProjetoAdapter adapter = new ProjetoAdapter(this, R.layout.listview_item_row, projetos);
					// lv.setAdapter(adapter);
				}
				projetosFragment.setListShown(true);
				// rlPb.setVisibility(View.GONE);
				// lv.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	public class buscaMaisProjetos extends AsyncTask<Void, Void, String> {
		Activity listaProjetosActivit;
		ArrayList<Projeto> projetos;
		Bundle bundle;
		int page;

		public buscaMaisProjetos(Activity listaProjetosActivity, int currentPage2, Bundle bundle) {
			this.listaProjetosActivit = listaProjetosActivity;
			page = currentPage2;
			this.bundle = bundle;
		}

		@Override
		protected String doInBackground(Void... params) {

			if (idPolitico > 0) {
				projetos = new DeputadoDAO().buscaProjetosVoto(page * 10, "8", null, null, idPolitico, casa);
			} else {
				if (casa.equals("s")) {
					projetos = new DeputadoDAO().buscaProjetosVoto(currentPage, "Projetos mais Recentes", null, null, idPolitico, casa);
				} else {
					String filtro = bundle.getString("filtro");
					if (null == filtro) {
						filtro = "Projetos mais Recentes";
					}
					String idUser = String.valueOf(new UserDAO(getActivity()).getIdUser());
					projetos = new DeputadoDAO().buscaProjetosVoto(page * 10, filtro, null, idUser, idPolitico, casa);
				}
			}
			return "ok";
		}

		protected void onPostExecute(String results) {

			// popula listivew com os projetos de autoria do deputado
			if (projetos != null) {
				if (!projetos.get(0).getNome().equals("Nenhum projeto")) {
					for (Projeto projeto : projetos) {
						adapter.add(projeto);
					}
					adapter.notifyDataSetChanged();
				} else {
					chegouFim = true;
					// colocar foot
					if (getListView().getFooterViewsCount() == 0) {
						View footerView = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE)).inflate(
								R.layout.footer_layout_fim, null, false);
						getListView().addFooterView(footerView);
						Toast.makeText(getActivity(), "Não há mais projetos!", Toast.LENGTH_LONG).show();
					}

				}
			}
			isSearching = false;

		}
	}

	private void loadElements(int currentPage2) {
		if (!chegouFim) {
			isSearching = true;
			new buscaMaisProjetos(getActivity(), currentPage2, mBundle).execute();
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (getListView().getLastVisiblePosition() >= getListView().getCount() - 3) {
				if (!isSearching) {
					if (casa.equals("s")) {
						Log.i(PrincipalActivity.TAG, String.valueOf(currentPage));
						currentPage--;
					} else {
						currentPage++;
					}

					// load more list items:
					loadElements(currentPage);
				}

			}
		}

	}

	public Bundle getmBundle() {
		return mBundle;
	}

	public void setmBundle(Bundle mBundle) {
		this.mBundle = mBundle;
	}

}
