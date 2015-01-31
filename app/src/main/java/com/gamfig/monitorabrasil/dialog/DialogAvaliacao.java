package com.gamfig.monitorabrasil.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;

@SuppressLint("ValidFragment")
public class DialogAvaliacao extends DialogFragment {

	int idUser;
	int idPolitico;
	String titulo;

	public DialogAvaliacao(int i, int id, String titulo) {
		this.idUser = i;
		this.idPolitico = id;
		this.titulo = titulo;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.dialog_avaliacao, container, false);

		final RatingBar rb = (RatingBar) view.findViewById(R.id.ratingBar1);

		// setar avaliacao feita
		rb.setRating(new UserDAO(getActivity()).buscaAvaliacaoSalva(idPolitico));

		Button btnCancelar = (Button) view.findViewById(R.id.cancel);
		btnCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDialog().dismiss();

			}
		});

		Button btnOk = (Button) view.findViewById(R.id.ok);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Avalia(rb.getRating(),getActivity()).execute();
				new UserDAO(getActivity()).salvaAvaliacaoPolitico(idPolitico,rb.getRating());
				getDialog().dismiss();
			}
		});
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setTitle(titulo);

		// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.copyFrom(dialog.getWindow().getAttributes());
		// lp.width = WindowManager.LayoutParams.MATCH_PARENT-20;
		// dialog.show();
		// dialog.getWindow().setAttributes(lp);

		return dialog;
	}

	public class Avalia extends AsyncTask<Void, Void, Boolean> {

		float rating;
		Context context;

		public Avalia(float rating, Context context) {
			this.rating = rating;
			this.context = context;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			new DeputadoDAO(getActivity()).avaliaPolitico(String.valueOf(idUser), idPolitico, rating);

			return true;
		}
		
		protected void onPostExecute(Boolean retorno) {
			Toast.makeText(context, "Avaliação enviada!", Toast.LENGTH_SHORT).show();
		}

	}

}