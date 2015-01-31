
package com.gamfig.monitorabrasil.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Politico;

public class PoliticoMonitoradoAdapter extends ArrayAdapter<Politico> {
	Context context;
	int layoutResourceId;
	private List<Politico> data = null;
	List<Politico> dataOriginal = null;

	List<Politico> subItems;

	public PoliticoMonitoradoAdapter(Context context, int layoutResourceId, List<Politico> politicos) {
		super(context, layoutResourceId, politicos);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.setData(politicos);
		this.dataOriginal = politicos;
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
			holder.txtTwitter = (TextView) row.findViewById(R.id.txtTwitter);
			holder.txtPartido = (TextView) row.findViewById(R.id.txtPartido);
			holder.tb = (ToggleButton) row.findViewById(R.id.toggleButton1);
			
			// se tiver o togglebutton add evento
			if (holder.tb != null) {				
				holder.tb.setTag(holder);
				
			}
			row.setTag(holder);
			

			
		} else {
			holder = (PoliticoHolder) row.getTag();
		}

		Politico politico = getData().get(position);

		holder.id = politico.getIdCadastro();
		holder.txtNomeRow.setText(politico.getNome());
		holder.txtPartido.setText(politico.getPartido().getSigla());
		holder.txtTwitter.setText(politico.getTwitter());
		// if (politico.getLider() != null) {
		// if (politico.getLider().length() > 0)
		// holder.txtPartido.setText(politico.getPartido().getSigla() + " (" +
		// politico.getLider() + ")");
		// }
		
		if (holder.tb != null) {				
			holder.tb.setChecked(politico.isMonitorado());
			holder.tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// salva a situacao definida
					PoliticoHolder ph = (PoliticoHolder) buttonView.getTag();
					// salva o deputado
					Politico p = new Politico();
					p.setNome(ph.txtNomeRow.getText().toString());
					p.setIdCadastro(Integer.valueOf(ph.id));
					p.setTwitter(ph.txtTwitter.getText().toString());
					new UserDAO(context).salvaMonitorado(p, isChecked);

				}
			});
		}

		return row;
	}

	public List<Politico> getData() {
		return data;
	}

	public void setData(List<Politico> data) {
		this.data = data;
	}

	static class PoliticoHolder {

		TextView txtNomeRow;
		TextView txtPartido;
		TextView txtTwitter;
		ToggleButton tb;
		int id;
	}
}
