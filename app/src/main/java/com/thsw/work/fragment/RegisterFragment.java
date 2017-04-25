package com.thsw.work.fragment;

import java.util.LinkedHashSet;
import java.util.Set;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.thsw.work.R;
import com.thsw.work.base.BaseFragment;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.intentactivity.StatisticsActivity;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.ExampleUtil;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 用户注册界面
 * 
 * @author lenovo
 * 
 */
public class RegisterFragment extends BaseFragment implements OnClickListener,
		OnWheelChangedListener {

	private TextView mTitle;
	private ImageView mIv_back_include;
	/** 用户名 */
	private EditText mEt_username;
	/** 密码 */
	private EditText mEt_userpwd;
	/** 姓名 */
	private EditText mEt_user;
	/** 性别 */
	private EditText mEt_usersex;
	/** 出生年月 */
	private EditText mEt_userborn;
	/** 身份证 */
	private EditText mEt_usercard;
	/** 电话 */
	private EditText mEt_userphone;
	/** 注册申请 */
	private Button mTv_personal;
	private Dialog dialog;
	private String result;
	private static final int USERMESSAGE = 1001;
	private WheelView mViewProvince;
	private WheelView mViewDistrict;
	private WheelView mViewCity;
	private Button mBtnConfirm;
	private Button mBtnCancel;
	private String xzdm;
	private Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case USERMESSAGE:
				String lowerCase = result.toLowerCase();
				// 获取bundle对象的值
				Bundle b = msg.getData();
				String username = b.getString("userName");
				String result = b.getString("result");
				if (lowerCase.equals("true")) {
					dialog.dismiss();
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.fragment, new LoginFragment());
					ft.commit();
					ToastUtils
							.show(getActivity(), "请登录您的账号", Toast.LENGTH_LONG);
				} else if (lowerCase.equals("false")) {
					dialog.dismiss();
					ToastUtils.show(getActivity(), "注册失败:用戶名可能已被注册！",
							Toast.LENGTH_LONG);
				} else if (result.equals(R.string.fail)) {
					dialog.dismiss();
					ToastUtils.show(getActivity(), R.string.datebase_fail,
							Toast.LENGTH_LONG);
				}
				break;

			default:
				break;
			}

		};
	};
	private String userName;
	private TextView tv_userarea;
	private PopupWindow popupWindow;

	protected void initData() {
		mTitle.setText("用户注册");

	}

	@Override
	protected int Layout() {
		// TODO Auto-generated method stub
		return R.layout.register_fragment;
	}

	@Override
	protected void initView(View view) {
		// TODO Auto-generated method stub
		mTitle = (TextView) view.findViewById(R.id.tv_title_include);
		mIv_back_include = (ImageView) view.findViewById(R.id.iv_back_include);
		tv_userarea = (TextView) view.findViewById(R.id.tv_userarea);
		mTv_personal = (Button) view.findViewById(R.id.tv_personal);
		mEt_username = (EditText) view.findViewById(R.id.et_username);
		mEt_userpwd = (EditText) view.findViewById(R.id.et_userpwd);
		mEt_user = (EditText) view.findViewById(R.id.et_user);
		mEt_usersex = (EditText) view.findViewById(R.id.et_usersex);
		mEt_userborn = (EditText) view.findViewById(R.id.et_userborn);
		mEt_usercard = (EditText) view.findViewById(R.id.et_usercard);
		mEt_userphone = (EditText) view.findViewById(R.id.et_userphone);
		mTv_personal.setOnClickListener(this);
		mIv_back_include.setOnClickListener(this);
		tv_userarea.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back_include:
			Intent back_intet = new Intent(getActivity(),
					PreferenceActivity.class);
			startActivity(back_intet);
			break;
		case R.id.tv_personal:
			visitWebService();
			break;
		case R.id.tv_userarea:
			View view = View
					.inflate(getActivity(), R.layout.wellview_pop, null);
			mViewProvince = (WheelView) view.findViewById(R.id.id_province);
			mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
			mViewCity = (WheelView) view.findViewById(R.id.id_city);
			mBtnConfirm = (Button) view.findViewById(R.id.btn_confirms);
			mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
			// 设置popupwindow视图,规定popupwindow宽高
			popupWindow = new PopupWindow(view,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			// 点击popupwindow区域外部时,让popupwindow消失(想要实现这个效果,一定要设置popupwindow的背景)
			popupWindow.setOutsideTouchable(false);
			// 获取焦点
			popupWindow.setFocusable(true);
			// backgroundAlpha(1f);
			// 设popupwindow背景
			popupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
			// 展示popupwindow,参数一:popupwindow展示时的父控件,参数二:popupwindow展示时的重力方向,参数三:x轴偏移值,参数四:y轴偏移值,
			popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			setUpListener();
			setPopuListener();
			setUpData();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getActivity().getWindow()
				.getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getActivity().getWindow().setAttributes(lp);
	}

	private void setPopuListener() {
		// TODO Auto-generated method stub
		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				xzdm = mCurrentZipCode;
				// TODO Auto-generated method stub
				tv_userarea.setText(mCurrentProviceName + ","
						+ mCurrentCityName + "," + mCurrentDistrictName + ","
						+ mCurrentZipCode);
				popupWindow.dismiss();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				getActivity(), mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(10);
		mViewCity.setVisibleItems(10);
		mViewDistrict.setVisibleItems(10);
		updateCities();
		updateAreas();
	}

	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(
				getActivity(), areas));
		mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(),
				cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 请求WebService服务验证信息是否正确
	 */
	private void visitWebService() {
		userName = mEt_username.getText().toString().trim();
		final String pwd = mEt_userpwd.getText().toString().trim();
		final String user = mEt_user.getText().toString().trim();
		final String sex = mEt_usersex.getText().toString().trim();
		final String born = mEt_userborn.getText().toString().trim();
		final String card = mEt_usercard.getText().toString().trim();
		final String phone = mEt_userphone.getText().toString().trim();
		if (userName.equals("") || pwd.equals("") || user.equals("")
				|| sex.equals("") || born.equals("") || card.equals("")
				|| phone.equals("")) {
			ToastUtils.show(getActivity(), "请完善您的用户信息", Toast.LENGTH_LONG);
		} else {
			if (sex.length() > 1) {
				mEt_usersex.setError("您的性别输入有误");
				mEt_usersex.requestFocus();
				return;
			} else {
				if (card.length() > 18 || card.length() < 18) {
					mEt_usercard.setError("您的身份证输入有误");
					mEt_usercard.requestFocus();
					return;
				} else {
					if (phone.length() < 11) {
						mEt_userphone.setError("您的手机号码有误");
						mEt_userphone.requestFocus();
					} else {

						new Thread(new Runnable() {

							@Override
							public void run() {
								dialog = DialogUtil.createLoadingDialog(
										getActivity(), "注册中...", true, 0);
								dialog.show();

								WebServiceBean bean = new WebServiceBean();
								bean.setUserName(userName);
								bean.setPwd(pwd);
								bean.setUser(user);
								bean.setSex(sex);
								bean.setBorn(born);
								bean.setCard(card);
								bean.setPhone(phone);
								bean.setPro(mCurrentProviceName);
								bean.setCity(mCurrentCityName);
								bean.setCoun(mCurrentDistrictName);
								SoapObject bodyIn = WebServiceUtil
										.getServiceData(bean,
												CommonData.REGISTERNAME,
												CommonData.REGISTERSOAPACTION);
								result = bodyIn.getProperty(0).toString();
								Bundle b = new Bundle();
								b.putString("result", result);
								Message msg = handle.obtainMessage();
								msg.what = USERMESSAGE;
								msg.setData(b);
								handle.sendMessage(msg);
							}
						}).start();

					}
				}
			}
		}
	}

}
