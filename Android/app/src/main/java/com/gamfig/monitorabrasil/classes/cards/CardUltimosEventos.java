package com.gamfig.monitorabrasil.classes.cards;

import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Evento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardUltimosEventos extends CardFactory {

	public CardUltimosEventos(Context fragmentActivity, View rootView, FragmentManager fragmentManager) {
		super(fragmentActivity, rootView, fragmentManager);

		int viewflipper1 = R.id.viewFlipper1;
		montaViewFlipper(viewflipper1);
	}

	public void buscaInfos() {

        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/getlog.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Evento> eventos = gson.fromJson(response, new TypeToken<ArrayList<Evento>>() {}.getType());
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // pb.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(request,"tag");
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


	}

