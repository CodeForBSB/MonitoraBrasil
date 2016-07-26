/**
 * 19/11/2014
 * Autor: Geraldo A M Figueiredo
 * Email: geraldo.morais@gmail.com
 * 
 * Fragmente que busca as informacoes dos cards
 * 
 */
package com.gamfig.monitorabrasil.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.RankAdapter;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PontuacaoFragment extends Fragment {

	private Usuario user;
	private Activity activity;

	public PontuacaoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_pontuacao, container, false);
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (null != bundle.getString("user")) {
				Gson gson = new Gson();
				user = gson.fromJson(bundle.getString("user"), Usuario.class);
			} else {
				user = new UserDAO(getActivity()).getUserCompleto();
			}
		} else {
			user = new UserDAO(getActivity()).getUserCompleto();
		}

		activity = getActivity();
        Session session = Session.getActiveSession();
        if(session==null){
            // try to restore from cache
            session = Session.openActiveSessionFromCache(getActivity());
        }
        if(session != null && ( session.isOpened())){
            //buscar a lista de amgigos
           // getFriends(session);
        }

		return rootView;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		montaFormIncial();
		// busca os pontos do usuario
		new BuscaPontos().execute();
	}

	private void montaFormIncial() {
		TextView nome = (TextView) getActivity().findViewById(R.id.txtNome);
		nome.setText(user.getNome());
		Bundle bundle = getArguments();
        // busca a foto
        ImageView imgFoto = (ImageView) getActivity().findViewById(R.id.imgFoto);
        if(user.getId()== AppController.getInstance().getSharedPref().getInt(getString(R.string.id_key_idcadastro_novo),0))
            user.carregaFoto(imgFoto,"large");
        else
            Imagens.carregaImagemFacebook(user.getIdFacebook(),imgFoto,"large");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

	}

	public class BuscaPontos extends AsyncTask<Void, Void, Usuario> {

		public BuscaPontos() {
		}

		@Override
		protected Usuario doInBackground(Void... params) {
			return new UserDAO().getPontuacao(user);
		}

		protected void onPostExecute(Usuario usuario) {
			try {
				// verifica se o cadastro esta completo
				TextView txtCadastroCompleto = (TextView) activity.findViewById(R.id.txtCadastroCompleto);

				// nrComentarios
				TextView txtNrComentarios = (TextView) activity.findViewById(R.id.txtNrComentarios);
				txtNrComentarios.setText(String.valueOf(usuario.getNrComentarios()));

				// nrVotos
				TextView txtNrVotos = (TextView) activity.findViewById(R.id.txtNumeroVotos);
				txtNrVotos.setText(String.valueOf(usuario.getNrVotos()));

				// nrPoliticosMonitorados
				TextView txtNumeroPoliticos = (TextView) activity.findViewById(R.id.txtNumeroPoliticos);
				txtNumeroPoliticos.setText(String.valueOf(usuario.getNrPoliticosMonitorados()));

				// txtNumProjetos monitorados
				TextView txtNumProjetos = (TextView) activity.findViewById(R.id.txtNumProjetos);
				txtNumProjetos.setText(String.valueOf(usuario.getNrProjetosMonitorados()));

				// politicos avaliados
				TextView txtNrAvaliacao = (TextView) activity.findViewById(R.id.txtNrAvaliacao);
				txtNrAvaliacao.setText(String.valueOf(usuario.getNrAvaliacaoPolitico()));

				// total
				TextView txtTotal = (TextView) activity.findViewById(R.id.txtTotal);
				txtTotal.setText(String.valueOf(usuario.getPontosTotal()));

				// barra de progresso
				ProgressBar pb = (ProgressBar) activity.findViewById(R.id.pbNivel);
				pb.setProgress(Math.round(usuario.getProgress()));

				// nivel atual
				int nivelAtual = usuario.getNivelAtual();
				TextView txtNivel = (TextView) activity.findViewById(R.id.txtNivel);
				txtNivel.setText("Nível " + String.valueOf(nivelAtual));

				// proximo nivel
				TextView txtProxNivel = (TextView) activity.findViewById(R.id.txtProxNivel);
				txtProxNivel.setText("Nível " + String.valueOf(nivelAtual + 1));

				pb.setVisibility(View.VISIBLE);
				ProgressBar pb2 = (ProgressBar) activity.findViewById(R.id.progressBar1);
				pb2.setVisibility(View.GONE);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
