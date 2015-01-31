package com.gamfig.monitorabrasil.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.activitys.PrincipalActivity;
import com.gamfig.monitorabrasil.classes.Hashtag;

public class HashtagAdapter extends ArrayAdapter<Hashtag> {
	Context context;
	int layoutResourceId;
	private List<Hashtag> data = null;

	public HashtagAdapter(Context context, int layoutResourceId, List<Hashtag> hashes) {
		super(context, layoutResourceId, hashes);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = hashes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		HashtagHolder holder = null;

		if (row == null) {
			LayoutInflater infater = ((Activity) context).getLayoutInflater();
			row = infater.inflate(layoutResourceId, parent, false);
			holder = new HashtagHolder();
			holder.txtHash = (TextView) row.findViewById(R.id.textView1);
			holder.btnLike = (Button) row.findViewById(R.id.imgButtonlike);
			holder.btnUnlike = (Button) row.findViewById(R.id.imgButtonUnlike);

			row.setTag(holder);

			holder.btnLike.setTag(holder);
			holder.btnUnlike.setTag(holder);

			OnClickListener onclick = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button btn = (Button) v;
					HashtagHolder holder = (HashtagHolder) btn.getTag();
					String voto = "unlike";
					if (btn.equals(holder.btnLike)) {
						voto = "like";
					}
					// enviar voto
					Log.i(PrincipalActivity.TAG, voto + " " + String.valueOf(holder.id));
					//atualizar o numero de likes
					if(voto.equals("like")){
						holder.btnLike.setText(String.valueOf(Integer.parseInt(holder.btnLike.getText().toString())+1));
					}else{
						holder.btnUnlike.setText(String.valueOf(Integer.parseInt(holder.btnUnlike.getText().toString())+1));
					}
					new EnviaLike(voto, holder.id, holder).execute();

				}
			};

			// eventos de click
			holder.btnLike.setOnClickListener(onclick);
			holder.btnUnlike.setOnClickListener(onclick);

		} else {
			holder = (HashtagHolder) row.getTag();
		}

		Hashtag hash = data.get(position);

		holder.id = hash.getId();
		holder.txtHash.setText(hash.getHashtag());
		// colocar numero no botao e eventos
		holder.btnLike.setText(String.valueOf(hash.getNrLikes()));
		holder.btnUnlike.setText(String.valueOf(hash.getNrUnlikes()));

		return row;
	}

	public List<Hashtag> getData() {
		return data;
	}

	public void setData(List<Hashtag> data) {
		this.data = data;
	}

	static class HashtagHolder {

		TextView txtHash;
		Button btnLike;
		Button btnUnlike;
		int id;
	}

	/**
	 * envia like/unlike
	 */
	public class EnviaLike extends AsyncTask<Void, Void, Void> {
		String voto;
		int idHashtag;
		HashtagHolder holder;

		public EnviaLike(String voto, int idHashtag, HashtagHolder holder) {
			this.voto = voto;
			this.idHashtag = idHashtag;
			this.holder = holder;
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			//TODO user id atualizar
			new DeputadoDAO().enviaLikeHash(voto,idHashtag,1);
			return null;
		}

		protected void onPostExecute(ArrayList<Hashtag> results) {
			
			

		}
	}
}
