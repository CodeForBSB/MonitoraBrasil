
package com.gamfig.monitorabrasil.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.classes.ItemDrawer;

public class DrawerAdapter extends BaseExpandableListAdapter {
	Context _context;
	int layoutResourceId;
	List<ItemDrawer> data = null;
	private List<ItemDrawer> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<ItemDrawer, List<ItemDrawer>> _listDataChild;

	public DrawerAdapter(Context context, int layoutResourceId, List<ItemDrawer> listDataHeader, HashMap<ItemDrawer, List<ItemDrawer>> listDataChild) {

		this._context = context;
		this.layoutResourceId = layoutResourceId;
		this._listDataChild = listDataChild;
		this._listDataHeader = listDataHeader;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final ItemDrawer childText = (ItemDrawer) getChild(groupPosition, childPosition);

		StatusHolder holder = null;
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(this.layoutResourceId, null);
			holder = new StatusHolder();
			holder.texto = (TextView) convertView.findViewById(R.id.drawer_item_text);
			holder.icone = (ImageView) convertView.findViewById(R.id.drawer_item_icon);

			convertView.setTag(holder);
		} else {
			holder = (StatusHolder) convertView.getTag();
		}
		ItemDrawer item = childText;

		holder.texto.setText(item.getTexto());

		holder.icone.setVisibility(View.INVISIBLE);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (this._listDataChild.get(this._listDataHeader.get(groupPosition)) == null)
			return 0;
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
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

		StatusHolder holder = null;
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(this.layoutResourceId, null);
			holder = new StatusHolder();
			holder.texto = (TextView) convertView.findViewById(R.id.drawer_item_text);
			holder.icone = (ImageView) convertView.findViewById(R.id.drawer_item_icon);

			convertView.setTag(holder);
		} else {
			holder = (StatusHolder) convertView.getTag();
		}
		ItemDrawer item = this._listDataHeader.get(groupPosition);

		holder.texto.setText(item.getTexto());

		holder.icone.setImageBitmap(BitmapFactory.decodeResource(convertView.getResources(), item.getIcone()));

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

	static class StatusHolder {

		TextView texto;
		ImageView icone;
	}

}
