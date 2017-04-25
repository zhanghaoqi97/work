package com.thsw.work.intentactivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleFillSymbol.STYLE;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.google.gson.Gson;
import com.thsw.work.R;
import com.thsw.work.base.BaseActivity;
import com.thsw.work.bean.PlotDataBean;
import com.thsw.work.bean.PointObject;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.layer.AllPolatLayer;
import com.thsw.work.utils.ArcgisUtils;
import com.thsw.work.utils.GeometryUtils;
import com.thsw.work.utils.InflateUtils;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 图上勾画
 * 
 * @author pc
 * 
 */
public class ImagdotActivity extends BaseActivity implements OnClickListener
		 {
	private TextView mTitle;
	private MapView mMapView;
	private RadioGroup imagdot_rg;
	private RadioButton mRb_sketch;
	private RadioButton mRb_clear;
	private Button mBt_confirm;
	private ImageView mIv_back_include;

	private int[] ids;
	/**
	 * 存放打点集合
	 */
	private ArrayList<Point> pointList;
	/**
	 * 存放点的经纬度集合
	 */
	private ArrayList<PlotDataBean> xyPlotList;

	private ArcGISLocalTiledLayer mFileLayer;
	// 打点图层
	private GraphicsLayer graphicsLayer;

	// 绘画点时用于储存点的变量
	Point startPoint = null;
	@SuppressWarnings("unused")
	private Symbol symbol;
	Polygon polygon = null;

	// 添加Arcgis底图服务
	ArcGISDynamicMapServiceLayer mDKLayer;
	// 打点图标图层
	private GraphicsLayer layer;
	// 所有地块的图层
	private GraphicsLayer mLayer;
	private SimpleFillSymbol fillSymbol;

	private String sArea = "";
	private Point p;
	private ArcGISTiledMapServiceLayer tiledLayer;
	private ArcGISDynamicMapServiceLayer baselayer;
	private ArcGISDynamicMapServiceLayer tileLayer;
	private String spLogin_name;
	private SimpleFillSymbol pressSymbol;
	/** 点击删除图层 */
	private GraphicsLayer removeLayer;
	private RadioButton mRb_delete;
	private RadioButton mRb_dot;

	private String polygonWkt="";
	@Override
	protected int inflater() {
		return R.layout.activity_imagdot;
	}

	@SuppressWarnings("serial")
	@Override
	protected void initData() {
		mTitle.setText("图上勾画");
		pointList = new ArrayList<Point>();
		xyPlotList = new ArrayList<>();
		// 初始化地图监听方法
		mMapView.setOnStatusChangedListener(new ImageOnStatusListener());
		// 初始化arcgis的地图服务
		LoadArcgisMap();

		initPanListener();
	}

	/**
	 * 初始化arcgis的地图服务
	 */
	private void LoadArcgisMap() {
		graphicsLayer = new GraphicsLayer();
		layer = new GraphicsLayer();
		baselayer = new ArcGISDynamicMapServiceLayer(CommonData.BASEARCGISURL);
		mMapView.addLayer(baselayer);
		// 去除水印
		ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
		// 加载Layer数据
		mFileLayer = new ArcGISLocalTiledLayer(
				"file:///mnt/sdcard/BasiImageMap/Layers");
		mMapView.addLayer(mFileLayer);
		if (InflateUtils.fileIsExists() == false) {
			tiledLayer = new ArcGISTiledMapServiceLayer(CommonData.YXARCGISURL);
			mMapView.addLayer(tiledLayer);
		}
		// 添加Arcgis底图服务
		mDKLayer = new ArcGISDynamicMapServiceLayer(CommonData.XZQYARCGISURL);
		mMapView.addLayer(mDKLayer);
		Envelope initextext = new Envelope(12902178.420333067,
				4503651.9433372505, 12965237.481534274, 4561442.030881682);
		mMapView.setExtent(initextext);
		tileLayer = new ArcGISDynamicMapServiceLayer(CommonData.DIKUAIURL3857);
		mMapView.addLayer(tileLayer);
		mMapView.addLayer(graphicsLayer);

		// 点击mMapview的监听方法
		mMapView.setOnSingleTapListener(new ImagOnSingleTapListener());

	}

	/**
	 * 点击MapView获取WebService服务中的数据
	 * 
	 * @author pc
	 * 
	 */
	class ImagOnSingleTapListener implements OnSingleTapListener {



		@Override
		public void onSingleTap(float x, float y) {
			// TODO Auto-generated method stub
			Point pt = mMapView.toMapPoint(x, y);
			Point toponit = mMapView.toScreenPoint(pt);
			float toX = (float) toponit.getX();
			float toY = (float) toponit.getY();
			ids = mLayer.getGraphicIDs(toX, toY,1);
			if (ids.length != 0) {
				// 检索当前 光标点（手指按压位置）的附近的 graphic对象
				Graphic indentifyGf = mLayer.getGraphic(ids[0]);
				Geometry geoIndentify = indentifyGf.getGeometry();
				polygonWkt = GeometryUtils.GeometryToWKT(geoIndentify);
				mLayer.setSelectedGraphics(ids, true);
			}

		}

	}

	/**
	 * 加载完成地图后进行对屏幕中心点进行打点
	 */
	private void initDot() {
		p = mMapView.getCenter();
		pointList.add(p);
		Point wgsPoint = (Point) GeometryEngine.project(p,
				mMapView.getSpatialReference(), SpatialReference.create(3857));
		double x = wgsPoint.getX();
		double y = wgsPoint.getY();
		xyPlotList.add(new PlotDataBean(x + "", y + ""));

		Graphic graphic = new Graphic(p, new SimpleMarkerSymbol(Color.BLACK,
				10, SimpleMarkerSymbol.STYLE.CIRCLE));
		graphicsLayer.addGraphic(graphic);
	}

	/**
	 * 初始化mMapview的滑动监听方法
	 */
	private void initPanListener() {
		// TODO Auto-generated method stub
		mMapView.setOnPanListener(new OnPanListener() {

			@Override
			public void prePointerUp(float arg0, float arg1, float arg2,
					float arg3) {
			}

			@Override
			public void prePointerMove(float arg0, float arg1, float arg2,
					float arg3) {
				if (layer != null) {
					layer.removeAll();
				}
				p = mMapView.getCenter();
				// 创建 graphic对象
				Graphic gp1 = CreateGraphic(p);
				// 添加 Graphics 到图层
				layer.addGraphic(gp1);
			}

			@Override
			public void postPointerUp(float arg0, float arg1, float arg2,
					float arg3) {
			}

			@Override
			public void postPointerMove(float arg0, float arg1, float arg2,
					float arg3) {
			}
		});
	}

	/***
	 * 初始化地图监听方法
	 * 
	 * @author pc
	 * 
	 */
	class ImageOnStatusListener implements OnStatusChangedListener {

		@Override
		public void onStatusChanged(Object arg0, STATUS arg1) {
			// TODO Auto-generated method stub
			if (arg1 == STATUS.INITIALIZED) {
				mLayer = new GraphicsLayer();
				removeLayer = new GraphicsLayer();
				if (layer != null) {
					layer.removeAll();
				}
				p = mMapView.getCenter();
				/**
				 * 设置图片到屏幕中心
				 */
				// 创建 graphic对象
				Graphic gp1 = CreateGraphic(p);
				// 添加 Graphics 到图层
				layer.addGraphic(gp1);
				mMapView.addLayer(layer);
				mMapView.addLayer(mLayer);
				// 加载用户的所有地块
				AllPolatLayer.LoginPolaAll(ImagdotActivity.this, mMapView,
						mLayer);
			}

		}

	}

	/*
	 * 创建一个Graphic ， 参数geometry是屏幕坐标位置，map是附加的属性参数
	 */
	private Graphic CreateGraphic(Point geometry) {
		Drawable image = ImagdotActivity.this.getBaseContext().getResources()
				.getDrawable(R.drawable.pop);
		PictureMarkerSymbol symbol = new PictureMarkerSymbol(image);

		// 构建graphic
		// Graphic g = new Graphic(geometry, symbol);
		Graphic g = new Graphic(geometry, symbol, null);
		return g;
	}

	@Override
	protected void initView() {
		mTitle = (TextView) findViewById(R.id.tv_title_include);
		mMapView = (MapView) findViewById(R.id.map);
		imagdot_rg = (RadioGroup) findViewById(R.id.imagdot_rg);
		mRb_dot = (RadioButton) findViewById(R.id.rb_dot);
		mRb_clear = (RadioButton) findViewById(R.id.rb_clear);
		mRb_sketch = (RadioButton) findViewById(R.id.rb_sketch);
		mRb_delete = (RadioButton) findViewById(R.id.rb_delete);
		mBt_confirm = (Button) findViewById(R.id.bt_confirm);
		mIv_back_include = (ImageView) findViewById(R.id.iv_back_include);
		mIv_back_include.setOnClickListener(this);
		mRb_dot.setOnClickListener(this);
		mRb_clear.setOnClickListener(this);
		mRb_sketch.setOnClickListener(this);
		mRb_delete.setOnClickListener(this);
		mBt_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * 打点
		 */
		case R.id.rb_dot:
			try {
				initDot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		/*
		 * 勾画
		 */
		case R.id.rb_sketch:
			if (pointList != null && pointList.size() > 2) {
				fillSymbol = new SimpleFillSymbol(Color.BLUE,
						SimpleFillSymbol.STYLE.SOLID);
				fillSymbol.setAlpha(100);
				
				polygon = new Polygon();
				Line line = new Line();
				// 多点连接
				for (int i = 0; i < pointList.size(); i++) {
					if (i == pointList.size() - 1) {
						break;// 推出循环，否则出现越界异常
					}
					line.setStart(pointList.get(i));
					line.setEnd(pointList.get(i + 1));
					polygon.addSegment(line, false);
					Graphic gGraphic = new Graphic(polygon, fillSymbol);
					graphicsLayer.addGraphic(gGraphic);

				}
				// 起点终点相连
				line.setStart(pointList.get(0));
				line.setEnd(pointList.get(pointList.size() - 1));
				polygon.addSegment(line, false);
				Graphic gGraphic = new Graphic(polygon, fillSymbol);
				graphicsLayer.addGraphic(gGraphic);

				Polygon tempPolygon = (Polygon) GeometryEngine.project(polygon,
						SpatialReference.create(3857),
						SpatialReference.create(3857));
				sArea = getAreaString(tempPolygon.calculateArea2D());
				PictureMarkerSymbol textSymbol = ArcgisUtils
						.createTextPicMarkerSymbol(sArea + "亩", 16,
								Color.BLACK, Color.RED,
								Color.argb(100, 255, 255, 255));// 红框、黑字
				Graphic textGraphic = new Graphic(p, textSymbol);
				graphicsLayer.addGraphic(textGraphic);
				pointList.clear();
			} else {
				ToastUtils.show(ImagdotActivity.this, "点数少于三个,无法成面", 0);
			}
			break;
		/*
		 * 清楚
		 */
		case R.id.rb_clear:
			pointList.clear();
			graphicsLayer.removeAll();
			removeLayer.removeAll();
			break;
		/*
		 * 删除
		 */
		case R.id.rb_delete:
			DialogMessage();

			break;
		/*
		 * 确定
		 */
		case R.id.bt_confirm:
				String spLogin_name = (String) SpUtils.get(
						ImagdotActivity.this, "login_name", "");
				String value = getRemoteInfo(spLogin_name, xyPlotList);
 				if(value!=null||!polygonWkt.equals("")){
					Intent intent = new Intent();
					intent.putExtra("area", sArea);
					intent.putExtra("value", value);
					intent.putExtra("polygonWkt", polygonWkt);
					// 专门用于向上一个活动返回数据
					// 第一个参数用于向上一个活动返回结果码，一般只使用RESULT_OK或RESULT_CANCELED这两个值
					// 第二个参数则是把带有数据的Intent传递回去
					setResult(RESULT_OK, intent);
					// 调用了finish()方法来销毁当前活动
					finish();
				}else{
					ToastUtils.show(ImagdotActivity.this, "请在地图进行编辑后 进行提交", Toast.LENGTH_LONG);
				}
			break;
		case R.id.iv_back_include:
			Intent back_intent = new Intent(ImagdotActivity.this, PolaActivity.class);
			startActivity(back_intent);
			finish();
			break;
		}
	}

	/**
	 * 删除选择是否删除对话框
	 */
	private void DialogMessage() {
		AlertDialog.Builder alertDialog = new Builder(ImagdotActivity.this);
		alertDialog.setMessage("对此地块进行删除！！");
		// 设置对话框的标题
		alertDialog.setTitle("确定删除？");
		alertDialog.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						mLayer.removeGraphic(ids[0]);
					}
				});

		alertDialog.setNegativeButton("取消", null);
		// 显示对话框
		alertDialog.show();
	}
	/**
	 * 对集合中的点进行截取
	 * @param string
	 * @param xyPlotList
	 * @return
	 */
	private String getRemoteInfo(String string, ArrayList<PlotDataBean> xyPlotList) {
		String value = "";
		if(xyPlotList.size()>2){
			for (int i = 0; i < xyPlotList.size(); i++) {
				value = value + xyPlotList.get(i).getLongitude() + " "
						+ xyPlotList.get(i).getLatitude() + ",";
			}
			value = value + xyPlotList.get(0).getLongitude() + " "
					+ xyPlotList.get(0).getLatitude();

			value = "polygon((" + value + "))";
			return value;
		}
		return null;
	}

	/**
	 * 计算选中地块的面积
	 * 
	 * @param dValue
	 * @return
	 */
	private String getAreaString(double dValue) {
		long area = Math.abs(Math.round(dValue));
		String sArea = "";
		// 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
		if (area >= 1000000) {
			double dArea = area / 666.67;
			DecimalFormat df = new DecimalFormat("#.##");
			sArea = df.format(dArea);
		} else {
			double dArea = area / 666.67;
			DecimalFormat df = new DecimalFormat("#.##");
			sArea = df.format(dArea);
		}
		return sArea;
	}
}
