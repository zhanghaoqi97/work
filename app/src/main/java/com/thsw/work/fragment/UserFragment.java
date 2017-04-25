package com.thsw.work.fragment;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.thsw.work.R;
import com.thsw.work.adapter.UserAdapter;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.intentactivity.MessageActivity;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

public class UserFragment extends Fragment implements OnClickListener {

	private View view;
	private ListView lv_user;
	/** 存放个人信息集合 */
	private List<String> userList;
	private TextView mTv_finish_login;
	private TextView mTv_username;

	private Dialog dialog;
	String finalResult = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String finalResult = (String) msg.obj;
			if (finalResult.equals("")) {// 如果信息为空说明没有存到数据库中
				dialog.dismiss();
				ToastUtils.show(getActivity(), "用户信息为空", Toast.LENGTH_LONG);
			} else if (finalResult.equals(R.string.datebase_fail)) {// 判断数据库是否异常
				dialog.dismiss();
				ToastUtils
						.show(getActivity(), R.string.fail, Toast.LENGTH_LONG);
			} else {
				dialog.dismiss();
				// 显示数据
				lv_user.setAdapter(new UserAdapter(getActivity(), userList));
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.activity_user_fragment,
				null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		lv_user = (ListView) view.findViewById(R.id.lv_user);
		userList = new ArrayList<>();
		mTv_username = (TextView) view.findViewById(R.id.tv_username);
		mTv_finish_login = (TextView) view.findViewById(R.id.tv_finish_login);
		mTv_finish_login.setOnClickListener(this);
		if (userList != null && !userList.isEmpty()) {
			mTv_username.setText(userList.get(0));
		}
		requestWebService();

	}

	/***
	 * 请求webService服务请求个人信息数据
	 */
	private void requestWebService() {
		dialog = DialogUtil.createLoadingDialog(getActivity(), "加载中...",
				true, 0);
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String spLogin_name = (String) SpUtils.get(getActivity(),
						"login_name", "");
				String spLogin_pwd = (String) SpUtils.get(getActivity(),
						"login_pwd", "");
				WebServiceBean bean = new WebServiceBean();
				bean.setUserName(spLogin_name);
				bean.setPwd(spLogin_pwd);
				SoapObject serviceData = WebServiceUtil.getServiceData(bean,
						CommonData.USERNAME, CommonData.USERAPACTION);
				finalResult = serviceData.getProperty(0).toString();
				String[] split = finalResult.split(",");
				for (int i = 0; i < split.length; i++) {
					userList.add(split[i]);
				}
				Message msg = handler.obtainMessage();
				msg.obj = finalResult;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_finish_login:
			SpUtils.remove(getActivity(), "login_pwd");
			SpUtils.remove(getActivity(), "login_name");
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment, new LoginFragment());
			ft.commit();
			JPushInterface.stopPush(getActivity());
			break;

		default:
			break;
		}
	}

}
