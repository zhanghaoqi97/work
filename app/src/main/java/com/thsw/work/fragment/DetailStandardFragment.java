package com.thsw.work.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.thsw.work.R;
import com.thsw.work.base.BaseFragment;
import com.thsw.work.bean.DetailMessage;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 标准模式
 * 
 * @author pc
 * 
 */
public class DetailStandardFragment extends BaseFragment implements
		OnClickListener {
	/** 乡镇村名称 */
	private static TextView mTv_plotdata_area;
	/** 当前作物 */
	private static TextView mTv_plot_crop;
	/** 面积 */
	private static TextView mTv_plot_area;
	/** 种植户 */
	private static TextView mTv_plot_plant;
	/** 联系方式 */
	private static TextView mTv_plot_phone;
	/** 推送给用户的建议 */
	private static TextView mTv_plot_message;
	private static ListView mLv_plot;
	private static TextView mTv_zsqk_staus;
	private static TextView mTv_zsqk_time;
	private static TextView mTv_trsq_staus;
	private static TextView mTv_trsq_time;
	private static TextView mTv_trfq_staus;
	private static TextView mTv_trfq_time;
	private static TextView mTv_bch_staus;
	private static TextView mTv_bch_time;
	private static TextView tv_plot_messag;
	private View view;
	private ProgressDialog progress;
	private MapView mMapView;
	/** 对地块长按选择添加高亮 */
	private GraphicsLayer graphicsLayer;
	private String mTBMJ;
	private String mWATER;
	private String mZHS;
	private String mBinCH;
	private String mW_TIME;
	private String mZ_TIME;
	private String mB_TIME;
	private String mN_FQ;
	private String mN_TIME;
	private String mPRO_NAME;
	private String mCITY_NAME;
	private String mCOUN_NAME;
	private String mTOWN_NAME;
	private String mVILL_NAME;
	private String mQT;

	/** 勾画面积 */
	private static String sArea;
	private String mResult;
	private String mAs[];
	public static List<DetailMessage> mDetailList = new ArrayList<>();
	private String point = "POLYGON((12933726.556540068 4532559.022374773,12927128.563925495 4517436.189744871,12940499.717100212 4521523.441806995,12933726.556540068 4532559.022374773))";

	public DetailStandardFragment(MapView mMapView, GraphicsLayer graphicsLayer) {
		// TODO Auto-generated constructor stub
		this.mMapView = mMapView;
		this.graphicsLayer = graphicsLayer;
	}

	@Override
	protected int Layout() {
		// TODO Auto-generated method stub
		return R.layout.detail_standard_fragment;
	}

	private Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			// // 查询地块的姓名及手机号码的异步线程
			if (msg.what == 0X456) {
				String mResult = (String) msg.obj;
				mAs = mResult.split(",");
				if (!mResult.equals("anyType{}")) {
					mTv_plot_plant.setText(mAs[0] + "");
					mTv_plot_phone.setText(mAs[2] + "");
				} else {
					mTv_plot_plant.setText("待定");
					mTv_plot_phone.setText("待定");
				}
			}
		};
	};

	@Override
	protected void initView(View mView) {
		// TODO Auto-generated method stub
		mTv_plotdata_area = (TextView) mView
				.findViewById(R.id.tv_plotdata_area);
		mTv_plot_crop = (TextView) mView.findViewById(R.id.tv_plot_crop);
		mTv_plot_area = (TextView) mView.findViewById(R.id.tv_plot_area);
		mTv_plot_plant = (TextView) mView.findViewById(R.id.tv_plot_plant);
		mTv_plot_phone = (TextView) mView.findViewById(R.id.tv_plot_phone);
		mTv_plot_message = (TextView) mView.findViewById(R.id.tv_plot_message);
		mTv_zsqk_staus = (TextView) mView.findViewById(R.id.tv_zsqk_staus);
		mTv_zsqk_time = (TextView) mView.findViewById(R.id.tv_zsqk_time);
		mTv_trsq_staus = (TextView) mView.findViewById(R.id.tv_trsq_staus);
		mTv_trsq_time = (TextView) mView.findViewById(R.id.tv_trsq_time);
		mTv_trfq_staus = (TextView) mView.findViewById(R.id.tv_trfq_staus);
		mTv_trfq_time = (TextView) mView.findViewById(R.id.tv_trfq_time);
		mTv_bch_staus = (TextView) mView.findViewById(R.id.tv_bch_staus);
		tv_plot_messag = (TextView) mView.findViewById(R.id.tv_plot_message);
		mTv_bch_time = (TextView) mView.findViewById(R.id.tv_bch_time);
		mTv_plot_phone.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		Bundle bundle = getArguments();// 从DetailsActivity传过来的Bundle
		float x = bundle.getFloat("x");
		float y = bundle.getFloat("y");

		QueryDetailAsyncTask ayncQuery = new QueryDetailAsyncTask();
		ayncQuery.execute(x, y);
		/***
		 * 调用webservice实现查询用户地块获取电话和姓名调用的方法名称
		 * 
		 * @param point
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {

				WebServiceBean bean = new WebServiceBean();
				bean.setDK_WKT(point);
				SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
						CommonData.QueryDKNAME, CommonData.QueryDKSOAPACTION);
				int count = bodyIn.getPropertyCount();
				if (count > 0) {
					mResult = bodyIn.getProperty(0).toString();
				}
				Message msg = handle.obtainMessage();
				msg.obj = mResult;
				msg.what = 0X456;
				handle.sendMessage(msg);
			}
		}).start();
	}

	class QueryDetailAsyncTask extends AsyncTask<Float, Void, FeatureResult> {
		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(getActivity());
			// 在未查询出结果时显示一个进度条
			progress = ProgressDialog.show(getActivity(), "", "查询任务正在执行 ");
		}

		@Override
		protected FeatureResult doInBackground(Float... queryArray) {
			try {
				// TODO Auto-generated method stub
				if (queryArray == null || queryArray.length <= 1)
					return null;
				float x = queryArray[0];
				float y = queryArray[1];
				Point point = mMapView.toMapPoint(x, y);

				Log.i("TAG", x + "," + y);
				QueryParameters qParameters = new QueryParameters();
				SpatialReference sr = SpatialReference.create(3857);
				qParameters.setGeometry(point);
				qParameters.setOutSpatialReference(sr);
				qParameters.setReturnGeometry(true);
				/**
				 * <string name="TBMJ">TBMJ</string><!-- 地块面积 --> <string
				 * name="WATER">WATER</string><!-- 墒情 --> <string
				 * name="ZHS">ZHS</string><!-- 长势 --> <string
				 * name="BinCH">BinCH</string><!-- 病虫害 --> <string
				 * name="W_TIME">W_TIME</string><!-- 墒情时间 --> <string
				 * name="Z_TIME">Z_TIME</string><!-- 长势时间 --> <string
				 * name="B_TIME">B_TIME</string><!-- 病虫害时间 --> <string
				 * name="N_FQ">N_FQ</string><!-- 土壤肥情 --> <string
				 * name="N_TIME">N_TIME</string><!-- 肥情时间 --> <string
				 * name="PRO_NAME">PRO_NAME</string><!-- 省名称 --> <string
				 * name="CITY_NAME">CITY_NAME</string><!-- 市名称 --> <string
				 * name="COUN_NAME">COUN_NAME</string><!-- 县名称 --> <string
				 * name="TOWN_NAME">TOWN_NAME</string><!-- 乡镇名称 --> <string
				 * name="VILL_NAME">"VILL_NAME"</string><!-- 村名称 -->
				 */
				qParameters.setOutFields(new String[] { "TBMJ", "WATER", "ZHS",
						"BinCH", "W_TIME", "Z_TIME", "B_TIME", "N_FQ",
						"N_TIME", "PRO_NAME", "CITY_NAME", "COUN_NAME",
						"TOWN_NAME", "VILL_NAME", "QT" });
				QueryTask qTask = new QueryTask(CommonData.DIKUAIHIGHtURL3857);
				FeatureResult results = qTask.execute(qParameters);
				Log.i("TAG", results + "");
				return results;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(FeatureResult results) {
			graphicsLayer.removeAll();

			if (results != null) {
				for (Object element : results) {
					if (element instanceof Feature) {
						Feature feature = (Feature) element;
						// turn feature into graphic
						Graphic graphic = new Graphic(feature.getGeometry(),
								feature.getSymbol(), feature.getAttributes());
						// add graphic to layer
						graphicsLayer.addGraphic(graphic);
						mTBMJ = getValue(graphic, "TBMJ", "");
						mWATER = getValue(graphic, "WATER", "");
						mZHS = getValue(graphic, "ZHS", "");
						mBinCH = getValue(graphic, "BinCH", "");
						mW_TIME = getValue(graphic, "W_TIME", "");
						mZ_TIME = getValue(graphic, "Z_TIME", "");
						mB_TIME = getValue(graphic, "B_TIME", "");
						mN_FQ = getValue(graphic, "N_FQ", "");
						mN_TIME = getValue(graphic, "N_TIME", "");
						mPRO_NAME = getValue(graphic, "PRO_NAME", "");
						mCITY_NAME = getValue(graphic, "CITY_NAME", "");
						mCOUN_NAME = getValue(graphic, "COUN_NAME", "");
						mTOWN_NAME = getValue(graphic, "TOWN_NAME", "");
						mVILL_NAME = getValue(graphic, "VILL_NAME", "");
						mQT = getValue(graphic, "QT", "");
						Log.i("TAG", mTBMJ + "/n" + mWATER + "/n" + mZHS + "/n"
								+ mBinCH + "/n" + mW_TIME + "/n" + mZ_TIME
								+ "/n" + mB_TIME + "/n" + mN_FQ + "/n"
								+ mN_TIME + "/n" + mPRO_NAME + "/n"
								+ mCITY_NAME + "/n" + mCOUN_NAME + "/n"
								+ mTOWN_NAME + "/n" + mVILL_NAME + "/n"
								+ "-------");
						// ToastUtils.show(getActivity(), , 1);
						// mDetailList.add(new DetailMessage(mTBMJ, mWATER,
						// mZHS,
						// mBinCH, mW_TIME, mZ_TIME, mB_TIME, mN_FQ,
						// mN_TIME, mPRO_NAME, mCITY_NAME, mCOUN_NAME,
						// mTOWN_NAME, mVILL_NAME));
						// 省市县村
						mTv_plotdata_area.setText(mPRO_NAME + mCITY_NAME
								+ mCOUN_NAME + mTOWN_NAME + mVILL_NAME);
						// 长势情况状态和时间
						mTv_zsqk_staus.setText(mZHS);
						mTv_zsqk_time.setText(mZ_TIME);
						// 土壤墒情状态和时间
						mTv_trsq_staus.setText(mWATER);
						mTv_trsq_time.setText(mW_TIME);
						// 土壤肥情状态和时间
						mTv_trfq_staus.setText(mN_FQ);
						mTv_trfq_time.setText(mN_TIME);
						// 病虫害情况状态和时间
						mTv_bch_staus.setText(mBinCH);
						mTv_bch_time.setText(mB_TIME);
						tv_plot_messag.setText(mQT);
						// 面积
						double area = Double.valueOf(mTBMJ);
						double a = area / 666.67;
						DecimalFormat df = new DecimalFormat("#.#");
						sArea = df.format(a);
						mTv_plot_area.setText(sArea);
					}
				}
			}
			progress.dismiss();
		}
	}

	String getValue(Graphic graphic, String key, String defaultVal) {
		Object obj = graphic.getAttributeValue(key);
		if (obj == null)
			return defaultVal;
		else
			return obj.toString();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		/**
		 * 通过隐式跳转到拨号界面当中
		 * 
		 */
		case R.id.tv_plot_phone:
			if (!mAs[0].equals("")) {
				Intent dialIntent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel:" + mAs[1]));// 跳转到拨号界面，同时传递电话号码
				startActivity(dialIntent);
			}
			break;

		default:
			break;
		}
	}

}
