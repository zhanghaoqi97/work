package com.thsw.work.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class StatisticGridViewCommon extends GridView {

	public StatisticGridViewCommon(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public StatisticGridViewCommon(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public StatisticGridViewCommon(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// // TODO Auto-generated method stub
	// int ms = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,
	// MeasureSpec.AT_MOST);
	// super.onMeasure(widthMeasureSpec, ms);
	// }
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean b = super.onTouchEvent(ev);
		// Log.d("TAG","ListView.......onTouchEvent。。。。"+b+"......."+ev.getAction());
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean b = super.onInterceptTouchEvent(ev);
		// Log.d("TAG","ListView.......onInterceptTouchEvent。。。。"+b+"......."+ev.getAction());
		return true;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 请求父亲（Scrollview不截断touch事件，因为Scrollview也有滚动效果）
		// getParent().requestDisallowInterceptTouchEvent(true);
		boolean b = super.dispatchTouchEvent(ev);
		// Log.d("TAG","ListView.......dispatchTouchEvent。。。。"+b+"......."+ev.getAction());
		return true;
	}

}
