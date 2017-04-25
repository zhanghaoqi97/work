package com.thsw.work.queryastc;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.thsw.work.utils.ArcgisUtils;

/**
 * 
 * 查询DetailsActivity中的搜索结果以及查询结果后绘画出面积 任务异步执行.
 * 
 */
public class QueryDetailXZQY extends AsyncTask<String, Void, FeatureResult> {

	private ProgressDialog progress;
	private Context context;
	private String area;
	private GraphicsLayer searchGraphicsLayer;
	private MapView mMapView;
	private ArcGISDynamicMapServiceLayer tileLayer;
	private GraphicsLayer labelGraphicsLayer;

	public QueryDetailXZQY(Context context, MapView mMapView, GraphicsLayer searchGraphicsLayer, GraphicsLayer labelGraphicsLayer, String area, ArcGISDynamicMapServiceLayer tileLayer) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.mMapView=mMapView;
		this.labelGraphicsLayer=labelGraphicsLayer;
		this.searchGraphicsLayer=searchGraphicsLayer;
		this.area=area;
		this.tileLayer=tileLayer;
	}

	@Override
	protected void onPreExecute() {
		progress = new ProgressDialog(context);
		// 在未查询出结果时显示一个进度条
		progress = ProgressDialog
				.show(context, "", "请等待…查询任务正在执行");

	}

	/**
	 * 
	 * 参数数组中的第一个成员是查询地址；第二个成员是其中的子句.
	 */
	@Override
	protected FeatureResult doInBackground(String... queryArray) {

		if (queryArray == null || queryArray.length <= 1)
			return null;
		// 查询条件和URL参数
		String url = queryArray[0];
		// 查询所需的参数类
		QueryParameters qParameters = new QueryParameters();
		String whereClause = queryArray[1];
		SpatialReference sr = SpatialReference.create(3857);
		// qParameters.setGeometry(mMapView.getExtent());//设置地图的范围
		qParameters.setOutSpatialReference(sr);// 设置查询输出的坐标系
		qParameters.setReturnGeometry(true);// 是否返回空间信息
		qParameters.setWhere(whereClause);// where条件

		QueryTask qTask = new QueryTask(url);// 查询任务类

		try {
			return qTask.execute(qParameters);// 鎵ц鏌ヨ锛岃繑鍥炴煡璇㈢粨鏋�
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("TAG", e.toString());
		}
		return null;

	}

	@Override
	protected void onPostExecute(FeatureResult results) {

		String message = "没有结果回来";

		if (results != null) {
			searchGraphicsLayer.removeAll();
			labelGraphicsLayer.removeAll();
			int size = (int) results.featureCount();
			for (Object element : results) {
				progress.incrementProgressBy(size / 100);
				if (element instanceof Feature) {
					Feature feature = (Feature) element;
					// turn feature into graphic
					Geometry geometry = feature.getGeometry();
					Graphic graphic = new Graphic(geometry,
							feature.getSymbol(), feature.getAttributes());
					// add graphic to layer
					searchGraphicsLayer.addGraphic(graphic);
					// 将搜索的Geometry对象居中
					mMapView.setExtent(feature.getGeometry());
					mMapView.refreshDrawableState();
					tileLayer.refresh();
				
					// 判断如果统计分析没有返回解决则执行搜索行政区域进行高亮 解决搜索和统计分析中计算面积的问题
					if (!area.equals("")) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PictureMarkerSymbol textSymbol = ArcgisUtils
										.createTextPicMarkerSymbol(area + "亩",
												16, Color.BLACK, Color.RED,
												Color.argb(100, 255, 255, 255));// 红框、黑字
								Graphic textGraphic = new Graphic(mMapView
										.getCenter(), textSymbol);
								labelGraphicsLayer.addGraphic(textGraphic);
								area = "";
								
							}
						}, 500);
					}

				}
			}
			// update message with results
			message = "搜索出" + String.valueOf(results.featureCount()) + "个结果";

		}
		progress.dismiss();
		Toast toast = Toast.makeText(context, message,
				Toast.LENGTH_LONG);
		toast.show();

	}

}