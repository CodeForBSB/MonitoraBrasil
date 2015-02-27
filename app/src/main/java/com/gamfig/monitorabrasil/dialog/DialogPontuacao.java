package com.gamfig.monitorabrasil.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.fragments.PontuacaoFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class DialogPontuacao extends DialogFragment {

    Usuario user;
    View view;

	public DialogPontuacao(Usuario user) {

        this.user=user;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_pontuacao, container, false);
        this.view=view;
        montaFormIncial();

		return view;
	}

    private void montaFormIncial() {
        TextView nome = (TextView) view.findViewById(R.id.txtNome);
        nome.setText(user.getNome());
        Bundle bundle = getArguments();
        // busca a foto
        ImageView imgFoto = (ImageView) view.findViewById(R.id.imgFoto);
        if(user.getId()== AppController.getInstance().getSharedPref().getInt(getString(R.string.id_key_idcadastro_novo),0))
            user.carregaFoto(imgFoto,"large");
        else
            Imagens.carregaImagemFacebook(user.getIdFacebook(), imgFoto, "large");
       buscaPontos();
    }

    public void buscaPontos(){
        StringRequest request = new StringRequest(Request.Method.POST , AppController.URL + "rest/get_user_pontuacao.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Usuario user = gson.fromJson(response, Usuario.class);

                        // verifica se o cadastro esta completo
                        TextView txtCadastroCompleto = (TextView) view.findViewById(R.id.txtCadastroCompleto);

                        // nrComentarios
                        TextView txtNrComentarios = (TextView) view.findViewById(R.id.txtNrComentarios);
                        txtNrComentarios.setText(String.valueOf(user.getNrComentarios()));

                        // nrVotos
                        TextView txtNrVotos = (TextView) view.findViewById(R.id.txtNumeroVotos);
                        txtNrVotos.setText(String.valueOf(user.getNrVotos()));

                        // nrPoliticosMonitorados
                        TextView txtNumeroPoliticos = (TextView) view.findViewById(R.id.txtNumeroPoliticos);
                        txtNumeroPoliticos.setText(String.valueOf(user.getNrPoliticosMonitorados()));

                        // txtNumProjetos monitorados
                        TextView txtNumProjetos = (TextView) view.findViewById(R.id.txtNumProjetos);
                        txtNumProjetos.setText(String.valueOf(user.getNrProjetosMonitorados()));

                        // politicos avaliados
                        TextView txtNrAvaliacao = (TextView) view.findViewById(R.id.txtNrAvaliacao);
                        txtNrAvaliacao.setText(String.valueOf(user.getNrAvaliacaoPolitico()));

                        // total
                        TextView txtTotal = (TextView) view.findViewById(R.id.txtTotal);
                        txtTotal.setText(String.valueOf(user.getPontosTotal()));

                        // barra de progresso
                        ProgressBar pb = (ProgressBar) view.findViewById(R.id.pbNivel);
                        pb.setProgress(Math.round(user.getProgress()));

                        // nivel atual
                        int nivelAtual = user.getNivelAtual();
                        TextView txtNivel = (TextView) view.findViewById(R.id.txtNivel);
                        txtNivel.setText("Nível " + String.valueOf(nivelAtual));

                        // proximo nivel
                        TextView txtProxNivel = (TextView) view.findViewById(R.id.txtProxNivel);
                        txtProxNivel.setText("Nível " + String.valueOf(nivelAtual + 1));

                        pb.setVisibility(View.VISIBLE);
                        ProgressBar pb2 = (ProgressBar) view.findViewById(R.id.progressBar1);
                        pb2.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("id",String.valueOf(user.getId()));
                return params;
            }};
        request.setTag("tag");
        AppController.getInstance().addToRequestQueue(request);
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setTitle("Pontuação");
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

		// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.copyFrom(dialog.getWindow().getAttributes());
		// lp.width = WindowManager.LayoutParams.MATCH_PARENT-20;
		// dialog.show();
		// dialog.getWindow().setAttributes(lp);
		return dialog;
	}


}