
package com.gamfig.monitorabrasil.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Twitter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class TwitterAdapter extends ArrayAdapter<Twitter> {
	Context context;
	int layoutResourceId;
	List<Twitter> data = null;
    private ImageLoader mImagemLoader;

	public TwitterAdapter(Context context, int layoutResourceId, List<Twitter> status) {
		super(context, layoutResourceId, status);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = status;
        this.mImagemLoader = ((AppController) context.getApplicationContext()).getmImagemLoader();
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

		holder.txtTwitterMsg.setText(Html.fromHtml(mensagem));

        Linkify.addLinks(holder.txtTwitterMsg, Linkify.WEB_URLS);
//		holder.txtTwitterMsg.setMovementMethod(LinkMovementMethod.getInstance());
        holder.txtTwitterTempo.setText(status.getData());
        mImagemLoader.displayImage(status.getUrlFoto(),holder.imgTwitter);
        holder.imagem.setVisibility(View.GONE);
        if(status.getMedia()!=null){
            try {

                mImagemLoader.displayImage(status.getMedia(), holder.imagem);
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
