
package com.gamfig.monitorabrasil.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.gamfig.monitorabrasil.pojo.Util;

public class TwitterAdapter extends ArrayAdapter<Twitter> {
	Context context;
	int layoutResourceId;
	List<Twitter> data = null;

	public TwitterAdapter(Context context, int layoutResourceId, List<Twitter> status) {
		super(context, layoutResourceId, status);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = status;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StatusHolder holder = null;

		if (row == null) {
			LayoutInflater infater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = infater.inflate(layoutResourceId, parent, false);
			holder = new StatusHolder();
			holder.txtTwitterId = (TextView) row.findViewById(R.id.txtTwitterId);
			holder.txtTwitterNome = (TextView) row.findViewById(R.id.txtTwitterNome);
			holder.txtTwitterMsg = (TextView) row.findViewById(R.id.txtTwitterMsg);
			holder.txtTwitterTempo = (TextView) row.findViewById(R.id.txtTwitterTempo);
			holder.imgTwitter = (ImageView) row.findViewById(R.id.imgTwitter);

            holder.imagem = (ImageView) row.findViewById(R.id.imageView);


			row.setTag(holder);
		} else {
			holder = (StatusHolder) row.getTag();
		}
		Twitter status = data.get(position);

		holder.txtTwitterId.setText(Html.fromHtml("<a href='http://twitter.com/#!/" + status.getScreenName() + "'>@" + status.getScreenName()
				+ "</a>"));
		holder.txtTwitterId.setMovementMethod(LinkMovementMethod.getInstance());

		holder.txtTwitterNome.setText(status.getNome());

		String mensagem = status.getTexto();

		// busca link http

		holder.txtTwitterMsg.setText(Html.fromHtml(Util.formataTextoTwitter(mensagem)));
		holder.txtTwitterMsg.setMovementMethod(LinkMovementMethod.getInstance());
        holder.txtTwitterTempo.setText(status.getData());

		holder.imgTwitter.setImageBitmap(status.getFoto());

        try {
            holder.imgTwitter.setImageBitmap(Imagens.getImageBitmap(status.getUrlFoto()));
            holder.imgTwitter.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        holder.imagem.setVisibility(View.GONE);
        if(status.getMedia()!=null){
            try {
                holder.imagem.setImageBitmap(Imagens.getImageBitmap(status.getMedia()));
                holder.imagem.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

		return row;
	}

	static class StatusHolder {

		TextView txtTwitterId;
		TextView txtTwitterNome;
		TextView txtTwitterMsg;
		TextView txtTwitterTempo;
        ImageView imgTwitter;
        ImageView imagem;
	}

}
