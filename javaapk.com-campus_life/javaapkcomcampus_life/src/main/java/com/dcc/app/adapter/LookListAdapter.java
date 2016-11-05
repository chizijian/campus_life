/**
 * 
 */
package com.dcc.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dcc.app.entity.ItemList;
import com.yhx.app.campus_life.R;

/**
 *listview 的适配器
 */
public class LookListAdapter extends BaseAdapter{
	private List<ItemList> itemList;
	public List<ItemList> getItemList() {
		return itemList;
	}
	public void setItemList(List<ItemList> itemList) {
		this.itemList = itemList;
	}

	private Context context;
	private LayoutInflater inflater;
	public LookListAdapter(Context context){
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return itemList == null ? 0 : itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView == null){
			convertView  = inflater.inflate(R.layout.shop_list_item, null);
			vh = new ViewHolder();
			vh.tv_list_title = (TextView) convertView.findViewById(R.id.tv_list_title);
			vh.tv_list_school = (TextView) convertView.findViewById(R.id.tv_list_school);
			vh.tv_list_court = (TextView) convertView.findViewById(R.id.tv_list_court);
			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		ItemList item = itemList.get(position);
		vh.tv_list_court.setText(item.getCourt());
		vh.tv_list_title.setText("【"+item.getCategory()+"】"+item.getShopname());
		vh.tv_list_school.setText(item.getSchool());
		return convertView;
	}
	private class ViewHolder {
		TextView tv_list_title,tv_list_school,tv_list_court;
	}
}
