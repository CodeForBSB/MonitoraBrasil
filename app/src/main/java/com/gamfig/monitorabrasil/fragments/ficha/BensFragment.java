package com.gamfig.monitorabrasil.fragments.ficha;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.adapter.BemAdapter;
import com.gamfig.monitorabrasil.classes.Bem;
import com.gamfig.monitorabrasil.classes.Politico;

public class BensFragment extends TabFactory {
	private ProgressBar pb;
	String casa;
	// private RelativeLayout rl;

	List<String> listDataHeader;
	HashMap<String, List<NameValuePair>> listDataChild;

	public BensFragment() {

	}

	public void montaLayout() {

		int idPolitico = getBundle().getInt("idPolitico");
		casa = getBundle().getString("casa");

		new buscaBens(getActivity(), idPolitico).execute();

	}

	public class buscaBens extends AsyncTask<Void, Void, List<Bem>> {
		Activity mActivity;
		private int idCadastro;
		Politico politico;
		ExpandableListView mExpLv;
		ProgressBar pb;
		String textoCompartilhar;

		public buscaBens(Activity activity, int idCadastro) {
			this.idCadastro = idCadastro;
			mActivity = activity;
		}

		protected void onPreExecute() {
			mExpLv = (ExpandableListView) mActivity.findViewById(R.id.lvBens);
			pb = (ProgressBar) mActivity.findViewById(R.id.progressBar1);
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Bem> doInBackground(Void... params) {
			new DeputadoDAO();			
			politico = DeputadoDAO.buscaPolitico(idCadastro, casa);
			try {
			} catch (Exception e) {
				// TODO: handle exception
			}
			return DeputadoDAO.buscaBens(politico.getIdTbrasil());
		}

		protected void onPostExecute(List<Bem> bens) {
			if (bens != null) {
				// busca o expande list view
				mExpLv = (ExpandableListView) mActivity.findViewById(R.id.lvBens);

				View headerView = View.inflate(mActivity.getApplicationContext(), R.layout.header_bens, null);
				TextView txtVariacao = (TextView) headerView.findViewById(R.id.textView3);
				WebView grafico = (WebView) headerView.findViewById(R.id.chart_view);
				grafico.getSettings().setJavaScriptEnabled(true);

				listDataHeader = new ArrayList<String>();
				listDataChild = new HashMap<String, List<NameValuePair>>();
				HashMap<Integer, Float> montantes = new HashMap<Integer, Float>();
				List<Integer> anos = new ArrayList<Integer>();
				int primeiroAno = 0, ultimoAno = 0;

				for (Bem bem : bens) {
					if (!montantes.containsKey(bem.getAno())) {
						montantes.put(bem.getAno(), bem.getValor());
						anos.add(bem.getAno());
						if (primeiroAno == 0)
							ultimoAno = bem.getAno();

						primeiroAno = bem.getAno();

					} else {
						montantes.put(bem.getAno(), (montantes.get(bem.getAno()) + bem.getValor()));
					}
				}
				DecimalFormat df = new DecimalFormat("#,###,##0.00");
				String parametrosUrl = "";
				String virgula;
				textoCompartilhar = "Evolução Patrimonial - " + politico.getNome() + "\n";
				List<NameValuePair> textos;
				for (int i = 0; i < anos.size(); i++) {
					listDataHeader.add(String.valueOf(anos.get(i)) + " R$" + df.format(montantes.get(anos.get(i))));
					textos = new ArrayList<NameValuePair>();
					for (Bem bem : bens) {
						if (bem.getAno() == anos.get(i)) {
							textos.add(new BasicNameValuePair(bem.getNome(), String.valueOf(bem.getValor())));

						}

					}
					listDataChild.put(String.valueOf(anos.get(i)), textos);
					virgula = "";
					if (i > 0) {
						virgula = ",";
					}
					parametrosUrl = "['" + anos.get(i) + "'," + String.valueOf(montantes.get(anos.get(i))) + "]" + virgula + parametrosUrl;
					textoCompartilhar = textoCompartilhar + anos.get(i) + " - " + " R$" + df.format(montantes.get(anos.get(i))) + "\n";

				}
				// textoCompartilhar = textoCompartilhar+"\nhttp://www.gamfig.com/mbrasilwsdl/rest/grafico/bens.php?param=" +
				// parametrosUrl+"\n#monitoraBrasil";
				try {
					grafico.loadUrl("http://www.gamfig.com/mbrasilwsdl/rest/grafico/bens.php?param=" + parametrosUrl);

					float variacao = ((montantes.get(ultimoAno) * 100) / montantes.get(primeiroAno)) - 100;
					String textoVariacao = "";
					if (montantes.size() > 1) {
						if (variacao > 0)
							textoVariacao = "Patrimônio aumentou " + df.format(variacao) + "%";
						else
							textoVariacao = "Patrimônio diminuiu " + df.format(variacao) + "%";
					} else {
						textoVariacao = "Não houve variação patrimonial";
					}

					txtVariacao.setText(textoVariacao);

					mExpLv.addHeaderView(headerView);
					BemAdapter listAdapter = new BemAdapter(getActivity(), listDataHeader, listDataChild);
					// // setting list adapter
					mExpLv.setAdapter(listAdapter);
					// mExpLv.expandGroup(0);
					pb.setVisibility(View.INVISIBLE);

					// setup buttons
					ImageButton btnFonte = (ImageButton) mActivity.findViewById(R.id.btnFonte);
					btnFonte.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							new AlertDialog.Builder(mActivity).setTitle(R.string.fonte).setMessage(R.string.msg_fonte_tbrasil).setIcon(R.drawable.ic_action_about)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											// continue with delete
										}
									}).show();

						}
					});

					ImageButton btnShare = (ImageButton) mActivity.findViewById(R.id.btnCompartilhar);
					btnShare.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent sendIntent = new Intent();
							sendIntent.setAction(Intent.ACTION_SEND);

							sendIntent.putExtra(Intent.EXTRA_TEXT, textoCompartilhar + "\n#monitoraBrasil https://play.google.com/store/apps/details?id=com.gamfig.monitorabrasil");
							sendIntent.setType("text/plain");
							getActivity().startActivity(sendIntent);

						}
					});

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}

	}
}
