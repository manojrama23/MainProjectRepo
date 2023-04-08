package com.smart.rct.postmigration.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

import com.smart.rct.migration.entity.RunTestEntity;


public class ReportsModel {

	
	private Integer id;

	
	private Date startDate;
	

	private Date endDate;
	
	private String toDate;
	

	private String fromDate;
	
	private String ciqName;
	

	private String programeName;
	

	private int customerId;
	

	private String market;
	
	private String neName;
	

	private String enbId;
	
	
	private String userName;
	
	
	private String preMigEnvStatus;
	
	
	private String preMigGrowStatus;
	
	
	private String preMigCommStatus;

	
	
	private String preMigEndcStatus;
	
	
	private String neGrowPnpStatus;
	
	
	private String neGrowenbStatus;
	

	private String neGrowCellStatus;
	

	private String neGrowAuCacellStatus;
	

	private String neGrowAuStatus;
	

	private String migCaCellStatus;
	

	private String migAuStatus;
	

	private String migRfStatus;
	
	
	private String migCommStatus;

	private String migAcpfStatus;

	private String migCslStatus;
	
	
	private String migEndcStatus;
	
	
	private String migPreCheckStatus;
	

	private String migCutoverStatus;
	
	
	private String migRollbackStatus;
	
	
	private String migAnchorStatus;
	

	private String migNbrStatus;
	

	private String postAcpfAuditStatus;


	private String postMigAtpStatus;
	

	private String postMigAudiStatus;
	
	private String postAupfStatus;
	
	private String postMigVduStatus;
	
	private String postMigEnbAudiStatus;

	private String postAuAuditStatus;

	private String postEndcAuditStatus;
	
	private String postMigFsuAuditStatus;
	
	private String siteDataStatus;

	private String remarks;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}
	
	public String getCiqName() {
		return ciqName;
	}

	public void setCiqName(String ciqName) {
		this.ciqName = ciqName;
	}

	public String getPreMigEnvStatus() {
		return preMigEnvStatus;
	}

	public void setPreMigEnvStatus(String preMigEnvStatus) {
		this.preMigEnvStatus = preMigEnvStatus;
	}

	public String getPreMigGrowStatus() {
		return preMigGrowStatus;
	}

	public void setPreMigGrowStatus(String preMigGrowStatus) {
		this.preMigGrowStatus = preMigGrowStatus;
	}

	public String getPreMigCommStatus() {
		return preMigCommStatus;
	}

	public void setPreMigCommStatus(String preMigCommStatus) {
		this.preMigCommStatus = preMigCommStatus;
	}

	public String getPreMigEndcStatus() {
		return preMigEndcStatus;
	}

	public void setPreMigEndcStatus(String preMigEndcStatus) {
		this.preMigEndcStatus = preMigEndcStatus;
	}

	public String getNeGrowPnpStatus() {
		return neGrowPnpStatus;
	}

	public void setNeGrowPnpStatus(String neGrowPnpStatus) {
		this.neGrowPnpStatus = neGrowPnpStatus;
	}

	public String getNeGrowenbStatus() {
		return neGrowenbStatus;
	}

	public void setNeGrowenbStatus(String neGrowenbStatus) {
		this.neGrowenbStatus = neGrowenbStatus;
	}

	public String getNeGrowCellStatus() {
		return neGrowCellStatus;
	}

	public void setNeGrowCellStatus(String neGrowCellStatus) {
		this.neGrowCellStatus = neGrowCellStatus;
	}

	public String getNeGrowAuCacellStatus() {
		return neGrowAuCacellStatus;
	}

	public void setNeGrowAuCacellStatus(String neGrowAuCacellStatus) {
		this.neGrowAuCacellStatus = neGrowAuCacellStatus;
	}

	public String getNeGrowAuStatus() {
		return neGrowAuStatus;
	}

	public void setNeGrowAuStatus(String neGrowAuStatus) {
		this.neGrowAuStatus = neGrowAuStatus;
	}

	public String getMigAuStatus() {
		return migAuStatus;
	}

	public void setMigAuStatus(String migAuStatus) {
		this.migAuStatus = migAuStatus;
	}

	public String getMigRfStatus() {
		return migRfStatus;
	}

	public void setMigRfStatus(String migRfStatus) {
		this.migRfStatus = migRfStatus;
	}

	public String getMigCommStatus() {
		return migCommStatus;
	}

	public void setMigCommStatus(String migCommStatus) {
		this.migCommStatus = migCommStatus;
	}

	public String getMigAcpfStatus() {
		return migAcpfStatus;
	}

	public void setMigAcpfStatus(String migAcpfStatus) {
		this.migAcpfStatus = migAcpfStatus;
	}

	public String getMigCslStatus() {
		return migCslStatus;
	}

	public void setMigCslStatus(String migCslStatus) {
		this.migCslStatus = migCslStatus;
	}

	public String getMigEndcStatus() {
		return migEndcStatus;
	}

	public void setMigEndcStatus(String migEndcStatus) {
		this.migEndcStatus = migEndcStatus;
	}

	public String getMigPreCheckStatus() {
		return migPreCheckStatus;
	}

	public void setMigPreCheckStatus(String migPreCheckStatus) {
		this.migPreCheckStatus = migPreCheckStatus;
	}

	public String getMigCutoverStatus() {
		return migCutoverStatus;
	}

	public void setMigCutoverStatus(String migCutoverStatus) {
		this.migCutoverStatus = migCutoverStatus;
	}

	public String getMigRollbackStatus() {
		return migRollbackStatus;
	}

	public void setMigRollbackStatus(String migRollbackStatus) {
		this.migRollbackStatus = migRollbackStatus;
	}

	public String getMigAnchorStatus() {
		return migAnchorStatus;
	}

	public void setMigAnchorStatus(String migAnchorStatus) {
		this.migAnchorStatus = migAnchorStatus;
	}

	public String getMigNbrStatus() {
		return migNbrStatus;
	}

	public void setMigNbrStatus(String migNbrStatus) {
		this.migNbrStatus = migNbrStatus;
	}

	public String getPostAcpfAuditStatus() {
		return postAcpfAuditStatus;
	}

	public void setPostAcpfAuditStatus(String postAcpfAuditStatus) {
		this.postAcpfAuditStatus = postAcpfAuditStatus;
	}

	public String getPostMigAtpStatus() {
		return postMigAtpStatus;
	}

	public void setPostMigAtpStatus(String postMigAtpStatus) {
		this.postMigAtpStatus = postMigAtpStatus;
	}

	public String getPostMigAudiStatus() {
		return postMigAudiStatus;
	}

	public void setPostMigAudiStatus(String postMigAudiStatus) {
		this.postMigAudiStatus = postMigAudiStatus;
	}

	public String getPostAupfStatus() {
		return postAupfStatus;
	}

	public void setPostAupfStatus(String postAupfStatus) {
		this.postAupfStatus = postAupfStatus;
	}

	public String getPostMigVduStatus() {
		return postMigVduStatus;
	}

	public void setPostMigVduStatus(String postMigVduStatus) {
		this.postMigVduStatus = postMigVduStatus;
	}

	public String getPostMigEnbAudiStatus() {
		return postMigEnbAudiStatus;
	}

	public void setPostMigEnbAudiStatus(String postMigEnbAudiStatus) {
		this.postMigEnbAudiStatus = postMigEnbAudiStatus;
	}

	public String getPostAuAuditStatus() {
		return postAuAuditStatus;
	}

	public void setPostAuAuditStatus(String postAuAuditStatus) {
		this.postAuAuditStatus = postAuAuditStatus;
	}

	public String getPostEndcAuditStatus() {
		return postEndcAuditStatus;
	}

	public void setPostEndcAuditStatus(String postEndcAuditStatus) {
		this.postEndcAuditStatus = postEndcAuditStatus;
	}

	public String getPostMigFsuAuditStatus() {
		return postMigFsuAuditStatus;
	}

	public void setPostMigFsuAuditStatus(String postMigFsuAuditStatus) {
		this.postMigFsuAuditStatus = postMigFsuAuditStatus;
	}

	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSiteDataStatus() {
		return siteDataStatus;
	}

	public void setSiteDataStatus(String siteDataStatus) {
		this.siteDataStatus = siteDataStatus;
	}

	public String getEnbId() {
		return enbId;
	}

	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}


	public String getProgrameName() {
		return programeName;
	}

	public void setProgrameName(String programeName) {
		this.programeName = programeName;
	}

	public String getMigCaCellStatus() {
		return migCaCellStatus;
	}

	public void setMigCaCellStatus(String migCaCellStatus) {
		this.migCaCellStatus = migCaCellStatus;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

}
