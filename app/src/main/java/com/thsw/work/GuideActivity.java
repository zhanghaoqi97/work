package com.thsw.work;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.thsw.work.intentactivity.DetailsActivity;
import com.thsw.work.utils.InflateUtils;


public class GuideActivity extends Activity {

	private ViewPager guide_vp;
	private List<View> mVList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
		initData();
	}

	private void initView() {
		guide_vp = (ViewPager) findViewById(R.id.guide_vp);
		guide_vp.setOnPageChangeListener(new OnPageChangeListener() {

			private int GUIDETHREE = 2;

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == GUIDETHREE) {
					View three_iv = findViewById(R.id.guide_three_iv);
					three_iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent=new Intent(GuideActivity.this, DetailsActivity.class);
							startActivity(intent);
							finish();
						}
					});
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	
	}

	private void initData() {
		mVList = new ArrayList<View>();
		View guide_one =InflateUtils.inflate(R.layout.guide_one);
		View guide_two = InflateUtils.inflate(R.layout.guide_two);
		View guide_three = InflateUtils.inflate(R.layout.guide_three);
		mVList.add(guide_one);
		mVList.add(guide_two);
		mVList.add(guide_three);
		guide_vp.setAdapter(new FirstPagerAdapter());
	}

	class FirstPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mVList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mVList.get(position));
			return mVList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((View) object);
		}
	}
}
