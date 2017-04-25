package com.thsw.work.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thsw.work.R;

public class SpinnersAdapter extends BaseAdapter {

	private String[] list;
	private Context context;

	public SpinnersAdapter(Context context, String[] list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.spinners_item, null);
		}
		TextView item_tv = (TextView) convertView.findViewById(R.id.item_tv);
		item_tv.setText(list[position]);
		return convertView;
	}

}
