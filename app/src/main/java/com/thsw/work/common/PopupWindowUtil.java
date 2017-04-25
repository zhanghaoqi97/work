package com.thsw.work.common;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.thsw.work.R;
import com.thsw.work.application.MyApplication;
import com.thsw.work.utils.InflateUtils;

public class PopupWindowUtil {

	private ListView listView;
	private PopupWindow window;
	// 窗口在x轴偏移量
	private int xOff = 0;
	// 窗口在y轴的偏移量
	private int yOff = 0;
	private Context contexts;

	@SuppressWarnings("deprecation")
	public PopupWindowUtil(Context context, List<String> datas) {
		contexts = context;
		window = new PopupWindow(context);
		// ViewGroup.LayoutParams.WRAP_CONTENT，自动包裹所有的内容
		window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setFocusable(true);
		// 点击 back 键的时候，窗口会自动消失
		window.setBackgroundDrawable(new BitmapDrawable());

		View localView = LayoutInflater.from(context).inflate(
				R.layout.lv_pw_menu, null);
		listView = (ListView) localView.findViewById(R.id.lv_pop_list);

		listView.setAdapter(new MyAdapter(context, datas));
		listView.setTag(window);
		// 设置显示的视图
		window.setContentView(localView);
	}

	public void setItemClickListener(AdapterView.OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}

	public void dismiss() {
		window.dismiss();
	}

	/**
	 * @param xOff
	 *            x轴（左右）偏移
	 * @param yOff
	 *            y轴（上下）偏移
	 */
	public void setOff(int xOff, int yOff) {
		this.xOff = xOff;
		this.yOff = yOff;
	}

	/**
	 * @param paramView
	 *            点击的按钮
	 */
	@SuppressLint("NewApi")
	public void show(View paramView, int count) {
		// 该count 是手动调整窗口的宽度
		window.setWidth(paramView.getWidth() * count);
		// 设置窗口显示位置, 后面两个0 是表示偏移量，可以自由设置
		// window.showAsDropDown(paramView, xOff, yOff);
		// window.showAtLocation(paramView, Gravity.RIGHT, px2dip(contexts,
		// 400),
		// px2dip(contexts, 700));

		// xoff,yoff基于anchor的左下角进行偏移。
		window.showAsDropDown(paramView,
				InflateUtils.dip2px(MyApplication.getContext(), 0),
				InflateUtils.dip2px(MyApplication.getContext(), -50));
		// 更新窗口状态
		window.update();
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	class MyAdapter extends BaseAdapter {

		private Context context;
		private List<String> mDatas;

		public MyAdapter(Context context, List<String> datas) {
			this.context = context;
			if (datas == null) {
				datas = new ArrayList<>();
			}
			mDatas = datas;
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvItem;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.lv_item_pw_menu, null);
				tvItem = (TextView) convertView
						.findViewById(R.id.tv_item_pw_menu);
				convertView.setTag(tvItem);
			} else {
				tvItem = (TextView) convertView.getTag();
			}
			tvItem.setText(getItem(position) + "");
			return convertView;
		}

	}
}
