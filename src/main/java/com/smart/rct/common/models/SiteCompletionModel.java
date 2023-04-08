package com.smart.rct.common.models;

import java.util.List;

public class SiteCompletionModel {
	private String siteName;
	private String eNodeBName;
	private String eNodeBSW;
	private String fsuSW;
	private String vDUSW;
	private String currCBANDIntegStatus;
	private String finalCBANDIntegStatus;
	//private String preChkPass;
	//private String postChkPass;
	//private String crossAnchor;
	//private String concFSUInteg;
	//private String vDUInstCorrect;
	private String reportDate;
	private String neName;
	private String neId;
	private String project;
	private String softWareRelease;
	private String market;
	private String integrationType;
	private String ciCompletionStatus;
	private String finalIntegrationStatus;
	//private String alarmFree;
	//private String auCellsReqperMarket;

	//private String twampPingTestRepAttached;
	//private String basicSanityTestAllPass;
	//private String rfAtpStarted;
	private String remarks;
	private String resAuditIssueCheck;
	private String isCancellationReport;

	public String getIsCancellationReport() {
		return isCancellationReport;
	}

	public void setIsCancellationReport(String isCancellationReport) {
		this.isCancellationReport = isCancellationReport;
	}

	private List<TimeLineDetailsModel> timeLineDetails;
	private List<CategoryDetailsModel> categoryDetails;
	private List<TroubleshootTimelineDetailsModel> troubleshootTimelineDetails;
	private List<SiteCarriers> carriers;
	private List<AuditSummaryModel> postAuditIssues;
	private List<CriticalCheckDetails> checkDetails;

	
	//private String likeForLikeStatus;
	//private String incrementalStatus;
	//private String cellAdminStateIsPerCiq;
	//private String atpAllPass;
	//private String allRetsScanned;
	//private String allRetsLabeled;
	//private String followUpRequired;
	private String siteReportStatus;
	
		private String typeOfEffort;
		//private String fsuBypass;
		private String lteCommComp;
		private String lteCBRSCommComp;
		private String lteLAACommComp;
		private String lteOpsATP;
		private String lteLAAOpsATP;
		private String lteCBRSOpsAtp;
		private String tcReleased;
		private String finalIntegStatus;
		private String fsuIntegBypass;
		private String fsuIntegMultiplex;
		private String mmCommComp;
		private String mmOpsATP;
		private String dssCommComp;
		private String dssOpsATP;
		private String ovTicketNum;
	//	private String checkRSSI;
	//	private String checkVSWR;
		private String FuzeProjId;
		private String vendorType;
		//private String checkRSSIImbl;
	//	private String checkFIBER;
	//	private String checkSFP;
		//private String preMigrationHealth;
		//private String preExistingAlarm;
		//private String retRecived;
		//private String onsiteConfirm;
		

		

		private String userName;
		
		public List<CriticalCheckDetails> getCheckDetails() {
			return checkDetails;
		}

		public void setCheckDetails(List<CriticalCheckDetails> checkDetails) {
			this.checkDetails = checkDetails;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		

		public String getFuzeProjId() {
			return FuzeProjId;
		}

		public void setFuzeProjId(String fuzeProjId) {
			FuzeProjId = fuzeProjId;
		}
		
		public String getVendorType() {
			return vendorType;
		}

		public void setVendorType(String vendor) {
			vendorType = vendor;
		}
		
		public String getLteCommComp() {
			return lteCommComp;
		}

		public void setLteCommComp(String lteCommComp) {
			this.lteCommComp = lteCommComp;
		}

		public String getLteCBRSCommComp() {
			return lteCBRSCommComp;
		}

		public void setLteCBRSCommComp(String lteCBRSCommComp) {
			this.lteCBRSCommComp = lteCBRSCommComp;
		}

		public String getLteLAACommComp() {
			return lteLAACommComp;
		}

		public void setLteLAACommComp(String lteLAACommComp) {
			this.lteLAACommComp = lteLAACommComp;
		}

		public String getLteOpsATP() {
			return lteOpsATP;
		}

		public void setLteOpsATP(String lteOpsATP) {
			this.lteOpsATP = lteOpsATP;
		}

		public String getLteLAAOpsATP() {
			return lteLAAOpsATP;
		}

		public void setLteLAAOpsATP(String lteLAAOpsATP) {
			this.lteLAAOpsATP = lteLAAOpsATP;
		}

		public String getLteCBRSOpsAtp() {
			return lteCBRSOpsAtp;
		}

		public void setLteCBRSOpsAtp(String lteCBRSOpsAtp) {
			this.lteCBRSOpsAtp = lteCBRSOpsAtp;
		}

		public String getTcReleased() {
			return tcReleased;
		}

		public void setTcReleased(String tcReleased) {
			this.tcReleased = tcReleased;
		}

		public String getFinalIntegStatus() {
			return finalIntegStatus;
		}

		public void setFinalIntegStatus(String finalIntegStatus) {
			this.finalIntegStatus = finalIntegStatus;
		}

		public String getFsuIntegBypass() {
			return fsuIntegBypass;
		}

		public void setFsuIntegBypass(String fsuIntegBypass) {
			this.fsuIntegBypass = fsuIntegBypass;
		}

		public String getFsuIntegMultiplex() {
			return fsuIntegMultiplex;
		}

		public void setFsuIntegMultiplex(String fsuIntegMultiplex) {
			this.fsuIntegMultiplex = fsuIntegMultiplex;
		}

		public String getMmCommComp() {
			return mmCommComp;
		}

		public void setMmCommComp(String mmCommComp) {
			this.mmCommComp = mmCommComp;
		}

		public String getMmOpsATP() {
			return mmOpsATP;
		}

		public void setMmOpsATP(String mmOpsATP) {
			this.mmOpsATP = mmOpsATP;
		}

		public String getDssCommComp() {
			return dssCommComp;
		}

		public void setDssCommComp(String dssCommComp) {
			this.dssCommComp = dssCommComp;
		}

		public String getDssOpsATP() {
			return dssOpsATP;
		}

		public void setDssOpsATP(String dssOpsATP) {
			this.dssOpsATP = dssOpsATP;
		}

		public String getOvTicketNum() {
			return ovTicketNum;
		}

		public void setOvTicketNum(String ovTicketNum) {
			this.ovTicketNum = ovTicketNum;
		}
	//private String vDUDay2Complete;
		
		
		public String getTypeOfEffort() {
			return typeOfEffort;
		}

		public void setTypeOfEffort(String typeOfEffort) {
			this.typeOfEffort = typeOfEffort;
		}

		

		
	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String geteNodeBName() {
		return eNodeBName;
	}

	public void seteNodeBName(String eNodeBName) {
		this.eNodeBName = eNodeBName;
	}
	public String geteNodeBSW() {
		return eNodeBSW;
	}
	public void seteNodeBSW(String eNodeBSW) {
		this.eNodeBSW = eNodeBSW;
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
	public String getCurrCBANDIntegStatus() {
		return currCBANDIntegStatus;
	}
	public void setCurrCBANDIntegStatus(String currCBANDIntegStatus) {
		this.currCBANDIntegStatus = currCBANDIntegStatus;
	}
	public String getFinalCBANDIntegStatus() {
		return finalCBANDIntegStatus;
	}
	public void setFinalCBANDIntegStatus(String finalCBANDIntegStatus) {
		this.finalCBANDIntegStatus = finalCBANDIntegStatus;
	}
	
	
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getSoftWareRelease() {
		return softWareRelease;
	}

	public void setSoftWareRelease(String softWareRelease) {
		this.softWareRelease = softWareRelease;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}

	public String getCiCompletionStatus() {
		return ciCompletionStatus;
	}
	
	

	

	public void setCiCompletionStatus(String ciCompletionStatus) {
		this.ciCompletionStatus = ciCompletionStatus;
	}

	public String getFinalIntegrationStatus() {
		return finalIntegrationStatus;
	}

	public void setFinalIntegrationStatus(String finalIntegrationStatus) {
		this.finalIntegrationStatus = finalIntegrationStatus;
	}
	

	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<TimeLineDetailsModel> getTimeLineDetails() {
		return timeLineDetails;
	}

	public void setTimeLineDetails(List<TimeLineDetailsModel> timeLineDetails) {
		this.timeLineDetails = timeLineDetails;
	}

	public List<TroubleshootTimelineDetailsModel> getTroubleshootTimelineDetails() {
		return troubleshootTimelineDetails;
	}

	public void setTroubleshootTimelineDetails(List<TroubleshootTimelineDetailsModel> troubleshootTimelineDetails) {
		this.troubleshootTimelineDetails = troubleshootTimelineDetails;
	}

	
	public List<CategoryDetailsModel> getCategoryDetails() {
		return categoryDetails;
	}

	public void setCategoryDetails(List<CategoryDetailsModel> categoryDetails) {
		this.categoryDetails = categoryDetails;
	}

	

	public List<SiteCarriers> getCarriers() {
		return carriers;
	}

	public void setCarriers(List<SiteCarriers> carriers) {
		this.carriers = carriers;
	}

//	public String getLikeForLikeStatus() {
//		return likeForLikeStatus;
//	}
//
//	public void setLikeForLikeStatus(String likeForLikeStatus) {
//		this.likeForLikeStatus = likeForLikeStatus;
//	}
//
//	public String getIncrementalStatus() {
//		return incrementalStatus;
//	}
//
//	public void setIncrementalStatus(String incrementalStatus) {
//		this.incrementalStatus = incrementalStatus;
//	}

	

	
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteReportStatus() {
		return siteReportStatus;
	}

	public void setSiteReportStatus(String siteReportStatus) {
		this.siteReportStatus = siteReportStatus;
	}

	public List<AuditSummaryModel> getPostAuditIssues() {
		return postAuditIssues;
	}

	public void setPostAuditIssues(List<AuditSummaryModel> postAuditIssues) {
		this.postAuditIssues = postAuditIssues;
	}

	public String getResAuditIssueCheck() {
		return resAuditIssueCheck;
	}

	public void setResAuditIssueCheck(String resAuditIssueCheck) {
		this.resAuditIssueCheck = resAuditIssueCheck;
	}
	

}