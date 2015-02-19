package com.gamfig.monitorabrasil.fragments.ficha;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.adapter.HashtagAdapter;
import com.gamfig.monitorabrasil.adapter.ProjetoVotoAdapter;
import com.gamfig.monitorabrasil.classes.Hashtag;

public class HashtagFragment extends TabFactory {

	ProjetoVotoAdapter adapter;
	ListView lv;
	EditText txtHashtag;
	int idPolitico;
	int currentPage;
	boolean chegouFim = false;

	public HashtagFragment() {

	}

	public void montaLayout() {

		txtHashtag = (EditText) getActivity().findViewById(R.id.txtComentario);
		ImageButton btnEnviar = (ImageButton) getActivity().findViewById(R.id.btnEnviarHash);
		lv = (ListView) getActivity().findViewById(R.id.listView1);

		idPolitico = getBundle().getInt("idPolitico");

		OnClickListener click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO mudar o id do usuario
				if (txtHashtag.getText().toString().trim().replace(" ", "").length() > 1) {
					new DeputadoDAO().insereHashtagPolitico(1, txtHashtag.getText().toString().trim().replace(" ", ""), idPolitico);

					// limpar o edttex
					txtHashtag.setText("#");
					Toast.makeText(getActivity().getApplicationContext(), "Hashtag enviada com sucesso!", Toast.LENGTH_SHORT).show();

					// TODO atualizar o listview
					new buscaHashes().execute();
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "Escreva algo!", Toast.LENGTH_SHORT).show();
				}

			}
		};
		btnEnviar.setOnClickListener(click);

		// buscar os hashtags

		new buscaHashes().execute();

	}

	public class buscaHashes extends AsyncTask<Void, Void, ArrayList<Hashtag>> {

		public buscaHashes() {

		}

		@Override
		protected ArrayList<Hashtag> doInBackground(Void... params) {

			lv = (ListView) getActivity().findViewById(R.id.listView1);

			// rlPb.setVisibility(View.VISIBLE);
			// lv.setVisibility(View.GONE);
			return new DeputadoDAO().buscaHashtagPolitico(idPolitico);
		}

		protected void onPostExecute(ArrayList<Hashtag> results) {
			try {
				if (results != null) {
					HashtagAdapter adapter = new HashtagAdapter(getActivity(), R.layout.listview_item_hashtag, results);
					lv.setAdapter(adapter);
				} else {
					//Toast.makeText(getActivity(), "Nenhuma # encontrada", Toast.LENGTH_SHORT).show();
				}
				lv.setVisibility(View.VISIBLE);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
