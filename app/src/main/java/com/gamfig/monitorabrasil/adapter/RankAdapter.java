
package com.gamfig.monitorabrasil.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Usuario;

public class RankAdapter extends ArrayAdapter<Usuario> {
	Context context;
	int layoutResourceId;
	private List<Usuario> data = null;
	List<Usuario> dataOriginal = null;

	List<Usuario> subItems;

	public RankAdapter(Context context, int layoutResourceId, List<Usuario> usuarios) {
		super(context, layoutResourceId, usuarios);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.dataOriginal = usuarios;
	}

	@SuppressLint("NewApi") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		UsuarioHolder holder = null;

		if (row == null) {
			LayoutInflater infater = ((Activity) context).getLayoutInflater();
			row = infater.inflate(layoutResourceId, parent, false);
			holder = new UsuarioHolder();
			holder.txtNomeRow = (TextView) row.findViewById(R.id.nome);
			holder.txtPontos = (TextView) row.findViewById(R.id.pontos);
			holder.txtPosicao = (TextView) row.findViewById(R.id.posicao);
			holder.foto = (ImageView) row.findViewById(R.id.imageView1);

			row.setTag(holder);

		} else {
			holder = (UsuarioHolder) row.getTag();
		}

		Usuario user = this.dataOriginal.get(position);
		if(user.getId() == new UserDAO(getContext()).getIdUser()){
			holder.txtPosicao.setBackground(context.getResources().getDrawable(R.drawable.fundo_borda_redonda_2));
			holder.txtPosicao.setTextColor(context.getResources().getColor(R.color.white));
		}else{
			holder.txtPosicao.setBackground(context.getResources().getDrawable(R.drawable.fundo_borda_redonda));
			holder.txtPosicao.setTextColor(context.getResources().getColor(R.color.black));
		}
		holder.id = user.getId();
		holder.txtNomeRow.setText(user.getNome());
		holder.txtPontos.setText(String.valueOf(user.getPontos())+" pontos");
		holder.txtPosicao.setText(String.valueOf(user.getPosicao()));

		return row;
	}

	static class UsuarioHolder {

		TextView txtNomeRow;
		TextView txtPosicao;
		TextView txtPontos;
		ImageView foto;

		int id;
	}
}
