package com.thsw.work.bean;

/** 地块信息状态javaBean */
public class DetailMessage {
	private String TBMJ;
	private String WATER;
	private String ZHS;
	private String BinCH;
	private String W_TIME;
	private String Z_TIME;
	private String B_TIME;
	private String N_FQ;
	private String N_TIME;
	private String PRO_NAME;
	private String CITY_NAME;
	private String COUN_NAME;
	private String TOWN_NAME;
	private String VILL_NAME;

	public DetailMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DetailMessage(String tBMJ, String wATER, String zHS, String binCH,
			String w_TIME, String z_TIME, String b_TIME, String n_FQ,
			String n_TIME, String pRO_NAME, String cITY_NAME, String cOUN_NAME,
			String tOWN_NAME, String vILL_NAME) {
		super();
		TBMJ = tBMJ;
		WATER = wATER;
		ZHS = zHS;
		BinCH = binCH;
		W_TIME = w_TIME;
		Z_TIME = z_TIME;
		B_TIME = b_TIME;
		N_FQ = n_FQ;
		N_TIME = n_TIME;
		PRO_NAME = pRO_NAME;
		CITY_NAME = cITY_NAME;
		COUN_NAME = cOUN_NAME;
		TOWN_NAME = tOWN_NAME;
		VILL_NAME = vILL_NAME;
	}

	public String getTBMJ() {
		return TBMJ;
	}

	public void setTBMJ(String tBMJ) {
		TBMJ = tBMJ;
	}

	public String getWATER() {
		return WATER;
	}

	public void setWATER(String wATER) {
		WATER = wATER;
	}

	public String getZHS() {
		return ZHS;
	}

	public void setZHS(String zHS) {
		ZHS = zHS;
	}

	public String getBinCH() {
		return BinCH;
	}

	public void setBinCH(String binCH) {
		BinCH = binCH;
	}

	public String getW_TIME() {
		return W_TIME;
	}

	public void setW_TIME(String w_TIME) {
		W_TIME = w_TIME;
	}

	public String getZ_TIME() {
		return Z_TIME;
	}

	public void setZ_TIME(String z_TIME) {
		Z_TIME = z_TIME;
	}

	public String getB_TIME() {
		return B_TIME;
	}

	public void setB_TIME(String b_TIME) {
		B_TIME = b_TIME;
	}

	public String getN_FQ() {
		return N_FQ;
	}

	public void setN_FQ(String n_FQ) {
		N_FQ = n_FQ;
	}

	public String getN_TIME() {
		return N_TIME;
	}

	public void setN_TIME(String n_TIME) {
		N_TIME = n_TIME;
	}

	public String getPRO_NAME() {
		return PRO_NAME;
	}

	public void setPRO_NAME(String pRO_NAME) {
		PRO_NAME = pRO_NAME;
	}

	public String getCITY_NAME() {
		return CITY_NAME;
	}

	public void setCITY_NAME(String cITY_NAME) {
		CITY_NAME = cITY_NAME;
	}

	public String getCOUN_NAME() {
		return COUN_NAME;
	}

	public void setCOUN_NAME(String cOUN_NAME) {
		COUN_NAME = cOUN_NAME;
	}

	public String getTOWN_NAME() {
		return TOWN_NAME;
	}

	public void setTOWN_NAME(String tOWN_NAME) {
		TOWN_NAME = tOWN_NAME;
	}

	public String getVILL_NAME() {
		return VILL_NAME;
	}

	public void setVILL_NAME(String vILL_NAME) {
		VILL_NAME = vILL_NAME;
	}

	@Override
	public String toString() {
		return "DetailMessage [TBMJ=" + TBMJ + ", WATER=" + WATER + ", ZHS="
				+ ZHS + ", BinCH=" + BinCH + ", W_TIME=" + W_TIME + ", Z_TIME="
				+ Z_TIME + ", B_TIME=" + B_TIME + ", N_FQ=" + N_FQ
				+ ", N_TIME=" + N_TIME + ", PRO_NAME=" + PRO_NAME
				+ ", CITY_NAME=" + CITY_NAME + ", COUN_NAME=" + COUN_NAME
				+ ", TOWN_NAME=" + TOWN_NAME + ", VILL_NAME=" + VILL_NAME + "]";
	}

}
