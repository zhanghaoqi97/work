package com.thsw.work.fragment;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.thsw.work.R;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.common.EditTextClearTools;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.intentactivity.DetailsActivity;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.ExampleUtil;
import com.thsw.work.utils.NetWorkUtil;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

public class LoginFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mIv_back_include;
	private TextView mTitle;
	private EditText mPphonenumber;
	private EditText mPassword;
	private Button mLoginButton;
	private TextView mPersonregister;
	private ImageView mDel_phonenumber;
	private ImageView mDel_password;

	private Dialog dialog;
	/**
	 * 获取服务器返回的结果集
	 */
	private String result;
	private String fruit;
	/**
	 * 在4.0以上之后
	 */
	private String login_name;
	private Handler handle = new Handler() {


		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			login_name = bundle.getString("name");
			String login_pwd = bundle.getString("pwd");
			fruit = bundle.getString("result");
			String[] split = fruit.split(",");
			if (!fruit.equals("")) {
				// ","隔开的多个 转换成 Set
				String[] sArray = split[1].split(",");
				Set<String> tagSet = new LinkedHashSet<String>();
				for (String sTagItme : sArray) {
					if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
						Toast.makeText(getActivity(),
								R.string.error_tag_gs_empty, Toast.LENGTH_SHORT)
								.show();
						return;
					}
					tagSet.add(sTagItme);
				}
				JPushInterface.setAliasAndTags(getActivity(),
						(String) login_name, (Set<String>) tagSet, null);
				SpUtils.put(getActivity(), "login_name", login_name);
				SpUtils.put(getActivity(), "login_pwd", login_pwd);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						WebServiceBean bean = new WebServiceBean();
						bean.setAlias(login_name);
						SoapObject serviceData = WebServiceUtil.getServiceData(
								bean, CommonData.ALIASNNAME,
								CommonData.ALIASACTION);
						String string = serviceData.getProperty(0).toString();

					}
				}).start();
				ToastUtils.show(getActivity(), "登录成功", Toast.LENGTH_LONG);
				dialog.dismiss();
				// 登录成功之后进入Arcgis地图界面
				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				startActivity(intent);
				getActivity().finish();
			} else if (fruit.equals("anyType{}")) {
				dialog.dismiss();
				ToastUtils
						.show(getActivity(), "账户不存在，请注册账号", Toast.LENGTH_LONG);
			} else if (fruit.equals(R.string.fail)) {
				ToastUtils.show(getActivity(), R.string.datebase_fail,
						Toast.LENGTH_LONG);
			}
		};
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.login_fragment, null);
		initView(view);
		initData();
		return view;
	}

	/**
	 * 请求WebService服务验证信息是否正确
	 */
	private void VisitWebService() {
		// TODO Auto-generated method stub
		final String mNumber = mPphonenumber.getText().toString().trim();
		final String mPwd = mPassword.getText().toString().trim();
		if (mNumber.equals("")) {
			Toast.makeText(getActivity(), "账号不能为空", Toast.LENGTH_LONG).show();
		}
		if (mPwd.equals("")) {
			Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_LONG).show();
		} else {
			// 简单判断用户输入的账号是否合法
			if (mNumber.equals("")) {
				mPphonenumber.setError("您输入的账号有误请重新输入");
				mPphonenumber.requestFocus();
				return;
			}
			dialog = DialogUtil.createLoadingDialog(getActivity(), "登陆中...",
					true, 0);
			dialog.show();

			RequestParams params = new RequestParams();

			params.addBodyParameter("userName", "张7");
			params.addBodyParameter("pwd", "123");

			HttpUtils httpUtils = new HttpUtils();
			httpUtils.send(HttpRequest.HttpMethod.POST, CommonData.API.LOGINURL, params, new RequestCallBack<String>() {

				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					Log.e("TAG", responseInfo.result+"+++++++++++++");
					dialog.dismiss();
					Toast.makeText(getActivity(), "responseInfo"+responseInfo.result, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFailure(HttpException error, String msg) {

				}
			});



//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					WebServiceBean bean = new WebServiceBean();
//					bean.setUserName(mNumber);
//					bean.setPwd(mPwd);
//					SoapObject serviceData = WebServiceUtil.getServiceData(
//							bean, CommonData.LOGINNAME, CommonData.LOGINACTION);
//
//					if(serviceData!=null){
//						Log.i("TAG", serviceData+"");
//						String result = serviceData.getProperty(0).toString();
//
//						Message msg = handle.obtainMessage();
//						Bundle data = new Bundle();
//						data.putString("result", result);
//						data.putString("name", mNumber);
//						data.putString("pwd", mPwd);
//						msg.setData(data);
//						handle.sendMessage(msg);
//					}else{
//						Log.i("TAG", serviceData+"数据为空");
//						ToastUtils.show(getActivity(), "访问网络失败请重新加载！！",Toast.LENGTH_LONG);
//						dialog.dismiss();
//					}
//				}
//			}).start();
		}

	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		mTitle = (TextView) view.findViewById(R.id.tv_title_include);
		mLoginButton = (Button) view.findViewById(R.id.loginButton);
		mPphonenumber = (EditText) view.findViewById(R.id.phonenumber);
		mPassword = (EditText) view.findViewById(R.id.password);
		mPersonregister = (TextView) view.findViewById(R.id.tv_personregister);
		mDel_phonenumber = (ImageView) view.findViewById(R.id.del_phonenumber);
		mDel_password = (ImageView) view.findViewById(R.id.del_password);
		mIv_back_include = (ImageView) view.findViewById(R.id.iv_back_include);
		mLoginButton.setOnClickListener(this);
		mPersonregister.setOnClickListener(this);
		mIv_back_include.setOnClickListener(this);
	}

	protected void initData() {
		mTitle.setText("个人中心");
		// 添加清楚监听器
		EditTextClearTools.addclerListener(mPphonenumber, mDel_phonenumber);
		EditTextClearTools.addclerListener(mPassword, mDel_password);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.loginButton:
			if (!NetWorkUtil.isNetworkAvailable(getActivity())) {
				Toast.makeText(getActivity(), "当前没有可用网络！", Toast.LENGTH_LONG)
						.show();
			} else {

				VisitWebService();

			}
			break;
		case R.id.tv_personregister:
			FragmentManager fmregist = getActivity()
					.getSupportFragmentManager();
			FragmentTransaction ftregist = fmregist.beginTransaction();
			ftregist.replace(R.id.fragment, new RegisterFragment());
			ftregist.commit();
			break;
		case R.id.iv_back_include:
			getActivity().finish();
			break;
		default:
			break;
		}
	}
}
