package com.thsw.work.intentactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleFillSymbol.STYLE;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.Symbol;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.thsw.work.R;
import com.thsw.work.R.color;
import com.thsw.work.R.id;
import com.thsw.work.R.layout;
import com.thsw.work.R.string;
import com.thsw.work.bean.PointObject;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.common.PopupWindowUtil;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.fragment.DetailDroughtFragment;
import com.thsw.work.fragment.DetailFertilizerFragment;
import com.thsw.work.fragment.DetailPlantFragment;
import com.thsw.work.fragment.DetailSiteFragment;
import com.thsw.work.fragment.DetailStandardFragment;
import com.thsw.work.layer.AllPolatLayer;
import com.thsw.work.queryastc.QueryDetailXZQY;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.InflateUtils;
import com.thsw.work.utils.NetWorkUtil;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;
import com.thsw.work.voice.IatSettings;
import com.thsw.work.voice.JsonParser;

/***
 * 地块详情
 * 
 * @author pc
 * 
 */
public class DetailsActivity extends FragmentActivity implements
		OnClickListener {
	private static final String TAG = "DetilsActivity";
	private static final int RESULT_MESSAGE = 1234;
	/** 初始化Arcgis底图 */
	MapView mMapView = null;
	/** 添加Arcgis底图服务 */
	ArcGISDynamicMapServiceLayer tileLayer;

	/** arcgis地图服务的切换 */
	private ImageView mMain_transition;
	/** 离线影像地图服务 */
	private ArcGISLocalTiledLayer layer;
	/** scrollView滑动监听 */
	private ScrollView sv_details;
	/** 长势地图服务 */
	private ArcGISDynamicMapServiceLayer mZsLayer;
	/** 墒情地图服务 */
	private ArcGISDynamicMapServiceLayer mSqLayer;
	/** 离线影像地图服务 */
	private ArcGISTiledMapServiceLayer tiledLayer;
	/** 隐藏显示底部RadioButton控件 */
	private LinearLayout mLinearLayout;
	/** 搜索框 */
	private ImageView search;
	/***
	 * 底部RadioButton按钮
	 */
	private RadioButton statistics_city;
	private RadioButton mTab_home;
	private RadioButton mTab_mine;
	private RadioButton mTab_shop;
	private ImageView mIv_details_regain;
	private String mLogin_pwd;
	private String mLogin_name;
	/** 长势图片介绍 */
	private ImageView iv_details_zs;
	/** 墒情图片介绍 */
	private ImageView iv_details_sq;
	private String mResult;
	private ArcGISDynamicMapServiceLayer baselayer;
	/** 搜索显示高亮 */
	private GraphicsLayer searchGraphicsLayer;
	/** 对地块长按选择添加高亮 */
	private GraphicsLayer graphicsLayer;
	/** 定位图层 */
	private GraphicsLayer gLayerGps;
	/** 添加面积框 **/
	private GraphicsLayer labelGraphicsLayer;
	/** 语音输入框 */
	private EditText searchkey;
	/** 用HashMap存储听写结果 */
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	/*** 语音听写UI */
	private RecognizerDialog mIatDialog;
	/** 语音听写对象 */
	private SpeechRecognizer mIat;

	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	int ret = 0; // 函数调用返回值
	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	private GraphicsLayer graphicsLayere;
	private String[] queryArrays;

	private String extra = "false";
	/** 统计面积 */
	private String area = "";
	/** 统计行政代码 */
	private String xzdm;
	/** 所有地块Layer */
	private GraphicsLayer mLayer;
	private String twoBodyin;
	/*** 行政区服务 */
	private ArcGISDynamicMapServiceLayer xzqLayer;

	private Dialog dialog;

	/** 定位坐标的标识符号 */
	private Symbol courseSymbol;
	/** 搜索半径值 */
	final static double SEARCH_RADIUS = 5;
	/** 模式切换 */
	private static int SWITCHSTATE = 0;
	Boolean up = false;// 默认false不刷新

	public static boolean isForeground = false;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private Handler handler = new Handler() {

		// 县 131127 杜桥镇, 131127106 东张庄,131127007062 衡水市,1311
		public void handleMessage(Message msg) {
			// 查询地块的行政区域异步线程
			if (msg.what == 0X123) {
				String twoBodyin = (String) msg.obj;
				if (twoBodyin.equals("anyType{}")) {
					dialog.dismiss();
					ToastUtils.show(DetailsActivity.this, "没有搜索到指定区域!",
							Toast.LENGTH_LONG);
				} else {
					String[] split = twoBodyin.split(",");
					// String targetLayer = CommonData.SEARCHCOUNTY;
					// queryArrays = new String[] { targetLayer,
					// "XZQDM='131127'" };
					String code = split[1].trim();
					if (!twoBodyin.equals("")) {
						area = "";
						QueryXZPolygon(code);
						dialog.dismiss();
					} else if (twoBodyin.equals(R.string.fail)) {
						dialog.dismiss();
						ToastUtils.show(DetailsActivity.this,
								R.string.datebase_fail, Toast.LENGTH_LONG);
					}
				}

			}

		}

	};
	private ImageView mBt_Locationorigin;
	private LocationManager locMag;
	private Point wgspoint;

	private Location loc;
	private Point mapPoint;
	private PictureMarkerSymbol locationSymbol;

	/** 抽取查询行政边界显示高亮方法 **/
	private void QueryXZPolygon(String code) {
		if (code.length() == 4) {
			String targetLayer = CommonData.SEARCHCITY;
			queryArrays = new String[] { targetLayer,
					"XZQDM=" + "'" + code + "'" };
		} else if (code.trim().length() == 6) {
			String targetLayer = CommonData.SEARCHCOUNTY;
			queryArrays = new String[] { targetLayer,
					"XZQDM=" + "'" + code.trim() + "'" };
		} else if (code.trim().length() == 9) {
			String targetLayer = CommonData.SEARCHVILLAGES;
			queryArrays = new String[] { targetLayer,
					"XZQDM=" + "'" + code + "'" };
		} else if (code.trim().length() == 12) {
			String targetLayer = CommonData.SEARCHVILLAGE;
			queryArrays = new String[] { targetLayer,
					"XZQDM=" + "'" + code + "'" };
		}
		QueryDetailXZQY ayncQuery = new QueryDetailXZQY(DetailsActivity.this,
				mMapView, searchGraphicsLayer, labelGraphicsLayer, area,
				tileLayer);
		ayncQuery.execute(queryArrays);
		dialog.dismiss();
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_details);
		// 去除水印
		ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
		initView();
		initData();
	}

	protected void initData() {
		if (!NetWorkUtil.isNetworkAvailable(DetailsActivity.this)) {
			HttpTest(DetailsActivity.this);
		} else {
			// 初始化识别无UI识别对象
			// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
			mIat = SpeechRecognizer.createRecognizer(DetailsActivity.this,
					mInitListener);
			// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
			// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
			mIatDialog = new RecognizerDialog(DetailsActivity.this,
					mInitListener);
			mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
					Activity.MODE_PRIVATE);
			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
			// mInstaller = new ApkInstaller(DetailsActivity.this);
			mLogin_pwd = (String) SpUtils.get(DetailsActivity.this,
					"login_pwd", "");
			mMapView.setEsriLogoVisible(false);

			baselayer = new ArcGISDynamicMapServiceLayer(
					CommonData.BASEARCGISURL);
			mMapView.addLayer(baselayer);

			// 加载Layer数据
			layer = new ArcGISLocalTiledLayer(
					"file:///mnt/sdcard/BasiImageMap/Layers");
			mMapView.addLayer(layer);

			if (InflateUtils.fileIsExists() == false) {
				tiledLayer = new ArcGISTiledMapServiceLayer(
						CommonData.YXARCGISURL);
				mMapView.addLayer(tiledLayer);
			}
			xzqLayer = new ArcGISDynamicMapServiceLayer(
					CommonData.XZQYARCGISURL);
			mMapView.addLayer(xzqLayer);
			// 添加Arcgis底图服务
			// liLayer = new
			// ArcGISDynamicMapServiceLayer(CommonData.YINGXIANGURL4326);
			// mMapView.addLayer(liLayer);
			Envelope initextext = new Envelope(12902178.420333067,
					4503651.9433372505, 12965237.481534274, 4561442.030881682);
			mMapView.setExtent(initextext);
			// 墒情
			mSqLayer = new ArcGISDynamicMapServiceLayer(CommonData.DIKUAISQ3857);
			mMapView.addLayer(mSqLayer);
			mSqLayer.setVisible(false);

			// 长势
			mZsLayer = new ArcGISDynamicMapServiceLayer(CommonData.DIKUAIZS3857);
			mMapView.addLayer(mZsLayer);
			mZsLayer.setVisible(false);

			tileLayer = new ArcGISDynamicMapServiceLayer(
					CommonData.DIKUAIURL3857);
			mMapView.addLayer(tileLayer);
			mLayer = new GraphicsLayer();

			PolaSelect();
			MapRollEvent();
		}

	}

	/**
	 * 检测网络是否存在
	 */
	public static void HttpTest(final Activity mActivity) {
		AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
		builders.setTitle("抱歉，网络连接失败，是否进行网络设置？");
		builders.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 进入无线网络配置界面
				// mActivity.startActivity(new Intent(
				// Settings.ACTION_WIRELESS_SETTINGS));
				mActivity.startActivity(new Intent(
						Settings.ACTION_WIFI_SETTINGS));
				// //进入手机中的wifi网络设置界面
			}
		});
		builders.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 关闭当前activity
				// mActivity.finish();
			}
		});
		builders.show();
	}

	

	/**
	 * 初始化地图的监听方法
	 */
	private void PolaSelect() {
		spLogin_name = (String) SpUtils.get(DetailsActivity.this, "login_name",
				"");
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

			@Override
			public void onStatusChanged(Object source, STATUS status) {
				// 判断地图是否初始化
				if (status.equals(STATUS.INITIALIZED)) {
					dialog = DialogUtil.createLoadingDialog(
							DetailsActivity.this, "加载中...", true, 0);
					dialog.show();
					graphicsLayer = new GraphicsLayer();
					searchGraphicsLayer = new GraphicsLayer();
					labelGraphicsLayer = new GraphicsLayer();

					// 点击地块时进行对地块进行对面高亮
					SimpleRenderer sr = new SimpleRenderer(
							new SimpleFillSymbol(Color.BLUE));
					graphicsLayer.setRenderer(sr);
					mMapView.addLayer(graphicsLayer);

					// 搜索时对行政区域进行线性高亮
					SimpleLineSymbol outLine = new SimpleLineSymbol(Color.RED,
							5.0f);
					SimpleFillSymbol simpleFill = new SimpleFillSymbol(
							Color.TRANSPARENT, STYLE.SOLID);
					simpleFill.setOutline(outLine);
					SimpleRenderer sr1 = new SimpleRenderer(simpleFill);
					searchGraphicsLayer.setRenderer(sr1);
					mMapView.addLayer(searchGraphicsLayer);
					mMapView.addLayer(labelGraphicsLayer);

					gLayerGps = new GraphicsLayer();
					mMapView.addLayer(gLayerGps);
					androidLocation();
				}
				// 判断地图是否加载完成
				if (status.equals(STATUS.LAYER_LOADED)) {
					dialog.dismiss();
					if (graphicsLayer != null) {
						graphicsLayer.removeAll();
					}
					// 添加长按地图事件
					mMapView.setOnLongPressListener(new MyOnLongPressListener());
				}
			}

		});
//		AllPolatLayer.LoginPolaAll(this, mMapView, mLayer);
	}

	/**
	 * 触摸地图回调接口
	 */
	private void MapRollEvent() {
		// TODO Auto-generated method stub
		/**
		 * 当用户触摸地图时回调函数
		 * 
		 * @param event
		 *            触摸事件
		 */
		mMapView.setOnPanListener(new OnPanListener() {

			@Override
			public void prePointerUp(float arg0, float arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void prePointerMove(float arg0, float arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postPointerUp(float arg0, float arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postPointerMove(float arg0, float arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub
				mBt_Locationorigin.setImageResource(R.drawable.location23);
			}
		});

	}

	/***
	 * arcgis图层的长按点击事件
	 * 
	 * @author pc
	 * 
	 */
	@SuppressWarnings("serial")
	class MyOnLongPressListener implements OnLongPressListener {

		@Override
		public boolean onLongPress(float x, float y) {
			labelGraphicsLayer.removeAll();
			mLinearLayout.setVisibility(View.GONE);
			ll_details_regain.setVisibility(View.VISIBLE);
			if (SWITCHSTATE == 0) {
				DetailStandardFragment standardFragment = new DetailStandardFragment(
						mMapView, graphicsLayer);
				FragmentManager fm = getSupportFragmentManager();
				Bundle bundle = new Bundle();
				bundle.putFloat("x", x);
				bundle.putFloat("y", y);
				standardFragment.setArguments(bundle);
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, standardFragment);
				ft.commit();

			} else if (SWITCHSTATE == 1) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, new DetailSiteFragment());
				ft.commit();
			} else if (SWITCHSTATE == 2) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, new DetailDroughtFragment());
				ft.commit();
			} else if (SWITCHSTATE == 3) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, new DetailPlantFragment());
				ft.commit();
			} else if (SWITCHSTATE == 4) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, new DetailFertilizerFragment());
				ft.commit();
			} else if (SWITCHSTATE == 5) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment, new DetailPlantFragment());
				ft.commit();
			}

			return true;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		/**
		 * Arcgis搜索的监听事件
		 */
		case R.id.search:

			dialog = DialogUtil.createLoadingDialog(DetailsActivity.this,
					"加载中...", true, 0);
			dialog.show();
			/**
			 * 根据名称获取行政代码调用webservice服务搜索行政代码
			 */
			new Thread(new Runnable() {
				@Override
				public void run() {
					String xzmc = searchkey.getText().toString().trim();
					// TODO Auto-generated method stub
					WebServiceBean bean = new WebServiceBean();
					bean.setXZMC(xzmc);
					SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
							CommonData.SEARCHANAME, CommonData.SEARCHAACTION);
					twoBodyin = bodyIn.getProperty(0).toString();
					Message msg = handler.obtainMessage();
					msg.obj = twoBodyin;
					msg.what = 0X123;
					handler.sendMessage(msg);
				}
			}).start();
			break;
		/**
		 * 统计分析
		 */
		case R.id.statistics_city:

			startActivityForResult(new Intent(DetailsActivity.this,
					StatisticsActivity.class), RESULT_MESSAGE);
			break;
		/**
		 * 农情主题的点击事件
		 */
		case R.id.details_transition:
			// 初始化添加切换对象的集合
			items = new ArrayList<>();
			items.add("标准");
			items.add("长势");
			items.add("墒情");
			items.add("肥料");
			items.add("病虫害");
			final PopupWindowUtil popupWindow = new PopupWindowUtil(
					DetailsActivity.this, items);
			popupWindow
					.setItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String str = items.get(position);
							if (str.equals(CommonData.STANDARD_TEXT)) {
								iv_details_zs.setVisibility(View.GONE);
								iv_details_sq.setVisibility(View.GONE);
								mSqLayer.setVisible(false);
								mZsLayer.setVisible(false);
								SWITCHSTATE = 0;// 标准
							} else if (str.equals(CommonData.ZHANGSHI_TEXT)) {
								mSqLayer.setVisible(false);
								mZsLayer.setVisible(true);
								iv_details_zs.setVisibility(View.VISIBLE);
								iv_details_sq.setVisibility(View.GONE);
								SWITCHSTATE = 1;// 长势
							} else if (str.equals(CommonData.SHANGQING_TEXT)) {
								mSqLayer.setVisible(true);
								mZsLayer.setVisible(false);
								iv_details_zs.setVisibility(View.GONE);
								iv_details_sq.setVisibility(View.VISIBLE);
								SWITCHSTATE = 2;// 墒情
							}
							popupWindow.dismiss();
						}
					});
			// 根据后面的数据调节窗口的宽度
			popupWindow.show(view, 4);
			break;

		/**
		 * 地块认领
		 */
		case R.id.tab_home:
			mLogin_name = (String) SpUtils.get(DetailsActivity.this,
					"login_name", "");
			if (!mLogin_name.equals("")) {
				Intent home_intent = new Intent(DetailsActivity.this,
						PolaActivity.class);
				startActivity(home_intent);
			} else {
				Intent personintent = new Intent(DetailsActivity.this,
						PersonalActivity.class);
				startActivity(personintent);
			}
			break;
		/**
		 * 信息上报
		 */
		case R.id.tab_mine:
			mLogin_name = (String) SpUtils.get(DetailsActivity.this,
					"login_name", "");
			if (!mLogin_name.equals("")) {
				Intent message_intent = new Intent(DetailsActivity.this,
						MessageActivity.class);
				startActivity(message_intent);

			} else {
				Intent personintent = new Intent(DetailsActivity.this,
						PersonalActivity.class);
				startActivity(personintent);
			}
			break;
		/**
		 * 个人中心
		 */
		case R.id.tab_shop:

			// if (mLogin_name.equals("")) {
			Intent person_intent = new Intent(DetailsActivity.this,
					PersonalActivity.class);
			startActivity(person_intent);
			// }
			break;
		/**
		 * 点击回到原点坐标
		 */
		case R.id.bt_locationorigin:
			// 改变成原来login
			mBt_Locationorigin.setImageResource(R.drawable.location1);
			// initLocation();
			androidLocation();

			break;
		/**
		 * 收起详情信息
		 */
		case R.id.iv_details_regain:
			ll_details_regain.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.VISIBLE);
			break;

		// 开始听写
		// 如何判断一次听写结束：OnResult isLast=true 或者 onError
		case R.id.iv_recognize:
			// 移动数据分析，收集开始听写事件
			FlowerCollector.onEvent(DetailsActivity.this, "iat_recognize");
			searchkey.setText(null);// 清空显示内容
			mIatResults.clear();
			// 设置参数
			setParam();
			boolean isShowDialog = mSharedPreferences.getBoolean(
					getString(R.string.pref_key_iat_show), true);
			if (isShowDialog) {
				// 显示听写对话框
				mIatDialog.setListener(mRecognizerDialogListener);
				mIatDialog.show();
				showTip(getString(R.string.text_begin));
			} else {
				// 不显示听写对话框
				ret = mIat.startListening(mRecognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					showTip("听写失败,再试一次..");
				} else {
					showTip(getString(R.string.text_begin));
				}
			}
		}
	}

	private void androidLocation() {
		// TODO Auto-generated method stub
		locationSymbol = new PictureMarkerSymbol(this.getResources()
				.getDrawable(R.drawable.pop));
		locMag = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// TODO Auto-generated method stub
		final List<String> providers = locMag.getProviders(true);
		for (String provider : providers) {
			loc = locMag.getLastKnownLocation(provider);
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					markLocation(location);
				}

				public void onProviderDisabled(String arg0) {
				}

				public void onProviderEnabled(String arg0) {
				}

				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				}
			};
			locMag.requestLocationUpdates(provider, 2000, 1000,
					locationListener);
			if (loc != null) {
				markLocation(loc);
			}
		}

	}

	private void markLocation(Location location) {
		gLayerGps.removeAll();
		double locx = location.getLongitude() + 0.006;// 经度
		double locy = location.getLatitude() + 0.001;// 纬度
		wgspoint = new Point(locx, locy);
		// 定位到所在位置
		mapPoint = (Point) GeometryEngine.project(wgspoint,
				SpatialReference.create(4326), SpatialReference.create(3857));
		Graphic graphic = new Graphic(mapPoint, locationSymbol);
		gLayerGps.addGraphic(graphic);
		// mMapView.centerAt(mapPoint, true);
		// mMapView.setScale(100);
		// mMapView.setMaxResolution(300);
		Unit mapUnit = mMapView.getSpatialReference().getUnit();
		double zoomWidth = Unit.convertUnits(SEARCH_RADIUS,
				Unit.create(LinearUnit.Code.MILE_US), mapUnit);
		Envelope zoomExtent = new Envelope(mapPoint, zoomWidth, zoomWidth);
		mMapView.setExtent(zoomExtent);

	}

	protected void initView() {
		// TODO Auto-generated method stub
		// 初始化布局文件
		mMapView = (MapView) findViewById(R.id.map);
		mMain_transition = (ImageView) findViewById(R.id.details_transition);
		sv_details = (ScrollView) findViewById(R.id.sv_details);
		ll_details_regain = (LinearLayout) findViewById(R.id.ll_details_regain);
		mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout2);
		mIv_details_regain = (ImageView) findViewById(R.id.iv_details_regain);
		mTab_home = (RadioButton) findViewById(R.id.tab_home);
		mTab_mine = (RadioButton) findViewById(R.id.tab_mine);
		mTab_shop = (RadioButton) findViewById(R.id.tab_shop);
		statistics_city = (RadioButton) findViewById(R.id.statistics_city);
		iv_details_zs = (ImageView) findViewById(R.id.iv_details_zs);
		iv_details_sq = (ImageView) findViewById(R.id.iv_details_sq);
		searchkey = (EditText) findViewById(R.id.searchkey);
		mBt_Locationorigin = (ImageView) findViewById(R.id.bt_locationorigin);

		search = (ImageView) findViewById(R.id.search);
		findViewById(R.id.iv_recognize)
				.setOnClickListener(DetailsActivity.this);
		search.setOnClickListener(this);
		mMain_transition.setOnClickListener(this);
		mBt_Locationorigin.setOnClickListener(this);
		mIv_details_regain.setOnClickListener(this);
		statistics_city.setOnClickListener(this);
		sv_details.setOnClickListener(this);
		ll_details_regain.setOnClickListener(this);
		mTab_home.setOnClickListener(this);
		mTab_mine.setOnClickListener(this);
		mTab_shop.setOnClickListener(this);
	}

	/****** 科大 *******/
	/**
	 * 科大讯飞初始化监听器
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				// showTip("初始化失败，错误码：" + code);
			}
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		searchkey.setText(resultBuffer.toString());
		searchkey.setSelection(searchkey.length());
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "0"));

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/iat.wav");
	}

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	/** 自定义吐司 */
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据：" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	private LinearLayout ll_details_regain;
	private long firstTime = 0;
	private List<String> items;
	private String spLogin_name;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于800毫秒，则不退出
				Toast.makeText(DetailsActivity.this, "再按一次退出程序...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				System.exit(0);// 否则退出程序
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/**** 结尾 *****/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == RESULT_MESSAGE) {
				area = data.getStringExtra("finalResult");
				xzdm = data.getStringExtra("xzdm");
				QueryXZPolygon(xzdm);
			}

		}
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapView = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
		up = true;// 不可见的时候将刷新开启
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.unpause();
		// 判断是否刷新
		if (up) {
			Layer[] layers = mMapView.getLayers();
			if(layers.length<=0){
				initData();
			}
			up = false;// 刷新一次即可，不需要一直刷新
		}
	}
}
