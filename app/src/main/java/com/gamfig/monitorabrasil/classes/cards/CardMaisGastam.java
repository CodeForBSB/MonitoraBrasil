package com.gamfig.monitorabrasil.classes.cards;

import java.text.DecimalFormat;
import java.util.List;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.MonitoraDAO;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;

public class CardMaisGastam extends CardFactory {

	public CardMaisGastam(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.flipperMaisGastam;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
		new BuscaInfos().execute();
	}

	private View montaCard(Politico politico) {
		LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(getFragmentActivity().LAYOUT_INFLATER_SERVICE);
		View ll = inflater.inflate(R.layout.card_gasto, null, false);

		// nome
		TextView txtNome = (TextView) ll.findViewById(R.id.nome);
		txtNome.setText(politico.getNomeParlamentar());

		Cota cota = politico.getCotas().get(0);

		// tipo de gasto
		TextView txtEvento = (TextView) ll.findViewById(R.id.categoria);
		txtEvento.setText(cota.getTipo());

		// valor
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		TextView txtVAlor = (TextView) ll.findViewById(R.id.valor);
		txtVAlor.setText("R$ " + df.format(cota.getValor()));

		// foto
		ImageView img = (ImageView) ll.findViewById(R.id.foto);
		img.setImageBitmap(Imagens.getCroppedBitmap(politico.getFoto()));

		// idElemento
		TextView txtIdElemento = (TextView) ll.findViewById(R.id.idPolitico);
		txtIdElemento.setText(String.valueOf(politico.getIdCadastro()));

		// tipo do politico (dep ou sen)
		TextView txtTipo = (TextView) ll.findViewById(R.id.tipo);
		txtTipo.setText(String.valueOf(politico.getTipoParlamentar()));

		return ll;
	}

	/**
	 * busca os hashtags
	 */
	private class BuscaInfos extends AsyncTask<Void, Void, List<Politico>> {

		@Override
		protected List<Politico> doInBackground(Void... params) {
			List<Politico> politicos = new MonitoraDAO().buscaPoliticosMaisGastam();
			// busca fotos
			for (Politico politico : politicos) {
				politico.setFoto(Imagens.getImageBitmap(String.valueOf(politico.getIdCadastro())));
			}
			return politicos;
		}

		protected void onPostExecute(List<Politico> politicos) {
			try {
				getView().removeAllViews();
				// criar os cards
				for (Politico politico : politicos) {
					getView().addView(montaCard(politico));
				}

				getView().startFlipping();
				// abrir o que o log mencionou
				getView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						View v2 = getView().getCurrentView();
						TextView txtIdPolitico = (TextView) v2.findViewById(R.id.idPolitico);
						TextView txtTipo = (TextView) v2.findViewById(R.id.tipo);
						TextView txtNome = (TextView) v2.findViewById(R.id.nome);


						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(getFragmentActivity(), FichaActivity.class);
						intent.putExtra("idPolitico", Integer.parseInt(txtIdPolitico.getText().toString()));
						intent.putExtra("casa", txtTipo.getText().toString());
						// intent.putExtra("twitter", politico.getTwitter());
						intent.putExtra("cota", "true");
						getFragmentActivity().startActivity(intent);
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
