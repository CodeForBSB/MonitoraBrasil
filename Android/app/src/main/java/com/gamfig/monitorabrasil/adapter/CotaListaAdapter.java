/*******************************************************************************
 * Copyright  2013 de Geraldo Augusto de Morais Figueiredo
 * Este arquivo � parte do programa Monitora, Brasil!. O Monitora, Brasil! � um software livre.
 * Voc� pode redistribu�-lo e/ou modific�-lo dentro dos termos da GNU Affero General Public License 
 * como publicada pela Funda��o do Software Livre (FSF); na vers�o 3 da Licen�a. 
 * Este programa � distribu�do na esperan�a que possa ser �til, mas SEM NENHUMA GARANTIA,
 * sem uma garantia impl�cita de ADEQUA��O a qualquer MERCADO ou APLICA��O EM PARTICULAR. 
 * Veja a licen�a para maiores detalhes. 
 * Voc� deve ter recebido uma c�pia da GNU Affero General Public License, sob o t�tulo "LICENSE.txt", 
 * junto com este programa, se n�o, acesse http://www.gnu.org/licenses/
 ******************************************************************************/
package com.gamfig.monitorabrasil.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.MediaCotas;
import com.gamfig.monitorabrasil.classes.Politico;

public class CotaListaAdapter extends ArrayAdapter<Politico> {
	Context context;
	int layoutResourceId;
	private List<Politico> data = null;
	List<Politico> dataOriginal = null;
	private List<MediaCotas> mediaCotas;

	List<Politico> subItems;

	public CotaListaAdapter(Context context, int layoutResourceId, List<Politico> politicos, List<MediaCotas> mediaCotas) {
		super(context, layoutResourceId, politicos);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.setData(politicos);
		this.dataOriginal = politicos;
		this.setMedias(mediaCotas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PoliticoHolder holder = null;

		if (row == null) {
			LayoutInflater infater = ((Activity) context).getLayoutInflater();
			row = infater.inflate(layoutResourceId, parent, false);
			holder = new PoliticoHolder();

			holder.txtNomeRow = (TextView) row.findViewById(R.id.txtNomeRow);
			holder.txtPosicao = (TextView) row.findViewById(R.id.txtPosicao);
			holder.txtValores = (TextView) row.findViewById(R.id.txtValores);
			holder.txtMedias = (TextView) row.findViewById(R.id.txtMedias);
			holder.txtAnos = (TextView) row.findViewById(R.id.txtAnos);
			holder.txtPosicao = (TextView) row.findViewById(R.id.txtPosicao);
			holder.tbGastos = (TableLayout) row.findViewById(R.id.tbGastos);

			holder.txtValorTotal = (TextView) row.findViewById(R.id.txtValorTotal);

			holder.txtMediaTotal = (TextView) row.findViewById(R.id.txtMediaTotal);

			holder.btnCompartilhar = (ImageButton) row.findViewById(R.id.btnCompartilhar);
			holder.btnCompartilhar.setTag(holder);
			holder.btnCompartilhar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					PoliticoHolder holder = (PoliticoHolder) v.getTag();
					compartilhar(holder);

				}

			});

			row.setTag(holder);
			

		} else {
			holder = (PoliticoHolder) row.getTag();
		}

		Politico politico = getData().get(position);
		holder.mPolitico = politico;
		holder.id = politico.getIdCadastro();
		holder.txtNomeRow.setText(politico.getNome());
		String anos = "";
		String valoresAnos = "";
		String mediaGastos = "";
		double total = 0;
		double mediaTotal = 0;
		DecimalFormat df = new DecimalFormat("#,###,##0.00");

		for (Cota cota : politico.getCotas()) {
			String finalString = "\n";
			if (cota.equals(politico.getCotas().get(politico.getCotas().size() - 1))) {
				finalString = "";
			}

			anos = anos.concat(String.valueOf(cota.getAno()) + finalString);

			valoresAnos = valoresAnos.concat(df.format(cota.getValor()) + finalString);

			total += cota.getValor();
			double media = 0;
			// calcular a m�dia
			for (MediaCotas cotaItem : mediaCotas) {
				if (cotaItem.getAno() == cota.getAno()) {
					media = cotaItem.getValorMedia();
					mediaTotal += media;
				}
			}
			double percentual = (cota.getValor() * 100 / media) - 100;
			mediaGastos = mediaGastos.concat(df.format(percentual) + finalString);

		}

		holder.txtValorTotal.setText(df.format(total));
		double mediaFinal = (total * 100 / mediaTotal) - 100;
		holder.txtMediaTotal.setText(df.format(mediaFinal));
		if (mediaFinal > 0) {
			holder.txtMediaTotal.setTextColor(context.getResources().getColor(R.color.vermelho));
			holder.txtValorTotal.setTextColor(context.getResources().getColor(R.color.vermelho));
		} else {

			holder.txtMediaTotal.setTextColor(context.getResources().getColor(R.color.vermelho));
			holder.txtValorTotal.setTextColor(context.getResources().getColor(R.color.vermelho));
		}

		holder.txtAnos.setText(anos);
		holder.txtValores.setText(valoresAnos);
		holder.txtMedias.setText(mediaGastos);

		holder.txtPosicao.setText(String.valueOf(politico.getPosicao()));

		return row;
	}

	protected void compartilhar(PoliticoHolder holder) {

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, montaTexto(holder));
		sendIntent.setType("text/plain");
		context.startActivity(sendIntent);

	}

	private String montaTexto(PoliticoHolder holder) {
		Politico politico = holder.mPolitico;
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		double total = 0;
		double mediaTotal = 0;
		String texto = holder.txtPosicao.getText().toString()+"�)";
		if (politico.getTwitter().length() > 0) {
			texto =texto + politico.getNome() + " " + politico.getTwitter() + " gastou: \n";
		} else {
			texto =texto + politico.getNome() + " gastou: \n";
		}
		String sinal = "-";
		for (Cota cota : politico.getCotas()) {

			texto = texto + String.valueOf(cota.getAno());
			texto = texto + ": R$" + df.format(cota.getValor());

			total += cota.getValor();
			double media = 0;
			// calcular a media
			for (MediaCotas cotaItem : mediaCotas) {
				if (cotaItem.getAno() == cota.getAno()) {
					media = cotaItem.getValorMedia();
					mediaTotal += media;
				}
			}
			double percentual = (cota.getValor() * 100 / media) - 100;

			if (percentual > 0)
				sinal = "+";
			texto = texto + " (" + sinal + df.format(percentual) + "% da média)\n";

		}
		texto = texto + "= R$" + df.format(total);
		double mediaFinal = (total * 100 / mediaTotal) - 100;
		if (mediaFinal > 0)
			sinal = "+";
		else
			sinal = "-";
		texto = texto + " (" + sinal + df.format(mediaFinal) + "% da m�dia)\n#monitoraBrasil";
		return texto;
	}

	public List<Politico> getData() {
		return data;
	}

	public void setData(List<Politico> data) {
		this.data = data;
	}
	public List<MediaCotas> getMedias() {
		return mediaCotas;
	}

	public void setMedias(List<MediaCotas> medias) {
		this.mediaCotas = medias;
	}
	
	 

	static class PoliticoHolder {

		TextView txtNomeRow;
		TextView txtPosicao;
		TableLayout tbGastos;

		TextView txtAnos;
		TextView txtValores;
		TextView txtMedias;

		TextView txtMediaTotal;
		TextView txtValorTotal;

		ImageButton btnCompartilhar;

		Politico mPolitico;

		int id;
	}
}
