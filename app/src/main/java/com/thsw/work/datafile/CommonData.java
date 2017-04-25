package com.thsw.work.datafile;

/**
 * 
 * @author Administrator
 * 
 */
public class CommonData {
	/** 汇总参数对象数据 */
	public static final String COLLECT_AREA_GV[] = { "水稻", "玉米", "花生", "大豆",
			"油菜", "小麦", "其他" };
	/** 汇总参数状态数据 */
	public static final String COLLECT_STATE_GV[] = { "面积", "产量", "灾害" };
	/** 全国Arcgis地图服务 */
	// public static String ARCGISURL =
	// "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer";
	public static String ARCGISURL = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineStreetCold/MapServer";

	/** 全国Arcgis地图服务 */
	// public static String BASEARCGISURL =
	// "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer";
	public static String BASEARCGISURL = "http://cache1.arcgisonline.cn/ArcGIS/rest/services/ChinaOnlineStreetCold/MapServer";

	/** 在线影像地图服务 */
	public static String YXARCGISURL = "http://124.207.115.125:6080/arcgis/rest/services/BasiImageMap/MapServer";
	// public static String YXARCGISURL =
	// "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer";

	/** 行政区域服务地图 */
	public static String XZQYARCGISURL = "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer";

	/** 3857地块边界矢量图地图服务 */
	public static String DIKUAIURL3857 = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap3857/MapServer";
	/** 3857地块高亮地图服务 */
	public static String DIKUAIHIGHtURL3857 = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap3857/MapServer/0";
	/** 3857地块墒情地图服务 */
	public static String DIKUAISQ3857 = "http://124.207.115.125:6080/arcgis/rest/services/WaterGroup_ZS_3857/MapServer";
	/** 3857地块长势地图服务 */
	public static String DIKUAIZS3857 = "http://124.207.115.125:6080/arcgis/rest/services/ZhsGroup3857/MapServer";

	/** 搜索市地图服务 */
	public static String SEARCHCITY = "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer/1";
	/** 搜索县地图服务 */
	public static String SEARCHCOUNTY = "http://124.207.115.125:6080/arcgis/rest/services/Coun_Pro/MapServer/0";
	/** 搜索乡镇地图服务 */
	public static String SEARCHVILLAGES = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap3857/MapServer/2";
	/** 搜索村地图服务 */
	public static String SEARCHVILLAGE = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap3857/MapServer/1";

	/** 4326地块边界矢量图地图服务 */
	public static String DIKUAIURL4326 = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap/MapServer";
	/** 4326地块高亮地图服务 */
	public static String DIKUAIHIGHtURL4326 = "http://124.207.115.125:6080/arcgis/rest/services/BasicVectorMap/MapServer/0";
	/** 4326动态影像服务 */
	public static String YINGXIANGURL4326 = "http://124.207.115.125:6080/arcgis/rest/services/SampleWorldCities/MapServer";

	/** 公共信息EndPoint */
	public static String ENDPOINT = "http://124.207.115.125/appservice/WebService1.asmx";
	/** 公共命名空间 */
	public static String NAMESPACE = "http://tempuri.org/";
	/** 登录信息调用的方法名称 */
	public static String LOGINNAME = "UserLoad";
	/** 登录信息的Soap Action */
	public static String LOGINACTION = "http://tempuri.org/UserLoad";

	/** 登录信息调用的方法名称 */
	public static String ALIASNNAME = "ExeAliasPush";
	/** 登录信息的Soap Action */
	public static String ALIASACTION = "http://tempuri.org/ExeAliasPush";

	/** 插入地块信息调用的方法名称 */
	public static String DKMETHODNAME = "InsertDK";
	/** 插入地块信息的Soap Action */
	public static String DKSOAPACTION = "http://tempuri.org/InsertDK";

	/** 注册信息调用的方法名称 */
	public static String REGISTERNAME = "XinUser";
	/** 注册信息的Soap Action */
	public static String REGISTERSOAPACTION = "http://tempuri.org/XinUser";

	/** 查询用户地块获取电话和姓名调用的方法名称 */
	public static String QueryDKNAME = "QueryUserByDk";
	/** 查询用户地块获取电话和姓名的Soap Action */
	public static String QueryDKSOAPACTION = "http://tempuri.org/QueryUserByDk";

	/** 查询用所属户地块调用的方法名称 */
	public static String QUERYUSERDKNAME = "QueryDkByUser";
	/** 查询用户所属地块的Soap Action */
	public static String QueryUSERSOAPACTION = "http://tempuri.org/QueryDkByUser";

	/** 上传地块图片的方法名称 */
	public static String LONGPASSNAME = "InsertDk_ImageFileStringAreaFG_OneDk";
	/** 上传地块的Soap Action */
	public static String LONGPASSSOAPACTION = "http://tempuri.org/InsertDk_ImageFileStringAreaFG_OneDk";

	/** 个人中心的方法名称 */
	public static String USERNAME = "UserLoadGetRes";
	/** 个人中心的Soap Action */
	public static String USERAPACTION = "http://tempuri.org/UserLoadGetRes";

	/** 上报信息的方法名称 */
	public static String APPEARNAME = "InsertSBResult";
	/** 上报信息的Soap Action */
	public static String APPEARACTION = "http://tempuri.org/InsertSBResult";

	/** 更新上报地块的方法名称 */
	public static String UPDATEMSGNAME = "UpDateSBResult";
	/** 更新上报地块的Soap Action */
	public static String UPDATEMSGACTION = "http://tempuri.org/UpDateSBResult";

	/** 获取需要核实的地块信息的方法名称 */
	public static String VERIFYNAMEMSG = "QueryTSResult";
	/** 获取需要核实的地块信息的Soap Action */
	public static String VERIFYACTIONMSG = "http://tempuri.org/QueryTSResult";

	/** 获取需要核实的地块的方法名称 */
	public static String VERIFYNAMEPLOT = "QueryTSDKResult";
	/** 获取需要核实的地块的Soap Action */
	public static String VERIFYACTIONPLOT = "http://tempuri.org/QueryTSDKResult";

	/** 获取认领人的基本信息的方法名称 */
	public static String CLAIMANTNAME = "QueryUserInfoByUser";
	/** 获取认领人的基本信息的Soap Action */
	public static String CLAIMANTACTION = "http://tempuri.org/QueryUserInfoByUser";

	/** 根据名称获取行政信息的方法名称 */
	public static String SEARCHANAME = "QueryMcAndCodeByXzm";
	/** 根据名称获取行政信息的Soap Action */
	public static String SEARCHAACTION = "http://tempuri.org/QueryMcAndCodeByXzm";

	/** 查询面积统计结果的方法名称 */
	public static String QUERYAREANAME = "QueryAreaBy";
	/** 查询面积统计结果的Soap Action */
	public static String QUERYAREAACTION = "http://tempuri.org/QueryAreaBy";
	
	
	/** 查询面积统计结果的方法名称 */
	public static String DELETEPOLATANAME = "DelDkRecord";
	/** 查询面积统计结果的Soap Action */
	public static String DELETEPOLATACTION = "http://tempuri.org/DelDkRecord";

	/** Arcgis地图墒情模式 */
	public static String SHANGQING_TEXT = "墒情";
	/** Arcgis地图标准模式 */
	public static String STANDARD_TEXT = "标准";
	/** Arcgis地图长势模式 */
	public static String ZHANGSHI_TEXT = "长势";
	/** Arcgis地图肥料模式 */
	public static String FEILIAO_TEXT = "肥料";
	/** Arcgis地图病虫害模式 */
	public static String BINGCHONGHAI_TEXT = "病虫害";
	/** 农情主题 */
	public static String SPECIAL_LISTVIEW[] = { "标准", "长势", "墒情", "肥料", "病虫害",
			"旱灾" };

	/** 地块状态信息 */
	public static String DetailsMessage[] = { "长势情况", "土壤墒情", "土壤肥情", "病虫害情况" };
	/** 用户信息 */
	public static String USERMESSAGE[] = { "用户名", "姓名", "性别", "出生年月", "身份证号",
			"手机号码" };


	public static  String BASEURL="http://192.168.1.162:8080/WebProject/";

	public static class API{
		public static final String LOGINURL=BASEURL+"servlet/UserLoadServlet";//登录接口
	}

}
