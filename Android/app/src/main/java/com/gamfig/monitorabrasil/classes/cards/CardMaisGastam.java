package com.gamfig.monitorabrasil.classes.cards;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.MonitoraDAO;
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.Hashtag;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CardMaisGastam extends CardFactory {

	public CardMaisGastam(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.flipperMaisGastam;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/getinfomain.php?acao=maisgastam",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Politico> politicos = gson.fromJson(response, new TypeToken<ArrayList<Politico>>() {}.getType());

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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // pb.setVisibility(View.GONE);
                    }
                });
        AppController.getInstance().addToRequestQueue(request, "tag");
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
        ImageView img = (ImageView) ll.findViewById(R.id.foto);
        try {
            Politico p = new PoliticoDAO(AppController.getInstance().getDbh().getConnectionSource()).getPolitico(politico.getIdCadastro());
           // img.setImageBitmap(Imagens.getCroppedBitmap(Imagens.getFotoPolitico(p)));
            Imagens.getFotoPolitico(p,img,false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		// foto



		// idElemento
		TextView txtIdElemento = (TextView) ll.findViewById(R.id.idPolitico);
		txtIdElemento.setText(String.valueOf(politico.getIdCadastro()));

		// tipo do politico (dep ou sen)
		TextView txtTipo = (TextView) ll.findViewById(R.id.tipo);
		txtTipo.setText(String.valueOf(politico.getTipo()));

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
