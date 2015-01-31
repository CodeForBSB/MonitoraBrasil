
package com.gamfig.monitorabrasil.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Comentario;
import com.gamfig.monitorabrasil.classes.Imagens;

public class ComentarioAdapter extends ArrayAdapter<Comentario> {
	Context context;
	int layoutResourceId;
	List<Comentario> data = null;
	HashMap<Integer, Integer> likes;

	public ComentarioAdapter(Context context, int layoutResourceId, List<Comentario> comentarios, HashMap<Integer, Integer> likes) {
		super(context, layoutResourceId, comentarios);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = comentarios;
		this.likes = likes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StatusHolder holder = null;

		if (row == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = infalInflater.inflate(layoutResourceId, parent, false);
			holder = new StatusHolder();

			holder.txtComentTexto = (TextView) row.findViewById(R.id.txtComentTexto);
			holder.txtComentTempo = (TextView) row.findViewById(R.id.txtComentTempo);
			holder.txtComentNome = (TextView) row.findViewById(R.id.txtComentNome);
			// holder.fotoFace = (ProfilePictureView) row.findViewById(R.id.fotoFaceComentario);
			holder.foto = (ImageView) row.findViewById(R.id.foto);
			holder.btnLike = (Button) row.findViewById(R.id.btnLike);
			holder.btnUnlike = (Button) row.findViewById(R.id.btnUnlike);
			holder.btnLike.setTag(holder);
			holder.btnUnlike.setTag(holder);

			holder.btnLike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					StatusHolder holder = (StatusHolder) v.getTag();
					UserDAO userDao = new UserDAO(getContext());
					userDao.likeComentario(holder.id, userDao.getIdUser(), 1);
					int like = Integer.parseInt(holder.btnLike.getText().toString());
					holder.btnLike.setText(String.valueOf(like+1));
					holder.btnLike.setEnabled(false);
					if (!holder.btnUnlike.isEnabled()) {
						holder.btnUnlike.setEnabled(true);
					}
				}
			});

			holder.btnUnlike.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					StatusHolder holder = (StatusHolder) v.getTag();
					UserDAO userDao = new UserDAO(getContext());
					userDao.likeComentario(holder.id, userDao.getIdUser(), -1);
					int like = Integer.parseInt(holder.btnUnlike.getText().toString());
					holder.btnUnlike.setText(String.valueOf(like+1));
					holder.btnUnlike.setEnabled(false);
					if (!holder.btnLike.isEnabled()) {
						holder.btnLike.setEnabled(true);
					}
				}
			});

			row.setTag(holder);
		} else {
			holder = (StatusHolder) row.getTag();
		}
		Comentario comentario = data.get(position);
		holder.id = comentario.getIdComentario();

		// se nao tiver nome entao vem da nova api
		if (comentario.getUser().getUrlFoto() == null) {

			// buscar a imagem
			// holder.fotoFace.setPresetSize(ProfilePictureView.SMALL);
			// holder.fotoFace.setProfileId(comentario.getUser().getIdFacebook());
			// holder.fotoFace.setVisibility(View.VISIBLE);
			// holder.foto.setVisibility(View.GONE);
		} else {
			// senao tem que buscar da url
			// holder.fotoFace.setVisibility(View.GONE);
			holder.foto.setVisibility(View.VISIBLE);
			Log.i("MONITORA", comentario.getUser().getUrlFoto());
			new buscaFoto(holder.foto, comentario.getUser().getUrlFoto()).execute();

		}
		holder.txtComentNome.setText(comentario.getUser().getNome());
		holder.txtComentTexto.setText(unescape(comentario.getComentario()));

		// calcula tempo
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date;
		try {
			date = (Date) formatter.parse(comentario.getData());
			long diferenca = System.currentTimeMillis() - date.getTime();
			long diferencaMin = diferenca / (60 * 1000); // diferenca em minutos
			long diferencaHoras = diferenca / 3600000;
			if (diferencaMin < 60) {
				holder.txtComentTempo.setText(String.valueOf(diferencaMin) + "min");
			} else if (diferencaHoras < 24) {

				holder.txtComentTempo.setText(String.valueOf(diferencaHoras) + "h");
			} else {
				holder.txtComentTempo.setText(String.valueOf(diferencaHoras / 24) + "d");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.btnLike.setText(String.valueOf(comentario.getLike()));
		holder.btnUnlike.setText(String.valueOf(comentario.getUnlike()));
		// verifica se ja classificou o comentario (like unlike)
		if (null != likes)
			if (likes.containsKey(comentario.getIdComentario())) {
				if (likes.get(comentario.getIdComentario()) == 1) {
					holder.btnLike.setEnabled(false);
					holder.btnUnlike.setEnabled(true);
				} else {
					holder.btnUnlike.setEnabled(false);
					holder.btnLike.setEnabled(true);
				}
			}

		return row;
	}

	private String unescape(String description) {
		return description.replaceAll("\\\\n", "\\\n");
	}

	static class StatusHolder {

		TextView txtComentTexto;
		TextView txtComentTempo;
		TextView txtComentNome;
		// ProfilePictureView fotoFace;
		ImageView foto;
		Button btnLike;
		Button btnUnlike;
		int id;
	}

	public class buscaFoto extends AsyncTask<Void, Void, String> {
		ImageView foto;
		String url;
		Bitmap imagem;

		public buscaFoto(ImageView foto, String url) {
			this.foto = foto;
			this.url = url;
		}

		@Override
		protected String doInBackground(Void... params) {
			imagem = Imagens.getImageBitmap(this.url);
			return "ok";
		}

		protected void onPostExecute(String results) {
			foto.setImageBitmap(imagem);

		}

	}

}
