package com.gamfig.monitorabrasil.fragments.ficha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.adapter.DoacaoAdapter;
import com.gamfig.monitorabrasil.adapter.ProjetoVotoAdapter;
import com.gamfig.monitorabrasil.classes.Doacao;
import com.gamfig.monitorabrasil.classes.Doador;

public class DoacaoFragment extends TabFactory {

	List<NameValuePair> listDataHeader;
	HashMap<String, List<Doador>> listDataChild;

	ProjetoVotoAdapter adapter;
	ExpandableListView lv;
	EditText edtTexto;
	int idPolitico;
	int currentPage;
	boolean chegouFim = false;

	public DoacaoFragment() {

	}

	public void montaLayout() {

		lv = (ExpandableListView) getActivity().findViewById(R.id.listView_doacao_ficha);
		// buscar as infos do deputado
		idPolitico = getBundle().getInt("idPolitico");

		new buscaCota(idPolitico).execute();

	}

	public class buscaCota extends AsyncTask<Void, Void, ArrayList<Doacao>> {
		int idPolitico;

		public buscaCota(int idPolitico) {
			this.idPolitico = idPolitico;
		}

		@Override
		protected ArrayList<Doacao> doInBackground(Void... params) {

			// buscar os projetos da lista do user

			return new DeputadoDAO().buscaDoacoes(idPolitico);
		}

		protected void onPostExecute(ArrayList<Doacao> doacoes) {

			try {
				listDataHeader = new ArrayList<NameValuePair>();
				listDataChild = new HashMap<String, List<Doador>>();
				double valorTotal = 0;

				for (Doacao doacao : doacoes) {
					// zera o total do tipo de cota
					double valorTotalTipo = 0;

					List<Doador> textos = new ArrayList<Doador>();
					for (Doador doador : doacao.getDoador()) {
						textos.add(doador);
						valorTotal += doador.getValor();
						valorTotalTipo += doador.getValor();

					}
					listDataHeader.add(new BasicNameValuePair(doacao.getTipo(), String.valueOf(valorTotalTipo)));
					listDataChild.put(doacao.getTipo(), textos);
				}

				ExpandableListView mExpLv = (ExpandableListView) getActivity().findViewById(R.id.listView_doacao_ficha);
				DoacaoAdapter listAdapter = new DoacaoAdapter(getActivity(), listDataHeader, listDataChild);
				// setting list adapter
				mExpLv.setAdapter(listAdapter);

				// posicao

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

}
