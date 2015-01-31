package com.gamfig.monitorabrasil.classes.cards;

import java.util.List;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.MonitoraDAO;
import com.gamfig.monitorabrasil.activitys.ProjetoDetalheActivity;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.google.gson.Gson;

public class CardProjetosMaisVotados extends CardFactory {
	private List<Projeto> mProjetos;
	public CardProjetosMaisVotados(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView,fragmentManager);

		int viewflipper1 = R.id.flipperPPVotados;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
		new BuscaInfos().execute();
	}

	private View montaCard(Projeto projeto) {
		LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(getFragmentActivity().LAYOUT_INFLATER_SERVICE);
		View ll = inflater.inflate(R.layout.card_proposicao_comentarios, null, false);

		// nome
		TextView txtNome = (TextView) ll.findViewById(R.id.nome);
		txtNome.setText(projeto.getNome());

		// autor
		TextView txtEvento = (TextView) ll.findViewById(R.id.autor);
		txtEvento.setText("Autor: " + projeto.getAutor().getNomeParlamentar());

		// idProjeto
		TextView txtIdElemento = (TextView) ll.findViewById(R.id.idProjeto);
		txtIdElemento.setText(String.valueOf(projeto.getId()));

		// nrVotos
		TextView txtNrcoment = (TextView) ll.findViewById(R.id.nrComentarios);
		txtNrcoment.setText(String.valueOf(projeto.getNrVotos()));
		TextView txtLblTipo = (TextView) ll.findViewById(R.id.lblTipo);
		txtLblTipo.setText("Votos");

		return ll;
	}

	/**
	 * busca os hashtags
	 */
	private class BuscaInfos extends AsyncTask<Void, Void, List<Projeto>> {

		@Override
		protected List<Projeto> doInBackground(Void... params) {
			List<Projeto> projetos = new MonitoraDAO().buscaProjetos("projetomaisvotados");

			return projetos;
		}

		protected void onPostExecute(List<Projeto> projetos) {
			try {
				getView().removeAllViews();
				mProjetos = projetos;
				// criar os cards
				for (Projeto projeto : mProjetos) {
					getView().addView(montaCard(projeto));
				}

				getView().startFlipping();
				// abrir o que o log mencionou
				getView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						View v2 = getView().getCurrentView();
						TextView txtIdProjeto = (TextView) v2.findViewById(R.id.idProjeto);
						TextView txtNome = (TextView) v2.findViewById(R.id.nome);
						Projeto selecionado = null;
						for (Projeto projeto : mProjetos) {
							if (projeto.getId() == Integer.parseInt(txtIdProjeto.getText().toString())) {
								selecionado = projeto;
							}
						}


						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(getFragmentActivity(), ProjetoDetalheActivity.class);
						Gson gson = new Gson();
						intent.putExtra("projeto", gson.toJson(selecionado));
						getFragmentActivity().startActivity(intent);
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
