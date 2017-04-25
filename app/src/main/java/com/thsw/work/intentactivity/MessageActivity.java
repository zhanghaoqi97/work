package com.thsw.work.intentactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnStatusChangedListener.STATUS;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleFillSymbol.STYLE;
import com.google.gson.Gson;
import com.thsw.work.R;
import com.thsw.work.base.BaseActivity;
import com.thsw.work.bean.PointObject;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.GeometryUtils;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 信息上报
 * 
 * @author pc
 * 
 */
public class MessageActivity extends BaseActivity {
	// 初始化Arcgis底图
	MapView mMapView = null;
	// 添加Arcgis底图服务
	ArcGISDynamicMapServiceLayer tileLayer;
	private ArcGISLocalTiledLayer layer;
	private ArcGISDynamicMapServiceLayer tileLayer2;
	private String queryLayer;

	private ProgressDialog progress;
	private GraphicsLayer graphicsLayer;
	/**
	 * 获取服务器返回的结果集
	 */
	private String result;
	/**
	 * 获取到当前点击地图中的坐标值
	 */
	private float mX2;
	private float mY2;

	@Override
	protected int inflater() {
		// TODO Auto-generated method stub
		return R.layout.activity_message;
	}

	private Dialog dialog;
	/** 所属户地块Layer */
	private GraphicsLayer mLayer;
	/** 需要核实的地块Layer */
	private GraphicsLayer VerifyLayer;
	private ArcGISDynamicMapServiceLayer fdjskl;
	private ArcGISTiledMapServiceLayer tiledLayer;

	@Override
	protected void initData() {
		mLayer = new GraphicsLayer();
		VerifyLayer = new GraphicsLayer();
		graphicsLayer = new GraphicsLayer();
		fdjskl = new ArcGISDynamicMapServiceLayer(CommonData.BASEARCGISURL);
		mMapView.addLayer(fdjskl);
		// 去除水印
		ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
		// // 加载Layer数据
		layer = new ArcGISLocalTiledLayer(
				"file:///mnt/sdcard/BasiImageMap/Layers");
		mMapView.addLayer(layer);

		tiledLayer = new ArcGISTiledMapServiceLayer(CommonData.YXARCGISURL);
		mMapView.addLayer(tiledLayer);

		// // 添加Arcgis底图服务
		tileLayer = new ArcGISDynamicMapServiceLayer(CommonData.DIKUAIURL3857);
		mMapView.addLayer(tileLayer);
		Envelope initextext = new Envelope(12902178.420333067,
				4503651.9433372505, 12965237.481534274, 4561442.030881682);
		mMapView.setExtent(initextext);
		mMapView.addLayer(mLayer);
		mMapView.addLayer(VerifyLayer);
		dialog = DialogUtil.createLoadingDialog(MessageActivity.this, "加载中...",
				true, 0);
		dialog.show();
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

			@Override
			public void onStatusChanged(Object obj, STATUS status) {
				// TODO Auto-generated method stub
				// 判断地图是否加载完成
				if (status.equals(STATUS.LAYER_LOADED)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							String spLogin_name = (String) SpUtils.get(
									MessageActivity.this, "login_name", "");
							if (!spLogin_name.equals("")) {
								getRemoteInfo(spLogin_name);
								getVerifyPolt(spLogin_name);
								dialog.dismiss();
							}
						}

					}).start();
				}
			}
		});

		// 点击MapView获取WebService服务中的数据
		mMapView.setOnSingleTapListener(new OnSingleTapListener() {

			private SimpleFillSymbol fillSymbol;

			@Override
			public void onSingleTap(float x, float y) {
				// TODO Auto-generated method stub
				Point pt = mMapView.toMapPoint(x, y);
				Point toponit = mMapView.toScreenPoint(pt);
				float toX = (float) toponit.getX();
				float toY = (float) toponit.getY();
				fillSymbol = new SimpleFillSymbol(Color.BLUE,
						SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
				fillSymbol.setAlpha(80);
				// float xd=(float)dd;
				// 检索表示此层中所有图形的唯一标识的数组, 返回指向点附近的图形
				int[] ids = mLayer.getGraphicIDs(toX, toY, 10);
				if (ids.length != 0) {
					// 检索当前 光标点（手指按压位置）的附近的 graphic对象
					Graphic indentifyGf = mLayer.getGraphic(ids[0]);
					Geometry geoIndentify = indentifyGf.getGeometry();
					Graphic gGraphic = new Graphic(geoIndentify, fillSymbol);
					graphicsLayer.addGraphic(gGraphic);
					// 将几何对象生成wkt字符串
					String result = GeometryUtils.GeometryToWKT(geoIndentify);
					Intent intent = new Intent(MessageActivity.this,
							MessageUldActivity.class);
					intent.putExtra("polygonResult", result);
					startActivity(intent);
				}

			}
		});

	}

	/***
	 * 通过WebService服务获取需要核实的地块
	 * 
	 * @param user
	 */
	private void getVerifyPolt(String user) {
		WebServiceBean bean = new WebServiceBean();
		bean.setUser(user);
		SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
				CommonData.VERIFYNAMEPLOT, CommonData.VERIFYACTIONPLOT);
		// 获取返回的结果
		String resultTwo = bodyIn.getProperty(0).toString();
		if (!resultTwo.equals("anyType{}")) {
			dialog.dismiss();
			String[] split = resultTwo.split(";");
			VerifyLayer.removeAll();
			for (int i = 0; i < split.length; i++) {
				String splits = split[i];
				String json = getPOLYGONWktToJson(splits, 3857);
				JsonFactory jsonFactory = new JsonFactory();
				try {
					JsonParser jsonParser = jsonFactory.createJsonParser(json);
					MapGeometry mapgeo = GeometryEngine
							.jsonToGeometry(jsonParser);

					Geometry geo = mapgeo.getGeometry();
					// SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(
					// Color.RED);
					// Graphic graphic = new Graphic(geo, simpleFillSymbol);
					// VerifyLayer.addGraphic(graphic);
					// mMapView.addLayer(VerifyLayer);
					SimpleLineSymbol outLine = new SimpleLineSymbol(Color.RED,
							1.0f);
					SimpleFillSymbol simpleFill = new SimpleFillSymbol(
							Color.TRANSPARENT, STYLE.SOLID);
					simpleFill.setOutline(outLine);
					SimpleRenderer sr1 = new SimpleRenderer(simpleFill);
					Graphic graphic = new Graphic(geo, simpleFill);
					VerifyLayer.addGraphic(graphic);
					VerifyLayer.setRenderer(sr1);
					mMapView.addLayer(VerifyLayer);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/***
	 * 通过WebService服务查询用所属户地块
	 * 
	 * @param user
	 */
	private void getRemoteInfo(String user) {
		WebServiceBean bean = new WebServiceBean();
		bean.setUser(user);
		SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
				CommonData.QUERYUSERDKNAME, CommonData.QueryUSERSOAPACTION);
		// 获取返回的结果
		String resultTwo = bodyIn.getProperty(0).toString();
		if (!resultTwo.equals("anyType{}")) {
			String[] split = resultTwo.split(";");
			mLayer.removeAll();
			for (int i = 0; i < split.length; i++) {
				String splits = split[i];
				String json = getPOLYGONWktToJson(splits, 3857);
				JsonFactory jsonFactory = new JsonFactory();
				try {
					JsonParser jsonParser = jsonFactory.createJsonParser(json);
					MapGeometry mapgeo = GeometryEngine
							.jsonToGeometry(jsonParser);
					Geometry geo = mapgeo.getGeometry();
					// Graphic graphic = new Graphic(geo, new SimpleFillSymbol(
					// Color.BLUE));
					// mLayer.addGraphic(graphic);
					SimpleLineSymbol outLine = new SimpleLineSymbol(Color.BLUE,
							1.0f);
					SimpleFillSymbol simpleFill = new SimpleFillSymbol(
							Color.TRANSPARENT, STYLE.SOLID);
					simpleFill.setOutline(outLine);
					SimpleRenderer sr1 = new SimpleRenderer(simpleFill);
					Graphic graphic = new Graphic(geo, simpleFill);
					mLayer.addGraphic(graphic);
					mLayer.setRenderer(sr1);
					mMapView.addLayer(mLayer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public String getPOLYGONWktToJson(String wkt, int wkid) {
		PointObject polygonObject = new PointObject();
		List<List<Double[]>> lists = new ArrayList<List<Double[]>>();
		String ToTailWkt = wkt.substring(0, wkt.length() - 1);
		String[] strHead = ToTailWkt.split("\\(", 2);
		String[] strList = strHead[1].split("\\), \\(");
		for (int i = 0; i < strList.length; i++) {
			String item = strList[i].trim();
			item = item.substring(1, item.length() - 1);
			String[] items = item.split(",");
			List<Double[]> list = new ArrayList<Double[]>();
			for (int j = 0; j < items.length; j++) {
				String jItem = items[j].trim();
				String[] jItems = jItem.split(" ");
				Double[] listResult = new Double[] {
						Double.parseDouble(jItems[0]),
						Double.parseDouble(jItems[1]) };
				list.add(listResult);

			}
			lists.add(list);
			HashMap<String, Integer> spatialReference = new HashMap<String, Integer>();
			spatialReference.put("wkid", wkid);
			polygonObject.setRings(lists);
			polygonObject.setSpatialReference(spatialReference);
			Gson gson = new Gson();
			return gson.toJson(polygonObject);
		}
		return null;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		// 初始化布局文件
		mMapView = (MapView) findViewById(R.id.map);
	}

}
