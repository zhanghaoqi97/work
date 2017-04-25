package com.thsw.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.thsw.work.intentactivity.DetailsActivity;
import com.thsw.work.utils.SpUtils;

/**
 *
 * @author Administrator
 * 
 */
public class StartLctActivity extends Activity {

	private Boolean mFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_lct);
		mFlag = (Boolean) SpUtils.get(getApplicationContext(), "flag", false);
		if (mFlag == false) {
			// 跳转到引导页
			Intent intent = new Intent(getApplicationContext(),
					GuideActivity.class);
			startActivity(intent);
			SpUtils.put(getApplicationContext(), "flag", true);
			finish();
		} else {
//			 跳转到帧动画界面
			Intent intent = new Intent(getApplicationContext(),
					DetailsActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
