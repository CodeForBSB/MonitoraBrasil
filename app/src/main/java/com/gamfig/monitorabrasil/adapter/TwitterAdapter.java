
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

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Twitter;

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
		// verifica se tem a #MonitoraBrasil
		int indexSearch = -1;
		indexSearch = mensagem.indexOf("#MonitoraBrasil");
		LinearLayout ln = (LinearLayout) row.findViewById(R.id.lnLayoutTwett);
		if (indexSearch >= 0) {

			ln.setBackgroundResource(R.color.white);
			holder.txtTwitterMsg.setTextColor(Color.BLACK);
			holder.txtTwitterId.setTextColor(Color.BLACK);
			holder.txtTwitterNome.setTextColor(Color.BLACK);
			holder.txtTwitterTempo.setTextColor(Color.BLACK);
		} else {
			ln.setBackgroundResource(R.color.cinzafundo);
			holder.txtTwitterMsg.setTextColor(Color.BLACK);
			holder.txtTwitterId.setTextColor(Color.BLACK);
			holder.txtTwitterNome.setTextColor(Color.BLACK);
			holder.txtTwitterTempo.setTextColor(Color.BLACK);
		}
		// busca link http
		int indexInicio = mensagem.indexOf("http://");
		int indexFim = mensagem.indexOf(" ", indexInicio);
		if (indexFim == -1)
			indexFim = mensagem.length();
		if (indexInicio > -1) {
			String link = mensagem.substring(indexInicio, indexFim);
			String linkNovo = "<a href='" + link + "'>" + link + "</a>";
			try {
				mensagem = mensagem.replaceAll(link, linkNovo);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		// busca link http
		indexInicio = mensagem.indexOf("https://");
		indexFim = mensagem.indexOf(" ", indexInicio);
		if (indexFim == -1)
			indexFim = mensagem.length();
		if (indexInicio > -1) {
			String link = mensagem.substring(indexInicio, indexFim);
			String linkNovo = "<a href='" + link + "'>" + link + "</a>";
			mensagem = mensagem.replaceAll(link, linkNovo);
		}
		holder.txtTwitterMsg.setText(Html.fromHtml(mensagem));
		holder.txtTwitterMsg.setMovementMethod(LinkMovementMethod.getInstance());

		// calcula tempo
		// long diferenca = System.currentTimeMillis() - status.getData().getTime();
		// long diferencaMin = diferenca / (60 * 1000); // diferenca em minutos
		// long diferencaHoras = diferenca / 3600000;
		// if (diferencaMin < 60) {
		// holder.txtTwitterTempo.setText(String.valueOf(diferencaMin) + "min");
		// } else if (diferencaHoras < 24) {
		//
		// holder.txtTwitterTempo.setText(String.valueOf(diferencaHoras) + "h");
		// } else {
		// holder.txtTwitterTempo.setText(String.valueOf(diferencaHoras / 24) + "d");
		// }
		holder.imgTwitter.setImageBitmap(status.getFoto());

		return row;
	}

	static class StatusHolder {

		TextView txtTwitterId;
		TextView txtTwitterNome;
		TextView txtTwitterMsg;
		TextView txtTwitterTempo;
		ImageView imgTwitter;
	}

}
