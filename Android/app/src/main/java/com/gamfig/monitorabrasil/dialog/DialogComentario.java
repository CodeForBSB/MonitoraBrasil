package com.gamfig.monitorabrasil.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.adapter.ComentarioAdapter;
import com.gamfig.monitorabrasil.classes.Comentario;
import com.gamfig.monitorabrasil.classes.Usuario;

@SuppressLint("ValidFragment")
public class DialogComentario extends DialogFragment {

	int idUser;
	int idProposta;
	String titulo;
	int tipo;// se 0 = comentarios do monitora
				// se 1 = comentarios dos parlamentares
	boolean isDebate;
	String tpComentario;

	public DialogComentario(int i, int id, String titulo) {
		this.idUser = i;
		this.idProposta = id;
		this.titulo = titulo;
		this.tipo = 0;

	}

	public DialogComentario(int i, int id, String titulo, boolean debate) {
		this.idUser = i;
		this.idProposta = id;
		this.titulo = titulo;
		this.tipo = 0;
		this.isDebate = debate;

	}

	public DialogComentario(int i, int id, String titulo, int tipo) {
		this.idUser = i;
		this.idProposta = id;
		this.titulo = titulo;
		this.tipo = tipo;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.dialog_comentario, container, false);

		final ImageButton btnPublicar = (ImageButton) view.findViewById(R.id.btnPublicar);

		final EditText txtComentario = (EditText) view.findViewById(R.id.txtComentario);
		txtComentario.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					btnPublicar.setVisibility(View.VISIBLE);
				} else {
					btnPublicar.setVisibility(View.GONE);
				}

			}
		});
		final CheckBox cbMonitorar = (CheckBox) view.findViewById(R.id.checkMonitorar);
		final TextView txtAviso = (TextView) view.findViewById(R.id.txtAviso);
		cbMonitorar.setChecked(new UserDAO(view.getContext()).isMonitorados(idProposta));
		cbMonitorar.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String mensagem;
				if (isChecked) {
					mensagem = "Receber notificação de outros comentários";
					txtAviso.setVisibility(View.VISIBLE);

				} else {
					mensagem = "Não receber notificação de outros comentários";
					txtAviso.setVisibility(View.GONE);
				}
				Comentario comentario = new Comentario();
				comentario.setId(String.valueOf(idProposta));
				Usuario user = new Usuario();
				user.setId(new UserDAO(buttonView.getContext()).getIdUser());
				comentario.setUser(user);
				comentario.setMonitarado(isChecked);
				new MudaMonitora(comentario).execute();
				new UserDAO(buttonView.getContext()).salvaComentarioMonitorado(comentario);
				Toast.makeText(view.getContext(), mensagem, Toast.LENGTH_LONG).show();

			}
		});

		// botao salvar
		btnPublicar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txtComentario.getText().length() > 0) {
					Comentario comentario = new Comentario();
					comentario.setComent(txtComentario.getText().toString());

					comentario.setMonitarado(cbMonitorar.isChecked());
					comentario.setId(String.valueOf(idProposta));
					Usuario user = new Usuario();

					user.setId(idUser);
					comentario.setUser(user);

					btnPublicar.setEnabled(false);
					new EnviaComentario(comentario, view).execute();
					txtComentario.setText("");

				} else {
					Toast.makeText(view.getContext(), "Insira um comentário", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// buscar os comentarios
		tpComentario = "proj";
		if (tipo == 1)
			tpComentario = "parlam";
		new BuscaComentarios(idProposta, view, tpComentario).execute();

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

	public class MudaMonitora extends AsyncTask<Void, Void, Boolean> {
		Comentario comentario;

		public MudaMonitora(Comentario comentario) {
			this.comentario = comentario;

		}

		@Override
		protected Boolean doInBackground(Void... params) {

			new DeputadoDAO().mudaMonintoraComentario(comentario);

			return true;
		}

	}

	public class EnviaComentario extends AsyncTask<Void, Void, Boolean> {
		Comentario comentario;
		View view;

		public EnviaComentario(Comentario comentario, View view) {
			this.comentario = comentario;
			this.view = view;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			new DeputadoDAO().insereComentario(comentario,tpComentario);

			return true;
		}

		protected void onPostExecute(Boolean retorno) {

			try {
				final ImageButton btnPublicar = (ImageButton) view.findViewById(R.id.btnPublicar);
				btnPublicar.setEnabled(true);

				final EditText txtComentario = (EditText) view.findViewById(R.id.txtComentario);
				txtComentario.setText("");

				new BuscaComentarios(Integer.valueOf(comentario.getId()), view, tpComentario).execute();
				Toast.makeText(view.getContext(), "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	public class BuscaComentarios extends AsyncTask<Void, Void, ArrayList<Comentario>> {
		int idProposta;
		View view;
		String tpComentario;

		public BuscaComentarios(int idProposta, View view, String tpComentario) {
			this.idProposta = idProposta;
			this.view = view;
			this.tpComentario = tpComentario;
		}

		@Override
		protected ArrayList<Comentario> doInBackground(Void... params) {

			return new DeputadoDAO().buscaComentarios(String.valueOf(idProposta), tpComentario);

		}

		protected void onPostExecute(ArrayList<Comentario> comentarios) {

			try {
				if (comentarios != null) {
					ListView lv = (ListView) view.findViewById(R.id.listView1);

					ComentarioAdapter adapter = new ComentarioAdapter(getActivity(), R.layout.listview_item_comentario, comentarios, new UserDAO(
							getActivity()).buscaLikes());
					lv.setAdapter(adapter);
					lv.setSelection(adapter.getCount() - 1);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar1);
			pb.setVisibility(View.GONE);

		}

	}
}