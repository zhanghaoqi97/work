package com.thsw.work.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.thsw.work.bean.WebServiceBean;
import com.thsw.work.datafile.CommonData;

/**
 * WebService工具类
 * @author pc
 *
 */
public class WebServiceUtil {
	public static SoapObject getServiceData(WebServiceBean bean,String name,String action){
		SoapObject sob = new SoapObject(CommonData.NAMESPACE,
				name);
		if(bean.getUserName()!=null){
			sob.addProperty("userName",bean.getUserName());
		}
		if(bean.getPwd()!=null){
			sob.addProperty("pwd",bean.getPwd());
		}
		if(bean.getUser()!=null){
			sob.addProperty("user", bean.getUser());
		}
		if(bean.getArea()!=null){
			sob.addProperty("area", bean.getArea());
		}
		if(bean.getBch()!=null){
			sob.addProperty("bch", bean.getBch());
		}
		if(bean.getBorn()!=null){
			sob.addProperty("born", bean.getBorn());
		}
		if(bean.getCard()!=null){
			sob.addProperty("card", bean.getCard());
		}
		if(bean.getSex()!=null){
			sob.addProperty("sex", bean.getSex());
		}
		if(bean.getDK_WKT()!=null){
			sob.addProperty("DK_WKT", bean.getDK_WKT());
		}
		if(bean.getDklx()!=null){
			sob.addProperty("dklx", bean.getDK_WKT());
		}
		if(bean.getOther()!=null){
			sob.addProperty("other", bean.getOther());
		}
		if(bean.getDkxz()!=null){
			sob.addProperty("dkxz", bean.getDkxz());
		}
		if(bean.getId()!=null){
			sob.addProperty("id", bean.getId());
		}
		if(bean.getListImageString()!=null){
			sob.addProperty("ListImageString", bean.getListImageString());
		}
		if(bean.getPhone()!=null){
			sob.addProperty("phone", bean.getPhone());
		}
		if(bean.getPolygonWkt()!=null){
			sob.addProperty("polygonWkt", bean.getPolygonWkt());
		}
		if(bean.getSq()!=null){
			sob.addProperty("sq", bean.getSq());
		}
		if(bean.getXZDM()!=null){
			sob.addProperty("XZDM", bean.getXZDM());
		}
		if(bean.getXZMC()!=null){
			sob.addProperty("XZMC", bean.getXZMC());
		}
		if(bean.getZsqk()!=null){
			sob.addProperty("zsqk", bean.getZsqk());
		}
		if(bean.getZwlx()!=null){
			sob.addProperty("zwlx", bean.getZwlx());
		}
		if(bean.getPro()!=null){
			sob.addProperty("pro",bean.getPro());
		}
		if(bean.getCity()!=null){
			sob.addProperty("city",bean.getCity());
		}
		if(bean.getCoun()!=null){
			sob.addProperty("coun",bean.getCoun());
		}
		if(bean.getTown()!=null){
			sob.addProperty("town",bean.getTown());
		}
		if(bean.getVill()!=null){
			sob.addProperty("vill",bean.getVill());
		}
		if(bean.getZWLX()!=null){
			sob.addProperty("ZWLX", bean.getZWLX());
		}
		if(bean.getAlias()!=null){
			sob.addProperty("alias", bean.getAlias());
		}
		
		// 生成webService方法的soap请求信息，并制定soap的版本
		SoapSerializationEnvelope evEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		evEnvelope.bodyOut = sob;
		// 设置是否调用的是dotnet开发的webService
		evEnvelope.dotNet = true;
		// 等价于envelope.bodyout=rpc
		evEnvelope.setOutputSoapObject(sob);
		// 通过Wsdl地址查询资源服务 创建连接
		HttpTransportSE transportSE = new HttpTransportSE(CommonData.ENDPOINT);
		// 调用webService
		try {
			transportSE.call(action, evEnvelope);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("TAG", e.toString());
		}
		// 获取返回的数据
		SoapObject bodyIn = (SoapObject) evEnvelope.bodyIn;
//		twoBodyin = bodyIn.getProperty(0).toString();
		return bodyIn;
	}
	
}
