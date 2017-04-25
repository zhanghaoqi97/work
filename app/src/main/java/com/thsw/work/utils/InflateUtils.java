package com.thsw.work.utils;

import java.io.File;

import android.content.Context;
import android.view.View;

import com.thsw.work.application.MyApplication;

/** 打气工具? */
public class InflateUtils {
	/**
	 * 打气
	 * 
	 * @param resource
	 * @return
	 */
	public static View inflate(int resource) {
		return View.inflate(MyApplication.getContext(), resource, null);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	public static boolean fileIsExists() {
		try {
			File f = new File("file:///mnt/sdcard/BasiImageMap/Layers");
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

}
