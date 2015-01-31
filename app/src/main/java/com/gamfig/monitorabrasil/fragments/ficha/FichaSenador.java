package com.gamfig.monitorabrasil.fragments.ficha;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.adapter.TwitterAdapter;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.gamfig.monitorabrasil.dialog.DialogAvaliacao;

public class FichaSenador extends TabFactory {
	private RelativeLayout rlPb;
	private ScrollView scrvConteudo;
	Politico mPolitico;

	// private RelativeLayout rl;

	public FichaSenador() {

	}

	public void montaLayout() {

		rlPb = (RelativeLayout) getActivity().findViewById(R.id.rlProgressBar);
		rlPb.setVisibility(View.VISIBLE);
		scrvConteudo = (ScrollView) getActivity().findViewById(R.id.scrollView1);
		scrvConteudo.setVisibility(View.GONE);
		// buscar as infos do deputado
		final int idPolitico = getBundle().getInt("idPolitico");

		new buscaPolitico(getActivity(), idPolitico, getBundle().getString("twitter")).execute();

		// esconde imagem
		ImageView foto = (ImageView) getActivity().findViewById(R.id.fotopolitico);
		foto.setVisibility(View.INVISIBLE);

		// coloca o listener no btnMonitorar
		final Button btnMonitorar = (Button) getActivity().findViewById(R.id.btnMonitorar);
		btnMonitorar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Politico pol = new Politico();
				// pol.setIdCadastro(idPolitico);
				UserDAO userdao = new UserDAO(getActivity().getApplicationContext());
				// pol = new DeputadoDAO(getActivity().getApplicationContext()).buscaPolitico(pol);
				boolean monitorado = false;
				if (btnMonitorar.getText().toString().equals("Monitorar")) {
					btnMonitorar.setText("Monitorando");
					monitorado = true;
				} else {
					btnMonitorar.setText("Monitorar");
				}
				mPolitico.setTipoParlamentar("s");
				userdao.salvaMonitorado(mPolitico, monitorado);

			}
		});

		// verificar se esta monitorando
		if (new UserDAO(getActivity().getApplicationContext()).isPoliticoMonitorado(idPolitico)) {
			btnMonitorar.setText("Monitorando");
		} else {
			btnMonitorar.setText("Monitorar");
		}

		// listener para o avalie
		final Button btnAvalie = (Button) getActivity().findViewById(R.id.btnAvaliar);
		btnAvalie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DialogAvaliacao(new UserDAO(getActivity()).getIdUser(), idPolitico, "Avalie").show(getActivity().getFragmentManager(), "dialogAvaliar");
			}
		});

	}

	public class buscaPolitico extends AsyncTask<Void, Void, String> {
		Activity mActivity;
		private int idCadastro;
		Politico politico;
		String twitter;

		public buscaPolitico(Activity activity, int idCadastro, String twitter) {
			this.idCadastro = idCadastro;
			this.twitter = twitter;
			mActivity = activity;
		}

		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(Void... params) {

			new DeputadoDAO();
			politico = DeputadoDAO.buscaPolitico(idCadastro, "s");
			if (politico == null)
				return null;
			else if (politico.getNome() == null)
				return null;
			return politico.getNome();
		}

		protected void onPostExecute(String results) {
			try {
				if (politico != null) {
					mPolitico = politico;
					// popula dados do deputado
					// fillForm(politico);
					rlPb.setVisibility(View.GONE);
					scrvConteudo.setVisibility(View.VISIBLE);

					// nome
					mActivity.getActionBar().setTitle(politico.getNome());

					TextView txtNome = (TextView) mActivity.findViewById(R.id.txtNome);
					txtNome.setText(politico.getNome());

					// twitter
					TextView txtTwitter = (TextView) mActivity.findViewById(R.id.txtTwitterFicha);
					txtTwitter.setText(politico.getTwitter());

					// foto
					Bitmap thumb_d = Imagens.getImageBitmap("http://www.senado.gov.br/senadores/img/fotos/bemv" + politico.getIdCadastro() + ".jpg");

					ImageView foto = (ImageView) mActivity.findViewById(R.id.fotopolitico);
					try {
						foto.setImageBitmap(Imagens.getCroppedBitmap(thumb_d));
						foto.setVisibility(View.VISIBLE);
					} catch (Exception e) {
						Log.e(PrincipalActivity.TAG, "Error getting imagem do deputado", e);
					}

					// email
					TextView txt = (TextView) mActivity.findViewById(R.id.txtEmail);
					txt.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
					txt.setText(politico.getEmail());

					// partido
					TextView txtPartido = (TextView) mActivity.findViewById(R.id.txtPartidoFicha);
					txtPartido.setText(politico.getPartido().getSigla() + "-" + politico.getUf());
					if (politico.getLider().length() > 0) {
						txtPartido.setText(txtPartido.getText() + " (" + politico.getLider() + ")");
					}

					// telefone
					TextView txtTel = (TextView) mActivity.findViewById(R.id.txtTel);
					txtTel.setText(politico.getTelefone());
					Linkify.addLinks(txtTel, Linkify.PHONE_NUMBERS);

					// endereco
					TextView txtEndereco = (TextView) mActivity.findViewById(R.id.txtGabinete);
					txtEndereco.setText(politico.getEndereco());

					// atualiza cota
					TextView txtCota = (TextView) mActivity.findViewById(R.id.txtCotaTotal);
					txtCota.setVisibility(View.GONE);
					// DecimalFormat df = new DecimalFormat("#,###,##0.00");
					// txtCota.setText(Html.fromHtml("<b>R$ " + df.format(politico.getValor()) + "</b><br>GASTOS"));
					// // atualiza faltas
					// int totalFalta = 0;
					// for (Presenca presenca : politico.getPresenca()) {
					// totalFalta += presenca.getNrAusenciaJustificada() + presenca.getNrAusenciaNaoJustificada();
					// }
					TextView txtPresenca = (TextView) mActivity.findViewById(R.id.txtFaltaTotal);
					txtPresenca.setVisibility(View.GONE);
					// txtPresenca.setText(Html.fromHtml("<b>" + String.valueOf(totalFalta) + "</b><br>FALTAS"));
					//
					// // atualiza numero de projetos
					TextView txtProjetos = (TextView) mActivity.findViewById(R.id.txtNrProjetos);
					txtProjetos.setVisibility(View.GONE);
					// txtProjetos.setText(Html.fromHtml("<b>" + String.valueOf(politico.getNrProjetos()) + "</b><br>PROJETOS"));

					// busca timeline do twitter
					new BuscaTimeline(getActivity(), twitter).execute();

					// busca infos tbrasil
					new BuscaInfosTbrasil(getActivity(), politico.getIdTbrasil()).execute();

					RatingBar ratingBar = (RatingBar) mActivity.findViewById(R.id.ratingBar1);
					ratingBar.setRating(politico.getNotaAvaliacao());
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		/**
		 * 
		 * @author geral_000
		 * 
		 */
		public class BuscaInfosTbrasil extends AsyncTask<Void, Void, JSONObject> {
			Activity mActivity;
			int idTbrasil;

			public BuscaInfosTbrasil(Activity activity, int id) {

				this.idTbrasil = id;
				mActivity = activity;
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject json = null;
				try {

					json = new DeputadoDAO().getInformacoesTBrasilDeputado(idTbrasil);
				} catch (Exception e) {
					// TODO: handle exception
				}

				return json;
			}

			protected void onPostExecute(JSONObject json) {
				try {
					ProgressBar pb2 = (ProgressBar) mActivity.findViewById(R.id.progressBar2);
					ProgressBar pb3 = (ProgressBar) mActivity.findViewById(R.id.progressBar3);
					pb2.setVisibility(View.GONE);
					pb3.setVisibility(View.GONE);
					if (json != null) {
						// popular lista
						TextView txtBio = (TextView) mActivity.findViewById(R.id.txtBiografia);
						TextView txtProcessos = (TextView) mActivity.findViewById(R.id.txtProcessos);
						TextView txtBancadas = (TextView) mActivity.findViewById(R.id.txtBancada);
						txtBio.setText(json.getString("miniBio"));
						if (!json.getString("processos").equals("null")) {
							txtProcessos.setText(Html.fromHtml(json.getString("processos").replace("<a", "<br><br><a")));
							txtProcessos.setMovementMethod(LinkMovementMethod.getInstance());
						} else {
							txtProcessos.setText("Sem informações");
						}
						String bancadas = json.getString("bancadas");
						if (bancadas.length() > 0) {
							bancadas = "Bancada: " + json.getString("bancadas");
							txtBancadas.setText(bancadas);
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}

		/**
		 * 
		 * @author geral_000
		 * 
		 */
		public class BuscaTimeline extends AsyncTask<Void, Void, String> {
			Activity mActivity;
			String twitter;
			ArrayList<Twitter> tweets;

			public BuscaTimeline(Activity activity, String twitter) {

				this.twitter = twitter;
				mActivity = activity;
			}

			@Override
			protected String doInBackground(Void... params) {

				new DeputadoDAO();
				try {
					// buscar o timeline
					if (twitter.length() > 0)
						tweets = DeputadoDAO.buscaTimeLine(twitter, 1);
					else
						Toast.makeText(getActivity().getApplicationContext(), "Ainda não tem twitter! Dá para acreditar?", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					// TODO: handle exception
				}

				return "";
			}

			protected void onPostExecute(String results) {
				try {

					if (tweets != null) {
						// popular lista
						TwitterAdapter tAdapter = new TwitterAdapter(mActivity.getApplicationContext(), R.layout.listview_item_twitter, tweets);
						ListView lvTwitter = (ListView) mActivity.findViewById(R.id.lvTwitter);
						lvTwitter.setAdapter(tAdapter);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}

	}
}
