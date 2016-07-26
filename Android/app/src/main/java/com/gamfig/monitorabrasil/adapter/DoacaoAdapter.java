package com.gamfig.monitorabrasil.adapter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.Doador;
import com.gamfig.monitorabrasil.pojo.Util;

public class DoacaoAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<NameValuePair> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<Doador>> _listDataChild;

	public DoacaoAdapter(Context context, List<NameValuePair> listDataHeader, HashMap<String, List<Doador>> listDataChild) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listDataChild;
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

		final Doador childText = (Doador) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_cota_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.textView1);		
		if(childText.getNome() != null){
			txtListChild.setText(Util.converteStringPrimeiraMaiuscula(childText.getNome().toLowerCase()));
			txtListChild.setTypeface(null, Typeface.BOLD);
		}
		
		
		TextView txtNumero = (TextView) convertView.findViewById(R.id.textView2);
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		txtNumero.setText("R$ "+df.format(childText.getValor()));
		
		
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
//		if (corTitulo > 0) {
//			lblListHeader.setTextColor(_context.getResources().getColor(corTitulo));
//		} else {
//			lblListHeader.setTextColor(_context.getResources().getColor(corTexto));
//		}

		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(Util.converteStringPrimeiraMaiuscula(headerTitle.getName().toLowerCase()));
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		try {
			lblValor.setText("R$ "+df.format(Double.parseDouble(headerTitle.getValue())));
		} catch (Exception e) {
			lblValor.setText(headerTitle.getValue());
		}
		

		return convertView;
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
