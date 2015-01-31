package com.gamfig.monitorabrasil.classes.cards;

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
import com.gamfig.monitorabrasil.classes.Hashtag;
import com.gamfig.monitorabrasil.classes.Imagens;

public class CardHashtag extends CardFactory {

	public CardHashtag(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.flipperHashtag;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
		new BuscaHashtags().execute();
	}

	private View montaCard(Hashtag hashtag) {
		LayoutInflater inflater = (LayoutInflater) getFragmentActivity().getSystemService(getFragmentActivity().LAYOUT_INFLATER_SERVICE);
		View ll = inflater.inflate(R.layout.card_hashtag, null, false);

		// nome
		TextView txtNome = (TextView) ll.findViewById(R.id.nome);
		txtNome.setText(hashtag.getPolitico().getNomeParlamentar());

		// texto
		TextView txtEvento = (TextView) ll.findViewById(R.id.texto);
		txtEvento.setText(hashtag.getHashtag());

		// idElemento
		TextView txtIdElemento = (TextView) ll.findViewById(R.id.idPolitico);
		txtIdElemento.setText(String.valueOf(hashtag.getPolitico().getIdCadastro()));

		// tipo do politico (dep ou sen)
		TextView txtTipo = (TextView) ll.findViewById(R.id.tipo); 
		txtTipo.setText(String.valueOf(hashtag.getPolitico().getTipoParlamentar()));

		// foto
		ImageView img = (ImageView) ll.findViewById(R.id.imageView1);
		img.setImageBitmap(Imagens.getCroppedBitmap(hashtag.getPolitico().getFoto()));

		return ll;
	}

	/**
	 * busca os hashtags
	 */
	private class BuscaHashtags extends AsyncTask<Void, Void, List<Hashtag>> {

		@Override
		protected List<Hashtag> doInBackground(Void... params) {
			List<Hashtag> hashtags = new MonitoraDAO().buscaHastags();
			// busca fotos
			for (Hashtag hashtag : hashtags) {
				hashtag.getPolitico().setFoto(Imagens.getImageBitmap(String.valueOf(hashtag.getPolitico().getIdCadastro())));
			}

			return hashtags;
		}

		protected void onPostExecute(List<Hashtag> hastags) {
			try {
				getView().removeAllViews();
				// criar os cards
				for (Hashtag hashtag : hastags) {
					getView().addView(montaCard(hashtag));
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
						intent.putExtra("hashtag", "true");
						getFragmentActivity().startActivity(intent);

					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

}
