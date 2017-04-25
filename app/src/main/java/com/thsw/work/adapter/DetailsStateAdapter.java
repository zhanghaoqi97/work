package com.thsw.work.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.thsw.work.R;
import com.thsw.work.fragment.DetailStandardFragment;

public class DetailsStateAdapter extends BaseAdapter implements ListAdapter {

	private String[] detailmessage;
	private Context context;
//	private List<DetailMessage> list;

	public DetailsStateAdapter(Context context, String[] detailsMessage) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.detailmessage = detailsMessage;
//		this.list = mDetailList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return detailmessage.length;
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
			convertView = View.inflate(context, R.layout.details_message_item,
					null);
			holder = new ViewHolder();
			holder.tv_detailitem_type = (TextView) convertView
					.findViewById(R.id.tv_detailitem_type);
			holder.tv_detilsitem_status = (TextView) convertView
					.findViewById(R.id.tv_detilsitem_status);
			holder.tv_detilsitem_time = (TextView) convertView
					.findViewById(R.id.tv_detilsitem_time);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.tv_detailitem_type.setText(detailmessage[position]);
		String time = DetailStandardFragment.mDetailList.get(position).getN_TIME();
		// SimpleDateFormat sdf = new SimpleDateFormat(" yyyy年MM月dd日 ");
		// String str = sdf.format(time);
//		 holder.tv_detilsitem_status.setText(list.get(position).getZHS());
		 holder.tv_detilsitem_time.setText(time);

		return convertView;
	}

	class ViewHolder {
		TextView tv_detailitem_type, tv_detilsitem_status, tv_detilsitem_time;
	}

}
