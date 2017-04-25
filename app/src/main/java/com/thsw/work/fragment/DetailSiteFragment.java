package com.thsw.work.fragment;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.Toast;

import com.thsw.work.R;
import com.thsw.work.base.BaseFragment;
import com.thsw.work.bean.BarChartBean;
import com.thsw.work.custom.CombineChart;

/***
 * 长势Fragment界面
 * 
 * @author pc
 * 
 */
public class DetailSiteFragment extends BaseFragment {
	String[] rightYLabels;
	boolean change = true;

	private CombineChart combineChart;

	@Override
	protected int Layout() {
		// TODO Auto-generated method stub
		return R.layout.detail_site_fragment;
	}

	@Override
	protected void initView(View mView) {
		// TODO Auto-generated method stub
		combineChart = (CombineChart) mView.findViewById(R.id.cb_chart);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		rightYLabels = new String[] { "0级", "5级", "10级", "%0rh", "50%rh",
				"100%rh", "-50" + getString(R.string.degree_centigrade),
				"0" + getString(R.string.degree_centigrade),
				"50" + getString(R.string.degree_centigrade) };
		List<BarChartBean> data = new ArrayList<BarChartBean>();
		data.add(new BarChartBean("7/1", 1003, 500, 600));
		data.add(new BarChartBean("7/2", 890, 456, 123));
		data.add(new BarChartBean("7/3", 456, 741, 654));
		data.add(new BarChartBean("7/4", 258, 951, 12));
		data.add(new BarChartBean("7/5", 863, 45, 99));
		data.add(new BarChartBean("7/6", 357, 235, 456));
		data.add(new BarChartBean("7/7", 452, 321, 55));
		data.add(new BarChartBean("7/8", 654, 555, 666));
		data.add(new BarChartBean("7/9", 321, 333, 222));
		data.add(new BarChartBean("7/10", 846, 111, 444));
		List<Float> winds = new ArrayList<Float>();
		winds.add(5f);
		winds.add(6f);
		winds.add(8f);
		winds.add(9f);
		winds.add(4f);
		winds.add(7f);
		winds.add(3f);
		winds.add(1f);
		winds.add(5.5f);
		winds.add(4.8f);
		List<Float> hum = new ArrayList<Float>();
		hum.add(50f);
		hum.add(60f);
		hum.add(80f);
		hum.add(90f);
		hum.add(40f);
		hum.add(70f);
		hum.add(30f);
		hum.add(10f);
		hum.add(55f);
		hum.add(48f);
		List<Float> tem = new ArrayList<Float>();
		tem.add(38f);
		tem.add(36f);
		tem.add(27f);
		tem.add(22f);
		tem.add(15f);
		tem.add(-20f);
		tem.add(-30f);
		tem.add(-40f);
		tem.add(10f);
		tem.add(18f);

		combineChart.setLeftYAxisLabels("kwh");
		combineChart.setItems(data, winds, hum, tem, rightYLabels);
		combineChart
				.setOnItemBarClickListener(new CombineChart.OnItemBarClickListener() {
					@Override
					public void onClick(int position) {
						Toast.makeText(getActivity(), "点击了：" + position,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_change:
			combineChart.setRightYLabels(rightYLabels);
			break;
		}

	}
}
