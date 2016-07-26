package com.gamfig.monitorabrasil.adapter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Beneficiario;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.pojo.Util;

public class CotaAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<NameValuePair> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<Beneficiario>> _listDataChild;
	private Politico _politico;

	public CotaAdapter(Context context, List<NameValuePair> listDataHeader, HashMap<String, List<Beneficiario>> listDataChild, Politico politico) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listDataChild;
		this._politico = politico;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition).getName()).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final Beneficiario childText = (Beneficiario) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_cota_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.textView1);
		txtListChild.setText(Util.converteStringPrimeiraMaiuscula(childText.getNome().toLowerCase()));
		txtListChild.setTypeface(null, Typeface.BOLD);

		TextView txtNumero = (TextView) convertView.findViewById(R.id.textView2);
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		txtNumero.setText("R$ " + df.format(childText.getValor()));

		TextView txtMes = (TextView) convertView.findViewById(R.id.txtMes);
		txtMes.setText(String.valueOf(childText.getMes()) + "/" + String.valueOf(childText.getAno()));
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition).getName()).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		NameValuePair headerTitle = (NameValuePair) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_cota_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.txtCategoria);
		TextView lblValor = (TextView) convertView.findViewById(R.id.txtValor);
		ImageButton btnCompartilhar = (ImageButton) convertView.findViewById(R.id.btnCompartilhar);
		btnCompartilhar.setTag(headerTitle);
		btnCompartilhar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				NameValuePair headerTitle = (NameValuePair) v.getTag();
				compartilhar(headerTitle);

			}

		});
		// if (corTitulo > 0) {
		// lblListHeader.setTextColor(_context.getResources().getColor(corTitulo));
		// } else {
		// lblListHeader.setTextColor(_context.getResources().getColor(corTexto));
		// }

		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(Util.converteStringPrimeiraMaiuscula(headerTitle.getName().toLowerCase()));
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		try {
			lblValor.setText("R$ " + df.format(Double.parseDouble(headerTitle.getValue())));
		} catch (Exception e) {
			lblValor.setText(headerTitle.getValue());
		}

		return convertView;
	}

	protected void compartilhar(NameValuePair headerTitle) {

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, montaTexto(headerTitle));
		sendIntent.setType("text/plain");
		_context.startActivity(sendIntent);

	}

	private String montaTexto(NameValuePair headerTitle) {
		Politico politico = this._politico;
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		String texto = "";
		if (politico.getTwitter().length() > 0) {
			texto = texto + politico.getNome() + " " + politico.getTwitter() + " gastou: \n";
		} else {
			texto = texto + politico.getNome() + " gastou: \n";
		}
		texto = texto + "R$ " + df.format(Double.parseDouble(headerTitle.getValue())) + "em "
				+ Util.converteStringPrimeiraMaiuscula(headerTitle.getName().toLowerCase()) + " #monitoraBrasil";
		return texto;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
