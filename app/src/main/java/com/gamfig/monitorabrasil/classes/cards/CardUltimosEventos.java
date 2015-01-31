package com.gamfig.monitorabrasil.classes.cards;

import java.util.List;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.MonitoraDAO;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.classes.Evento;

public class CardUltimosEventos extends CardFactory {

	public CardUltimosEventos(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.viewFlipper1;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
		new BuscaEventosLog().execute();
	}

	private View montaCard(Evento evento) {
		LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(getFragmentActivity().LAYOUT_INFLATER_SERVICE);
		View ll = inflater.inflate(R.layout.card_evento, null, false);

		// nome
		TextView txtNome = (TextView) ll.findViewById(R.id.nome);
		txtNome.setText(evento.getNome());

		// tempo
		TextView txtTempo = (TextView) ll.findViewById(R.id.tempo);
		txtTempo.setText(formataTempo(evento.getTempo()));

		// texto
		TextView txtEvento = (TextView) ll.findViewById(R.id.texto);
		txtEvento.setText(evento.getDesc());

		// evento
		TextView txtTpEvento = (TextView) ll.findViewById(R.id.evento);
		txtTpEvento.setText(evento.getEvento());

		// idElemento
		TextView txtIdElemento = (TextView) ll.findViewById(R.id.idElemento);
		txtIdElemento.setText(String.valueOf(evento.getIdElemento()));

		return ll;
	}

	/**
	 * busca os evento do log
	 */
	private class BuscaEventosLog extends AsyncTask<Void, Void, List<Evento>> {

		@Override
		protected List<Evento> doInBackground(Void... params) {

			return new MonitoraDAO().buscaEventos();
		}

		protected void onPostExecute(List<Evento> eventos) {
			try {
				getView().removeAllViews();
				// criar os cards
				for (Evento evento : eventos) {
					getView().addView(montaCard(evento));
				}

				getView().startFlipping();
				// abrir o que o log mencionou
				getView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						View v2 = getView().getCurrentView();
						TextView txt = (TextView) v2.findViewById(R.id.texto);
						Log.i(PrincipalActivity.TAG, txt.getText().toString());
						// TODO tratar o click e levar para a tela correspondente ao evento
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
