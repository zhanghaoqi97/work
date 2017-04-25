package com.thsw.work.intentactivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.thsw.work.R;
import com.thsw.work.fragment.LoginFragment;
import com.thsw.work.fragment.UserFragment;
import com.thsw.work.utils.SpUtils;

/**
 * 个人中心界面
 * 
 * @author lenovo
 * 
 */
public class PersonalActivity extends FragmentActivity {

	private String mLogin_name;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		initData();
	}

	private void initData() {
		mLogin_name = (String) SpUtils.get(PersonalActivity.this, "login_name",
				"");
		// TODO Auto-generated method stub
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		// 判断登录成功之后如果账号存在跳转到用户界面
		if (!"".equals(mLogin_name)) {
//			ft.add(R.id.fragment, new LoginFragment());
			ft.replace(R.id.fragment, new UserFragment(), null);
		} else {
//			ft.add(R.id.fragment, new LoginFragment());
			ft.replace(R.id.fragment, new LoginFragment(), null);
		}
		ft.commit();
	}
}
