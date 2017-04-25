package com.thsw.work.intentactivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thsw.work.AlbumActivity;
import com.thsw.work.GalleryActivity;
import com.thsw.work.R;
import com.thsw.work.bean.ImageItem;
import com.thsw.work.bean.PlotDataBean;
import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;
import com.thsw.work.utils.Bimp;
import com.thsw.work.utils.DialogUtil;
import com.thsw.work.utils.FileUtils;
import com.thsw.work.utils.PublicWay;
import com.thsw.work.utils.Res;
import com.thsw.work.utils.SpUtils;
import com.thsw.work.utils.ToastUtils;
import com.thsw.work.utils.WebServiceUtil;

/**
 * 地块认领
 * 
 * @author pc
 * 
 */
public class PolaActivity extends Activity implements OnClickListener {

	private static final int RESULT_MESSAGE = 1;

	private static final int SCALE = 5;// 照片缩小比例
	/**
	 * 返回跳转请求码
	 */
	private static final int IMGRESULT_MESSAGE = 1234;
	private static final int GPSRESULT_MESSAGE = 4567;
	private static final String TAG = "PolaActivity";
	private TextView mTitle_include;
	private RadioButton mRbimgsketch;
	private RadioButton mGpssketch;
	private ImageView mStartdot;
	private EditText mEt_pola_area;
	private Button mBt_confirm;
	private String result;
	/** imgedotActivity中的勾画poly点坐标 */
	private String polyValue;
	private ImageView mIv_back_include;
	/**
	 * 存放点的经纬度集合
	 */
	private ArrayList<PlotDataBean> xyPlotList;
	private PopupWindow pop;
	private LinearLayout ll_popup;
	private com.thsw.work.custom.CamGridViewCoustom noScrollgridview;
	private GridAdapter adapter;
	public static Bitmap bimap;
	/** 将图片转换成字符串形式的格式 */
	String switchString = null;
	private Dialog dialog;
	private static final int TAKE_PICTURE = 0x000001;
	/** 读取手机中存取的用户名 **/
	private String spLogin_name;
	private Handler handle = new Handler() {
		private Bitmap bimap;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0X11) {
				String msgResult = (String) msg.obj;
				String[] split = msgResult.split(",");
				et_pola_name.setText(split[0] + "");
				et_pola_number.setText(split[1] + "");
				et_pola_phone.setText(split[2] + "");
				dialog.dismiss();
			}

			if (msg.what == 0X66) {
				String finalResult = (String) msg.obj;
				if (finalResult.equals("1") && finalResult.equals("true")) {
					if (finalResult.equals("true")) {
						dialog.dismiss();
						ToastUtils.show(PolaActivity.this, "地块认领成功！",
								Toast.LENGTH_LONG);
						Intent intent = new Intent(PolaActivity.this,
								DetailsActivity.class);
						startActivity(intent);
						finish();
					}
				} else if (finalResult.equals(R.string.fail)) {
					dialog.dismiss();
					ToastUtils.show(PolaActivity.this, R.string.datebase_fail,
							Toast.LENGTH_LONG);

				} else if (finalResult.equals("false")
						&& finalResult.equals("0")) {
					dialog.dismiss();
					ToastUtils.show(PolaActivity.this, "地块认领失败！",
							Toast.LENGTH_LONG);
				}

			}
		}
	};

	private View parentView;
	/** ImagdotActivity中的面积变量 */
	private String returArea = "";
	private EditText et_pola_name;

	private EditText et_pola_number;

	private EditText et_pola_phone;

	/** ImagdotActivity中的删除变量 */
	private String polygonWkt = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		xyPlotList = new ArrayList<>();
		Res.init(this);
		bimap = BitmapFactory.decodeResource(getResources(), R.drawable.camicm);
		PublicWay.activityList.add(this);
		parentView = getLayoutInflater().inflate(R.layout.activity_pola, null);
		setContentView(parentView);
		mTitle_include = (TextView) findViewById(R.id.tv_title_include);
		mRbimgsketch = (RadioButton) findViewById(R.id.rb_pola_imgsketch);
		mGpssketch = (RadioButton) findViewById(R.id.rb_pola_gpssketch);
		mBt_confirm = (Button) findViewById(R.id.bt_confirm);
		mStartdot = (ImageView) findViewById(R.id.iv_startdot);
		mEt_pola_area = (EditText) findViewById(R.id.et_pola_area);
		mIv_back_include = (ImageView) findViewById(R.id.iv_back_include);
		et_pola_name = (EditText) findViewById(R.id.et_pola_name);
		et_pola_number = (EditText) findViewById(R.id.et_pola_number);
		et_pola_phone = (EditText) findViewById(R.id.et_pola_phone);
		noScrollgridview = (com.thsw.work.custom.CamGridViewCoustom) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		mStartdot.setOnClickListener(this);
		mBt_confirm.setOnClickListener(this);
		mIv_back_include.setOnClickListener(this);
		// 默认为图上勾选
		mRbimgsketch.setChecked(true);
		mTitle_include.setText("地块认领");
		InitPopupWindown();
		spLogin_name = (String) SpUtils
				.get(PolaActivity.this, "login_name", "");
		dialog = DialogUtil.createLoadingDialog(PolaActivity.this, "加载中...",
				true, 0);
		dialog.show();
		/**
		 * 获取地块认领人的基本信息 姓名 身份证号 和联系方式
		 * */
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WebServiceBean bean = new WebServiceBean();
				bean.setUser(spLogin_name);
				SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
						CommonData.CLAIMANTNAME, CommonData.CLAIMANTACTION);
				String msgResult = bodyIn.getProperty(0).toString();
				Message msg = handle.obtainMessage();
				msg.obj = msgResult;
				msg.what = 0X11;
				handle.sendMessage(msg);
			}
		}).start();

	}

	/***
	 * InitPopupWindown弹出对话框进行选择相机或者相册进行选择
	 */
	public void InitPopupWindown() {

		pop = new PopupWindow(PolaActivity.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
				null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PolaActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							PolaActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(PolaActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/** 返回主界面 */
		case R.id.iv_back_include:
			finish();
			break;
		/** 进入勾画地图界面 **/
		case R.id.iv_startdot:
			if (mRbimgsketch.isChecked()) {
				/**
				 * 跳转地图勾画界面
				 */
				startActivityForResult(new Intent(PolaActivity.this,
						ImagdotActivity.class), IMGRESULT_MESSAGE);
			} else {
				/**
				 * 跳转GPS勾画地块
				 */
				startActivityForResult(new Intent(PolaActivity.this,
						GpsdotActivity.class), GPSRESULT_MESSAGE);
			}
			break;
		/** 点击确定进行插入地块 */
		case R.id.bt_confirm:
			if (returArea.equals("") && polygonWkt.equals("")) {
				ToastUtils.show(PolaActivity.this, "请先对地块进行编辑", 0);
			} else {
				if (switchString != null) {

					new Thread() {

						private String finalResult;

						@Override
						public void run() {
							// 判断返回的面积是否为空
							if (!returArea.equals("")) {
								finalResult = LogPassImgService();
							}

							// 判断polygonWkt（）是否为空如果为空说明没有删除地块，不为空则地块已经删除
							if (!polygonWkt.equals("")) {
								finalResult = DeletePolayService();
							}
							Message msg = handle.obtainMessage();
							msg.obj = finalResult;
							msg.what = 0X66;
							handle.sendMessage(msg);
						}
					}.start();

				} else {
					ToastUtils.show(PolaActivity.this, "请对地块进行拍照",
							Toast.LENGTH_LONG);
				}

			}

			break;
		}
	}

	/***
	 * 调用WebServie服务删除地块
	 * 
	 * @return
	 */
	private String DeletePolayService() {

		WebServiceBean bean = new WebServiceBean();
		bean.setListImageString(switchString);
		bean.setUserName(spLogin_name);
		bean.setPolygonWkt(polygonWkt);
		SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
				CommonData.DELETEPOLATANAME, CommonData.DELETEPOLATACTION);
		/** 删除地块结果 */
		String deleteResult = bodyIn.getProperty(0).toString();
		return deleteResult;

	}

	/**
	 * 调用WebService上传图片
	 * 
	 * @return
	 */
	private String LogPassImgService() {
		dialog = DialogUtil.createLoadingDialog(PolaActivity.this, "加载中...",
				true, 0);
		dialog.show();
		WebServiceBean bean = new WebServiceBean();
		bean.setListImageString(switchString);
		bean.setUserName(spLogin_name);
		bean.setDkxz(polyValue);
		bean.setArea(returArea);
		SoapObject bodyIn = WebServiceUtil.getServiceData(bean,
				CommonData.LONGPASSNAME, CommonData.LONGPASSSOAPACTION);
		// 获取返回的结果
		String mResult = bodyIn.getProperty(0).toString();
		return mResult;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
					String fileName = String
							.valueOf(System.currentTimeMillis());
					Bitmap bm = (Bitmap) data.getExtras().get("data");
					FileUtils.saveBitmap(bm, fileName);
					ImageItem takePhoto = new ImageItem();
					takePhoto.setBitmap(bm);
					Bimp.tempSelectBitmap.add(takePhoto);
					// 将Bitmap转换成字符串
					ByteArrayOutputStream bStream = new ByteArrayOutputStream();
					bm.compress(CompressFormat.PNG, 100, bStream);
					byte[] bytes = bStream.toByteArray();
					switchString = Base64.encodeToString(bytes, Base64.DEFAULT);
					// imagelist.add(string);
				}
				break;
			case IMGRESULT_MESSAGE:
				if (resultCode == RESULT_OK) {
					returArea = data.getStringExtra("area");
					polyValue = data.getStringExtra("value");
					polygonWkt = data.getStringExtra("polygonWkt");
					mEt_pola_area.setText(returArea + "亩");
				}
			case GPSRESULT_MESSAGE:
				if (resultCode == RESULT_OK) {
					returArea = data.getStringExtra("area");
					polyValue = data.getStringExtra("value");
					mEt_pola_area.setText(returArea + "亩");
				}
				break;
			default:
				break;
			}
		}
	}

	/***
	 * 相机所有方法
	 */
	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.camicm));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for (int i = 0; i < PublicWay.activityList.size(); i++) {
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			System.exit(0);
		}
		return true;
	}
	/***
	 * ** **结束
	 */
}
