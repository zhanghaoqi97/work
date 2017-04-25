package com.thsw.work.bean;

import java.io.Serializable;

public class PlotDataBean implements Serializable {

	private String longitude;
	private String latitude;

	public PlotDataBean(String longitude, String latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "PlotDataBean [longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}

}
