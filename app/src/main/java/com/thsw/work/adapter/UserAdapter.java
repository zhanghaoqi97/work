package com.thsw.work.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.thsw.work.R;
import com.thsw.work.datafile.CommonData;

public class UserAdapter extends BaseAdapter implements ListAdapter {

	private Context context;
	private List<String> list;

	public UserAdapter(Context context, List<String> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
//		return userlist != null ? userlist.length : 0;
		return list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.user_list_item, null);
			holder = new ViewHolder();
			holder.tv_user_title = (TextView) convertView
					.findViewById(R.id.tv_user_title);
			holder.tv_user_content = (TextView) convertView
					.findViewById(R.id.tv_user_content);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
//		Log.i("TAG", CommonData.USERMESSAGE[position]+list.get(position)+"");
		holder.tv_user_title.setText(CommonData.USERMESSAGE[position]);
		holder.tv_user_content.setText(list.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView tv_user_title, tv_user_content;

	}

}
