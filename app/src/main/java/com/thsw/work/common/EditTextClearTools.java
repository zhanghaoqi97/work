package com.thsw.work.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 输入框工具类
 * 
 * @author pc
 * 
 */
public class EditTextClearTools {

	public static void addclerListener(final EditText mPphonenumber,
			final ImageView mDel_phonenumber) {
		// TODO Auto-generated method stub
		mPphonenumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			//在afterTextChanged中，调用setText()方法会循环递归触发监听器，必须合理退出递归，不然会产生异常 
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// 监听如果输入串长度大于0那么就显示clear按钮。
				String s1 = s + "";
				if (s.length() > 0) {
					mDel_phonenumber.setVisibility(View.VISIBLE);
				} else {

					mDel_phonenumber.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mDel_phonenumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清空输入框
				mPphonenumber.setText("");
			}
		});
	}

}
