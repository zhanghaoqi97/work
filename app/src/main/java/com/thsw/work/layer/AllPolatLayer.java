package com.thsw.work.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.graphics.Color;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleFillSymbol.STYLE;
import com.google.gson.Gson;
import com.thsw.work.bean.PointObject;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.intentactivity.ImagdotActivity;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 用户的所有地块
 * 
 * @author pc
 * 
 */
public class AllPolatLayer {
	private static String spLogin_name;

	public static void LoginPolaAll(final Context context,final MapView mMapView,final GraphicsLayer mLayer) {
		spLogin_name = (String) SpUtils.get(context, "login_name",
				"");
		/**
		 * 通过WebService服务查询用所属户地块
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!spLogin_name.equals("")) {
					WebServiceBean bean = new WebServiceBean();
					bean.setUser(spLogin_name);
					SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
							CommonData.QUERYUSERDKNAME,
							CommonData.QueryUSERSOAPACTION);
					// 获取返回的结果
					String resultTwo = bodyIn.getProperty(0).toString();
					if (!resultTwo.equals("anyType{}")) {
						String[] split = resultTwo.split(";");
						for (int i = 0; i < split.length; i++) {
							String DD = split[i];
							String json = getPOLYGONWktToJson(DD, 3857);
							JsonFactory jsonFactory = new JsonFactory();
							try {
								org.codehaus.jackson.JsonParser jsonParser = jsonFactory
										.createJsonParser(json);
								MapGeometry mapgeo = GeometryEngine
										.jsonToGeometry(jsonParser);
								Geometry geo = mapgeo.getGeometry();
								SimpleLineSymbol symbol = new SimpleLineSymbol(
										Color.BLUE, 2.0f);
								SimpleFillSymbol fillSymbol = new SimpleFillSymbol(
										Color.TRANSPARENT, STYLE.SOLID);
								fillSymbol.setOutline(symbol);
								SimpleRenderer renderer = new SimpleRenderer(
										fillSymbol);
								Graphic graphic = new Graphic(geo, fillSymbol);
								mLayer.addGraphic(graphic);
								mLayer.setRenderer(renderer);
								mMapView.addLayer(mLayer);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						mMapView.addLayer(mLayer);
					}

				}
			}
		}).start();
	}

	/**
	 * Wkt转换成Json
	 * 
	 * @param wkt
	 * @param wkid
	 * @return
	 */
	public static String getPOLYGONWktToJson(String wkt, int wkid) {
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
}
