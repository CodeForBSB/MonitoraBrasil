package com.gamfig.monitorabrasil.adapter;

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

public class BemAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<NameValuePair>> _listDataChild;

	public BemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<NameValuePair>> listDataChild) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listDataChild;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition).substring(0, 4)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final NameValuePair childText = (NameValuePair) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_bem_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.textView1);
		txtListChild.setText(childText.getName());

		TextView txtNumero = (TextView) convertView.findViewById(R.id.textView2);
		txtNumero.setText(childText.getValue());

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition).substring(0, 4)).size();
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
		try {
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_presenca_group, null);
			}

			TextView lblListHeader = (TextView) convertView.findViewById(R.id.txtAno);
			// if (corTitulo > 0) {
			// lblListHeader.setTextColor(_context.getResources().getColor(corTitulo));
			// } else {
			// lblListHeader.setTextColor(_context.getResources().getColor(corTexto));
			// }

			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

		} catch (Exception e) {
			// TODO: handle exception
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
