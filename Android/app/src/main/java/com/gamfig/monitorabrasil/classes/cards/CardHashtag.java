package com.gamfig.monitorabrasil.classes.cards;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
import com.gamfig.monitorabrasil.activitys.FichaActivity;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Hashtag;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardHashtag extends CardFactory {

	public CardHashtag(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.flipperHashtag;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {
        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/getinfomain.php?acao=hashmaisvotadas",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Hashtag> hashtags = gson.fromJson(response, new TypeToken<ArrayList<Hashtag>>() {}.getType());

                        getView().removeAllViews();
                        // criar os cards
                        for (Hashtag hashtag : hashtags) {
                            getView().addView(montaCard(hashtag));

                        }

                        getView().startFlipping();
                        // abrir o que o log mencionou
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // pb.setVisibility(View.GONE);
                    }
                });
        AppController.getInstance().addToRequestQueue(request,"tag");
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
		txtTipo.setText(String.valueOf(hashtag.getPolitico().getTipo()));

		// foto
        try {
            Politico p = new PoliticoDAO(AppController.getInstance().getDbh().getConnectionSource()).getPolitico(hashtag.getPolitico().getIdCadastro());
            ImageView img = (ImageView) ll.findViewById(R.id.imageView1);
            Imagens.getFotoPolitico(p,img,true);

        } catch (SQLException e) {
            e.printStackTrace();
        }


		return ll;
	}



}
