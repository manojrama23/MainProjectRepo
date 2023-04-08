package com.smart.rct.common.models;

public class SiteModel {
	private String eNodeBName;
	private String eNodeBSW;
	private String fsuSW;
	private String vDUSW;
	private String softWareRelease;
	private String fuzeProjId;
	private String market;
	


	public SiteModel(String eNodeBName, String eNodeBSW, String fsuSW, String vDUSW,String softWareRelease,String fuzeProjId) {
		
		this.eNodeBName = eNodeBName;
		this.eNodeBSW = eNodeBSW;
		this.fsuSW = fsuSW;
		this.vDUSW = vDUSW;
		this.vDUSW = fuzeProjId;
	}
	
	public String getMarket() {
		return market;
	}



	public void setMarket(String market) {
		this.market = market;
	}

	
	public String getFuzeProjId() {
		return fuzeProjId;
	}



	public void setFuzeProjId(String fuzeProjId) {
		this.fuzeProjId = fuzeProjId;
	}



	public String geteNodeBSW() {
		return eNodeBSW;
	}





	public void seteNodeBSW(String eNodeBSW) {
		this.eNodeBSW = eNodeBSW;
	}





	public String getsoftWareRelease() {
		return softWareRelease;
	}



	public void setsoftWareRelease(String softWareRelease) {
		this.softWareRelease = softWareRelease;
	}



	public String getFsuSW() {
		return fsuSW;
	}





	public void setFsuSW(String fsuSW) {
		this.fsuSW = fsuSW;
	}





	public String getvDUSW() {
		return vDUSW;
	}





	public void setvDUSW(String vDUSW) {
		this.vDUSW = vDUSW;
	}

	

	
	
	
	
	public String geteNodeBName() {
		return eNodeBName;
	}

	public void seteNodeBName(String eNodeBName) {
		this.eNodeBName = eNodeBName;
	}
}
