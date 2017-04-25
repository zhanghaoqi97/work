package com.thsw.work.intentactivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.thsw.work.R;
import com.thsw.work.base.BaseActivity;
import com.thsw.work.bean.PlotDataBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.ArcgisUtils;
import com.thsw.work.utils.InflateUtils;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;

/**
 * GPS打点界面
 * 
 * @author pc
 * 
 */
public class GpsdotActivity extends BaseActivity implements OnClickListener {
	private TextView mTitle;
	private MapView mMapView;
	private ImageView mIv_dot;
	private ImageView mIv_sketch;
	private ImageView mIv_clear;
	private Button mBt_confirm;
	private ImageView mIv_back_include;
	/** 搜索半径值 */
	final static double SEARCH_RADIUS = 5;
	Location loc;
	LocationManager locMag;
	/** 存放点的经纬度集合 */
	private ArrayList<PlotDataBean> xyPlotList;
	/** 存放打点集合 */
	private ArrayList<Point> pointList;
	/** 位置图层 */
	private GraphicsLayer gLayerPos;
	/** 构点图层 */
	private GraphicsLayer layer;
	private PictureMarkerSymbol locationSymbol;
	private Point wgspoint;
	/** 经纬度转换后的坐标点 */
	private Point mapPoint;
	/** 绘画点时用于储存点的变量 */
	Point startPoint = null;
	/** 打点图层 */
	private GraphicsLayer graphicsLayer;
	private ArcGISDynamicMapServiceLayer baselayer;
	private ArcGISLocalTiledLayer mFileLayer;
	private ArcGISTiledMapServiceLayer tiledLayer;
	private ArcGISDynamicMapServiceLayer mDKLayer;
	private ArcGISDynamicMapServiceLayer tileLayer;
	private SimpleFillSymbol fillSymbol;
	private Polygon polygon;
	/** 存放面积值 */
	private String sArea = null;

	String value = "";

	@Override
	protected int inflater() {
		// TODO Auto-generated method stub
		return R.layout.activity_gpsdot;
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// 去除水印
		ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
		xyPlotList = new ArrayList<>();
		pointList = new ArrayList<>();
		mTitle.setText("GPS打点");
		// 初始化地图监听方法
		mMapView.setOnStatusChangedListener(new ImageOnStatusListener());
		LoadArcgisMap();
		// 初始化定位方法
		InitArcgisLocation();

	}

	/**
	 * 初始化arcgis的地图服务
	 */
	private void LoadArcgisMap() {
		// TODO Auto-generated method stub
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
	}

	/**
	 * 初始化定位方法
	 */
	private void InitArcgisLocation() {
		// TODO Auto-generated method stub
		locationSymbol = new PictureMarkerSymbol(this.getResources()
				.getDrawable(R.drawable.pop));
		locMag = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// 地图监听事件
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
			locMag.requestLocationUpdates(provider, 50, 10, locationListener);
		}
	}

	/***
	 * 通过定位获取信息参数，设置定位格式
	 * 
	 * @param location
	 */
	private void markLocation(Location location) {
		gLayerPos.removeAll();
		double locx = location.getLongitude() + 0.006;// 经度
		double locy = location.getLatitude() + 0.001;// 纬度
		wgspoint = new Point(locx, locy);
		// 定位到所在位置
		mapPoint = (Point) GeometryEngine.project(wgspoint,
				SpatialReference.create(4326), mMapView.getSpatialReference());
		Graphic graphic = new Graphic(mapPoint, locationSymbol);
		gLayerPos.addGraphic(graphic);
		mMapView.centerAt(mapPoint, true);
		// mMapView.setScale(100);
		// mMapView.setMaxResolution(300);
		Unit mapUnit = mMapView.getSpatialReference().getUnit();
		double zoomWidth = Unit.convertUnits(SEARCH_RADIUS,
				Unit.create(LinearUnit.Code.MILE_US), mapUnit);
		Envelope zoomExtent = new Envelope(mapPoint, zoomWidth, zoomWidth);
		mMapView.setExtent(zoomExtent);
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
				// 位置图层
				gLayerPos = new GraphicsLayer();
				mMapView.addLayer(gLayerPos);
				// 构点图层
				layer = new GraphicsLayer();
				mMapView.addLayer(layer);
			}

		}

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mTitle = (TextView) findViewById(R.id.tv_title_include);
		mMapView = (MapView) findViewById(R.id.map);
		mIv_dot = (ImageView) findViewById(R.id.iv_dot);
		mIv_clear = (ImageView) findViewById(R.id.iv_clear);
		mIv_sketch = (ImageView) findViewById(R.id.iv_sketch);
		mBt_confirm = (Button) findViewById(R.id.bt_confirm);
		mIv_back_include = (ImageView) findViewById(R.id.iv_back_include);
		mIv_back_include.setOnClickListener(this);
		mIv_dot.setOnClickListener(this);
		mIv_sketch.setOnClickListener(this);
		mIv_clear.setOnClickListener(this);
		mBt_confirm.setOnClickListener(this);
	}

	/***
	 * 定位后进行打点方法
	 */
	private void initDot() {
		// TODO Auto-generated method stub
		if (mapPoint != null) {
			double x = mapPoint.getX();
			double y = mapPoint.getY();
			pointList.add(mapPoint);
			xyPlotList.add(new PlotDataBean(x + "", y + ""));
			// 获取中心点，在构点图层上打点
			Graphic graphic = new Graphic(mapPoint, new SimpleMarkerSymbol(
					Color.BLACK, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
			layer.addGraphic(graphic);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*
		 * 打点
		 */
		case R.id.iv_dot:
			try {
				initDot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		/*
		 * 勾画
		 */
		case R.id.iv_sketch:
			if (pointList != null && pointList.size() > 2) {
				fillSymbol = new SimpleFillSymbol(Color.BLUE,
						SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
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
				Graphic textGraphic = new Graphic(mapPoint, textSymbol);
				graphicsLayer.addGraphic(textGraphic);
			} else {
				ToastUtils.show(GpsdotActivity.this, "点数少于三个,无法成面", 0);
			}
			break;
		/*
		 * 清楚
		 */
		case R.id.iv_clear:
			pointList.clear();
			graphicsLayer.removeAll();
			break;
		/*
		 * 确定
		 */
		case R.id.bt_confirm:
			if (sArea != null) {
				String spLogin_name = (String) SpUtils.get(GpsdotActivity.this,
						"login_name", "");
				getRemoteInfo(spLogin_name, xyPlotList);
				Intent intent = new Intent();
				intent.putExtra("area", sArea);
				intent.putExtra("value", value);
				// 专门用于向上一个活动返回数据
				// 第一个参数用于向上一个活动返回结果码，一般只使用RESULT_OK或RESULT_CANCELED这两个值
				// 第二个参数则是把带有数据的Intent传递回去
				setResult(RESULT_OK, intent);
				// 调用了finish()方法来销毁当前活动
				finish();
			} else {
				ToastUtils.show(GpsdotActivity.this, "请对地图进行勾画",
						Toast.LENGTH_LONG);
			}
			break;
		case R.id.iv_back_include:
			Intent intent = new Intent(GpsdotActivity.this, PolaActivity.class);
			startActivity(intent);
			break;
		}

	}

	private void getRemoteInfo(String string, ArrayList<PlotDataBean> xyPlotList) {
		for (int i = 0; i < xyPlotList.size(); i++) {
			value = value + xyPlotList.get(i).getLongitude() + " "
					+ xyPlotList.get(i).getLatitude() + ",";
		}
		value = value + xyPlotList.get(0).getLongitude() + " "
				+ xyPlotList.get(0).getLatitude();
		value = "polygon((" + value + "))";
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