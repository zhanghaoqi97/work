package com.thsw.work.intentactivity;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thsw.work.R;
import com.thsw.work.adapter.StatisticsAdapter;
import com.thsw.work.base.BaseActivity;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/***
 * 汇总参数设置界面
 * 
 * @author pc
 * 
 */
public class StatisticsActivity extends BaseActivity implements
		OnClickListener, OnWheelChangedListener {

	private TextView tv_title_include;
	private com.thsw.work.custom.StatisticGridViewCommon mGv_statistics_area;
	private com.thsw.work.custom.StatisticGridViewCommon mGv_statistics_state;
	private ScrollView scrollView;
	private PopupWindow popupWindow;
	private TextView mTv_statistics_city;
	private WheelView mViewProvince;
	private WheelView mViewDistrict;
	private WheelView mViewCity;
	private Button mBtnConfirm;
	private Button mBtnCancel;
	private ImageView mIv_back_include;
	private String twoBodyin;
	private Button mBt_confirm;
	/** 汇总对象参数 */
	private String zwlx = "";
	/** 汇总对象地区代码 */
	private String xzdm;
	/**汇总作物状态*/
	private String zwzt="";

	@Override
	protected int inflater() {
		// TODO Auto-generated method stub
		return R.layout.activity_statistics;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String finalResult = (String) msg.obj;
			if (finalResult.equals("anyType{}")) {// 如果信息为空说明没有存到数据库中
				dialog.dismiss();
				ToastUtils.show(StatisticsActivity.this, mCurrentProviceName
						+ "," + mCurrentCityName + "," + mCurrentDistrictName
						+ "," + zwlx + "未找到结果", Toast.LENGTH_LONG);
			} else if (finalResult.equals(R.string.datebase_fail)) {// 判断数据库是否异常
				dialog.dismiss();
				ToastUtils.show(StatisticsActivity.this, R.string.fail,
						Toast.LENGTH_LONG);
			} else {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("finalResult", finalResult);
				intent.putExtra("xzdm", xzdm);
				// 专门用于向上一个活动返回数据
				// 第一个参数用于向上一个活动返回结果码，一般只使用RESULT_OK或RESULT_CANCELED这两个值
				// 第二个参数则是把带有数据的Intent传递回去
				setResult(RESULT_OK, intent);
				// 调用了finish()方法来销毁当前活动
				finish();
			}
		};
	};
	private StatisticsAdapter areaAdapter;
	private StatisticsAdapter stateAdapter;
	private Dialog dialog;

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		tv_title_include.setText("汇总参数设置");

		StatisticsAdapterData();
		mGv_statistics_area.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					scrollView.requestDisallowInterceptTouchEvent(true);

					break;
				}
				return false;
			}
		});
	}

	/**
	 * 汇总参数Adapter数据展示 Adapter的点击事件
	 */
	private void StatisticsAdapterData() {
		areaAdapter = new StatisticsAdapter(StatisticsActivity.this,
				CommonData.COLLECT_AREA_GV);
		mGv_statistics_area.setAdapter(areaAdapter);

		stateAdapter = new StatisticsAdapter(StatisticsActivity.this,
				CommonData.COLLECT_STATE_GV);
		mGv_statistics_state.setAdapter(stateAdapter);
	}

	@Override
	protected void initView() {
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		tv_title_include = (TextView) findViewById(R.id.tv_title_include);
		mGv_statistics_area = (com.thsw.work.custom.StatisticGridViewCommon) findViewById(R.id.gv_statistics_area);
		mGv_statistics_state = (com.thsw.work.custom.StatisticGridViewCommon) findViewById(R.id.gv_statistics_state);
		mTv_statistics_city = (TextView) findViewById(R.id.tv_statistics_city);
		mIv_back_include = (ImageView) findViewById(R.id.iv_back_include);
		mBt_confirm = (Button) findViewById(R.id.bt_confirm);
		mBt_confirm.setOnClickListener(this);
		mTv_statistics_city.setOnClickListener(this);
		mIv_back_include.setOnClickListener(this);
		mGv_statistics_area.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				areaAdapter.setSelectItem(position);
				areaAdapter.notifyDataSetInvalidated();
				zwlx = CommonData.COLLECT_AREA_GV[position];

			}
		});
		mGv_statistics_state.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				stateAdapter.setSelectItem(position);
				stateAdapter.notifyDataSetInvalidated();
				zwzt = CommonData.COLLECT_STATE_GV[position];
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// 当滑动GridView列表时请求父类ScrollView父类不拦截控件
			scrollView.requestDisallowInterceptTouchEvent(true);
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.iv_back_include:
			finish();
			break;
		case R.id.tv_statistics_city:
			PupupuCityList();
			break;
		case R.id.bt_confirm:
			if (mTv_statistics_city.getText().toString().trim().equals("请选择城市区")
					|| zwlx.equals("")||zwzt.equals("")) {
				ToastUtils.show(StatisticsActivity.this, "请选择完善参数信息！！",
						Toast.LENGTH_LONG);
			} else {
				dialog=DialogUtil.createLoadingDialog(StatisticsActivity.this, "加载中...", true, 0);
				dialog.show();
				/**
				 * 调用查询面积统计结果的WebService
				 */
				new Thread(new Runnable() {
					@Override
					public void run() {
						WebServiceBean bean = new WebServiceBean();
						bean.setXZDM(xzdm);
						bean.setZWLX(zwlx);
						SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
								CommonData.QUERYAREANAME,
								CommonData.QUERYAREAACTION);
						twoBodyin = bodyIn.getProperty(0).toString();
						Message msg = handler.obtainMessage();
						msg.obj = twoBodyin;
						handler.sendMessage(msg);
					}
				}).start();
			}

			break;
		}
	}

	private void PupupuCityList() {
		View view = View.inflate(StatisticsActivity.this,
				R.layout.wellview_pop, null);
		mViewProvince = (WheelView) view.findViewById(R.id.id_province);
		mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
		mViewCity = (WheelView) view.findViewById(R.id.id_city);
		mBtnConfirm = (Button) view.findViewById(R.id.btn_confirms);
		mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
		// 设置popupwindow视图,规定popupwindow宽高
		popupWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 点击popupwindow区域外部时,让popupwindow消失(想要实现这个效果,一定要设置popupwindow的背景)
		popupWindow.setOutsideTouchable(false);
		// 获取焦点
		popupWindow.setFocusable(true);
		// backgroundAlpha(1f);
		// 设popupwindow背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 展示popupwindow,参数一:popupwindow展示时的父控件,参数二:popupwindow展示时的重力方向,参数三:x轴偏移值,参数四:y轴偏移值,
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		setUpListener();
		setPopuListener();
		setUpData();
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}

	private void setPopuListener() {
		// TODO Auto-generated method stub
		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				xzdm = mCurrentZipCode;
				// TODO Auto-generated method stub
				mTv_statistics_city.setText(mCurrentProviceName + ","
						+ mCurrentCityName + "," + mCurrentDistrictName + ","
						+ mCurrentZipCode);
				popupWindow.dismiss();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				StatisticsActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(10);
		mViewCity.setVisibleItems(10);
		mViewDistrict.setVisibleItems(10);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

}
