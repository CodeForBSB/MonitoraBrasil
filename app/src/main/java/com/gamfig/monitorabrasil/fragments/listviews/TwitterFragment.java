package com.gamfig.monitorabrasil.fragments.listviews;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.adapter.TwitterAdapter;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Twitter;

public class TwitterFragment extends Fragment {

	public TwitterFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

		// buscar os projetos da lista do user
		new buscaTweets(getActivity()).execute();

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	/**
	 * BUSCA PROJETOS
	 * 
	 * @author 89741803168
	 * 
	 */
	public class buscaTweets extends AsyncTask<Void, Void, ArrayList<Twitter>> {
		Activity activit;
		RelativeLayout rl;
		ListView lv;

		public buscaTweets(Activity listaProjetosActivity) {
			this.activit = listaProjetosActivity;

		}

		@Override
		protected ArrayList<Twitter> doInBackground(Void... params) {
			try {
				rl = (RelativeLayout) activit.findViewById(R.id.rlProgressBar);

				lv = (ListView) activit.findViewById(R.id.listView1);

				rl.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			} catch (Exception e) {
				// TODO: handle exception
			}

			new DeputadoDAO();
			return DeputadoDAO.buscaTimeLine("", 2);

		}

		protected void onPostExecute(ArrayList<Twitter> tweets) {

			// VERIFICA SE ESTa NA ABA DE PROJETOS
			try {
				if (tweets != null) {

					TwitterAdapter tAdapter = new TwitterAdapter(activit, R.layout.listview_item_twitter, tweets);
					lv.setAdapter(tAdapter);
					lv.setVisibility(View.VISIBLE);
					rl.setVisibility(View.GONE);

					lv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							Twitter twette = (Twitter) parent.getItemAtPosition(position);
							Politico politico = new Politico(twette.getNome());
//							politico.setTipoParlamentar();
							politico = new DeputadoDAO(activit.getApplicationContext()).buscaPolitico(politico);
							if (politico != null) {

								Intent intent = new Intent();
								intent.setClass(getActivity(), FichaActivity.class);
								intent.putExtra("idPolitico", politico.getIdCadastro());
								intent.putExtra("twitter", twette.getNome());
								startActivity(intent);

							}
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(activit, "Não foi possível recuperar os feeds :(", Toast.LENGTH_LONG).show();
			}

		}
	}
}
