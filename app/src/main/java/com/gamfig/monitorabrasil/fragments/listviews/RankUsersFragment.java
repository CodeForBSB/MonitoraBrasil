package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.adapter.RankAdapter;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.fragments.PontuacaoFragment;
import com.google.gson.Gson;

public class RankUsersFragment extends ListFragment implements OnScrollListener {

	RankAdapter adapter;
	RelativeLayout rlPb;
	ListView lv;
	int idPolitico;
	int currentPage;
	boolean chegouFim = false;

	public RankUsersFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new buscaRank(getActivity()).execute();
		getListView().setOnScrollListener(this);

	}

	/**
	 * BUSCA Rank
	 * 
	 * @author 89741803168
	 * 
	 */
	public class buscaRank extends AsyncTask<Void, Void, List<Usuario>> {
		Activity mActivity;
		ArrayList<Usuario> users;

		public buscaRank(Activity listaProjetosActivity) {
			this.mActivity = listaProjetosActivity;

		}

		@Override
		protected List<Usuario> doInBackground(Void... params) {

			// buscar os projetos da lista do user

			return new UserDAO().buscaRankingUsers(0);

		}

		protected void onPostExecute(List<Usuario> usuarios) {

			// VERIFICA SE ESTa NA ABA DE PROJETOS
			try {
				if (usuarios != null) {
					adapter = new RankAdapter(getActivity(), R.layout.listview_item_usuario, usuarios);
					setListAdapter(adapter);
					getListView().setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							Usuario userSelecionado = (Usuario)getListAdapter().getItem(arg2);
							PontuacaoFragment fragment2open = new PontuacaoFragment();
							Bundle bundle = new Bundle();
							Gson gson = new Gson();
							bundle.putString("user", gson.toJson(userSelecionado));
							fragment2open.setArguments(bundle);
							FragmentManager fragmentManager = getActivity().getFragmentManager();
							FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
							fragmentTransaction.replace(R.id.fragment_container, fragment2open);
							fragmentTransaction.addToBackStack(null);
							fragmentTransaction.commit();
							
						}
					});
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public class buscaMaisUsers extends AsyncTask<Void, Void, List<Usuario>> {
		Activity mActivity;
		private ProgressDialog mDialog;
		List<Usuario> users;
		int page;

		public buscaMaisUsers(Activity listaProjetosActivity, int currentPage2) {
			this.mActivity = listaProjetosActivity;
			page = currentPage2;
		}

		@Override
		protected List<Usuario> doInBackground(Void... params) {

			return new UserDAO().buscaRankingUsers(page);

		}

		protected void onPostExecute(List<Usuario> usuarios) {

			// VERIFICA SE ESTa NA ABA DE PROJETOS
			try {
				if (usuarios != null) {
					adapter = (RankAdapter) getListAdapter();
					for (Usuario user : usuarios) {
						adapter.add(user);
					}
					adapter.notifyDataSetChanged();

				} else {
					chegouFim = true;
					// colocar foot
					if (getListView().getFooterViewsCount() == 0) {
						View footerView = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout_fim, null, false);
						lv.addFooterView(footerView);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	private void loadElements(int currentPage2) {
		if (!chegouFim)
			new buscaMaisUsers(getActivity(), currentPage2).execute();

	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (getListView().getLastVisiblePosition() >= getListView().getCount() - 3) {
				currentPage++;
				// load more list items:
				loadElements(currentPage);
			}
		}

	}
}
