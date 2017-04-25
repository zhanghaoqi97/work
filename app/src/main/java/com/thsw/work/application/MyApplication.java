package com.thsw.work.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import cn.jpush.android.api.JPushInterface;

import com.iflytek.cloud.SpeechUtility;
import com.thsw.work.R;

/**
 * 创建启动Application
 * 
 * @author Administrator
 * 
 */
public class MyApplication extends Application {
	/** 全局上下文 */
	private static Context mContext;
	public Vibrator mVibrator;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mVibrator = (Vibrator) getApplicationContext().getSystemService(
				Service.VIBRATOR_SERVICE);
		mContext = this.getApplicationContext();
		// 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用半角“,”分隔。
		// 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

		// 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
		SpeechUtility.createUtility(MyApplication.this, "appid="
				+ getString(R.string.app_id));
		// 初始化科大讯飞
		 JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
         JPushInterface.resumePush(getApplicationContext());
	}

	/**
	 * 全局上下文
	 * 
	 * @return
	 */
	public static Context getContext() {
		return mContext;
	}
	
}
