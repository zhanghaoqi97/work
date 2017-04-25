package com.thsw.work.intentactivity;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thsw.work.R;
import com.thsw.work.base.BaseActivity;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 信息上报详情
 * 
 * @author pc
 * 
 */
public class MessageUldActivity extends BaseActivity implements OnClickListener {

	private TextView mTitle_include;
	private ImageView mIv_back_include;
	private String webresult;
	private Button mBt_messageuld;
	/** 通过MessageActivity中穿过来的polygon坐标 */
	private String polygonWkt;
	/** 从Spinner列表中得到的数据 */
	private String sqData, zsData, dkData, zwData;
	/** 信息上报信息输入框 **/
	private EditText et_msguld_else, et_msguld_bch, et_msguld_number;
	/** 得到Spinner中的长势情况数值 */
	private String spSite;
	/*** 得到Spinner中的墒情情况 */
	private String spSoil;
	/*** 得到Spinner中的作物类型情况 */
	private String spGrotype_name;
	/*** 得到Spinner中的地块类型情况 */
	private String spEarth_name;
	/** 作物类型 */
	private Spinner sv_message_agrotype;
	/** 地块类型 */
	private Spinner sv_message_earth;
	/** 长势情况 */
	private Spinner sv_message_site;
	/** 墒情情况 */
	private Spinner sv_message_soil;
	/** 地块编号 */
	private String number;
	/** 病虫害情况 */
	private String bch;
	/** 其他信息 */
	private String elsemsg;
	/** 用户名称 */
	private String spLogin_name;
	private Dialog dialog;
	private String[] splits;

	@Override
	protected int inflater() {
		// TODO Auto-generated method stub
		return R.layout.activity_message_uld;
	}

	private Handler handle = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0X222) {
				String twoBodyin = (String) msg.obj;
				if (twoBodyin.equals("anyType{}")) {
					dialog.dismiss();
					ToastUtils.show(MessageUldActivity.this, "该地块目前未接收审核信息！",
							Toast.LENGTH_LONG);
				} else {
					splits = twoBodyin.split(",");
					if (splits[0].trim().equals("1")) {
						dialog.dismiss();
						ToastUtils.show(MessageUldActivity.this, "该地块已经被审核",
								Toast.LENGTH_LONG);
					} else if (splits[0].equals("0")) {
						dialog.dismiss();
						ToastUtils.show(MessageUldActivity.this, "该地块未被审核",
								Toast.LENGTH_LONG);
					}
					et_msguld_number.setText(splits[1]);
					et_msguld_bch.setText(splits[6]);
					et_msguld_else.setText(splits[7]);
					dialog.dismiss();
				}
			}
			String webresult = (String) msg.obj.toString();
			if (webresult.equals("true")) {
				dialog.dismiss();
				Intent intent = new Intent(MessageUldActivity.this,
						MessageActivity.class);
				startActivity(intent);
				finish();
				ToastUtils.show(MessageUldActivity.this, "信息上报成功！",
						Toast.LENGTH_LONG);
			} else if (webresult.equals("false")) {
				dialog.dismiss();
				ToastUtils.show(MessageUldActivity.this, "信息上报失败！" + webresult,
						0);
			} else if (webresult.equals(R.string.datebase_fail)) {
				dialog.dismiss();
				ToastUtils.show(MessageUldActivity.this, R.string.fail, 0);

			}

		};
	};

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mTitle_include.setText("信息上报");
		polygonWkt = getIntent().getStringExtra("polygonResult");
		spLogin_name = (String) SpUtils.get(MessageUldActivity.this,
				"login_name", "");
		dialog = DialogUtil.createLoadingDialog(MessageUldActivity.this,
				"加载中...", true, 0);
		dialog.show();
		/**
		 * 从信息上报地块中调用服务获取地块编码，地块类型等数据展示到Spinner控件中
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WebServiceBean bean = new WebServiceBean();
				bean.setPolygonWkt(polygonWkt);
				bean.setUser(spLogin_name);
				SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
						CommonData.VERIFYNAMEMSG, CommonData.VERIFYACTIONMSG);
				String twoBodyin = bodyIn.getProperty(0).toString();
				Message msg = handle.obtainMessage();
				msg.obj = twoBodyin;
				msg.what = 0X222;
				handle.sendMessage(msg);
			}
		}).start();

	}

	@Override
	protected void initView() {
		mTitle_include = (TextView) findViewById(R.id.tv_title_include);
		mIv_back_include = (ImageView) findViewById(R.id.iv_back_include);
		mBt_messageuld = (Button) findViewById(R.id.bt_messageuld);
		et_msguld_number = (EditText) findViewById(R.id.et_msguld_number);
		et_msguld_else = (EditText) findViewById(R.id.et_msguld_else);
		et_msguld_bch = (EditText) findViewById(R.id.et_msguld_bch);
		sv_message_agrotype = (Spinner) findViewById(R.id.sv_message_agrotype);
		sv_message_earth = (Spinner) findViewById(R.id.sv_message_earth);
		sv_message_site = (Spinner) findViewById(R.id.sv_message_site);
		sv_message_soil = (Spinner) findViewById(R.id.sv_message_soil);
		mIv_back_include.setOnClickListener(this);
		mBt_messageuld.setOnClickListener(this);

		SpinnerAdapterMethod();
	}

	/**
	 * 设置Spinner信息框的信息值
	 */
	private void SpinnerAdapterMethod() {

		sv_message_earth
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						spEarth_name = sv_message_earth.getSelectedItem()
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		sv_message_agrotype
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						spGrotype_name = sv_message_agrotype.getSelectedItem()
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		sv_message_site.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				spSite = sv_message_site.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		sv_message_soil.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				spSoil = sv_message_soil.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 通过WebService服务获取更新上报地块
	 * 
	 * @param spLogin_name
	 */
	private void getUpdatePolt(String spLogin_name) {
		// TODO Auto-generated method stub
		WebServiceBean bean = new WebServiceBean();
		bean.setPolygonWkt(polygonWkt);
		bean.setId(number);
		bean.setDklx(spEarth_name);
		bean.setZwlx(spGrotype_name);
		bean.setZsqk(spSite);
		bean.setSq(spSoil);
		bean.setBch(bch);
		bean.setOther(elsemsg);
		bean.setUser(spLogin_name);
		SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
				CommonData.UPDATEMSGNAME, CommonData.UPDATEMSGACTION);
		// 获取返回的结果
		String resultTwo = bodyIn.getProperty(0).toString();
		if (!resultTwo.equals("anyType{}")) {
			dialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 返回按钮
		case R.id.iv_back_include:
			Intent intent = new Intent(MessageUldActivity.this,
					MessageActivity.class);
			startActivity(intent);
			break;
		// 信息上报
		case R.id.bt_messageuld:
			dialog = DialogUtil.createLoadingDialog(MessageUldActivity.this,
					"加载中...", true, 0);
			dialog.show();
			String spLogin_name = (String) SpUtils.get(MessageUldActivity.this,
					"login_name", "");
			number = et_msguld_number.getText().toString().trim();
			bch = et_msguld_bch.getText().toString().trim();
			elsemsg = et_msguld_else.getText().toString().trim();
			if (splits[0].trim().equals("1")) {
				dialog.dismiss();
				getUpdatePolt(spLogin_name);
				ToastUtils.show(MessageUldActivity.this, "调用了更新上报地块1",0);
			} else if (splits[0].equals("0")) {
				dialog.dismiss();
				appearMessage();
				ToastUtils.show(MessageUldActivity.this, "调用了上报核实的地块信息0",0);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 通过WebService服务得到现有的数据对数据进行修改上报
	 */
	private void appearMessage() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				WebServiceBean bean = new WebServiceBean();
				bean.setPolygonWkt(polygonWkt);
				bean.setId(number);
				bean.setDklx(spEarth_name);
				bean.setZwlx(spGrotype_name);
				bean.setZsqk(spSite);
				bean.setSq(spSoil);
				bean.setBch(bch);
				bean.setOther(elsemsg);
				bean.setUser(spLogin_name);
				SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
						CommonData.APPEARNAME, CommonData.APPEARACTION);
				webresult = bodyIn.getProperty(0).toString();
				Message msg = handle.obtainMessage();
				msg.obj = webresult;
				handle.sendMessage(msg);
			}
		}).start();
	}
}
