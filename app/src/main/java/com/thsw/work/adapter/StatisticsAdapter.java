package com.thsw.work.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thsw.work.R;

public class StatisticsAdapter extends BaseAdapter {

	private String[] collectAreaGv;
	private Context context;
	public StatisticsAdapter(Context context,
			String[] collectAreaGv) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.collectAreaGv=collectAreaGv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return collectAreaGv.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=View.inflate(context, R.layout.staticstics_gv_item, null);
			holder=new ViewHolder();
			 holder.tv_staticstics_name = (TextView) convertView.findViewById(R.id.tv_staticstics_name);
			 holder.v_staticstics_view=(ImageView) convertView.findViewById(R.id.v_staticstics_view);
			 convertView.setTag(holder);
		}
		holder=(ViewHolder) convertView.getTag();
		holder.tv_staticstics_name.setText(collectAreaGv[position]);
		
		if (position == selectItem) {
			holder.tv_staticstics_name.setBackgroundColor(Color.YELLOW);
			holder.v_staticstics_view.setBackgroundColor(Color.RED);
			holder.tv_staticstics_name.setAlpha(80);
//			convertView.setBackgroundColor(Color.BLUE);
		} else {
			holder.tv_staticstics_name.setBackgroundColor(Color.WHITE);
			holder.v_staticstics_view.setBackgroundColor(Color.BLACK);
//			convertView.setBackgroundColor(Color.BLUE);
		}
//		 convertView.getBackground().setAlpha(80);
		
		return convertView;
	}
	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	private int selectItem = -1;
	class ViewHolder{
		TextView tv_staticstics_name;
		ImageView v_staticstics_view;
		
	}
}
