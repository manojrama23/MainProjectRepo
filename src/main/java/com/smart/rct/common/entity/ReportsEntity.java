package com.smart.rct.common.entity;

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

/**
 * @author tapan
 *
 */
@Entity
@Table(name = "REPORTS_DETAILS")
public class ReportsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "REPORTS_GENERATION_DATE")
	private Date startDate;

	@Column(name = "CIQ_NAME")
	private String ciqName;
	
	@Column(name = "PROGRAME_NAME")
	private String programeName;
	
	@Column(name = "CUSTOMER_ID")
	private int customerId;
	
	@Column(name = "MARKET")
	private String market;
	
	@Column(name = "NE_NAME")
	private String neName;
	
	@Column(name = "ENBID")
	private String enbId;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "PRE_MIG_ENV")
	private String preMigEnvStatus;
	
	@Column(name = "PRE_MIG_ENV_GEN_TIME")
	private Date preMigEnvGenTime;

	@Column(name = "PRE_MIG_GROW_TEMPLATE")
	private String preMigGrowStatus;
	
	@Column(name = "PRE_MIG_GROW_GEN_TIME")
	private Date preMigGrowGenTime;
	
	@Column(name = "PRE_MIG_COMMISSION_SCRIPT")
	private String preMigCommStatus;
	
	@Column(name = "PRE_MIG_COMMISSION_GEN_TIME")
	private Date preMigCommGenTime;
	
	@Column(name = "PRE_MIG_ENDC")
	private String preMigEndcStatus;
	
	@Column(name = "PRE_MIG_ENDC_GEN_TIME")
	private Date preMigEndcGenTime;
	
	@Column(name = "PRE_MIG_RF_SCRIPT")
	private String preMigRfScriptStatus;
	
	@Column(name = "PRE_MIG_RF_GEN_TIME")
	private Date preMigRfGenTime;

	@Column(name = "NE_GROW_PNP")
	private String neGrowPnpStatus;
	
	@Column(name = "NE_GROW_PNP_GEN_TIME")
	private Date neGrowPnpgenTime;
	
	@Column(name = "NE_GROW_ENB")
	private String neGrowenbStatus;
	
	@Column(name = "NE_GROW_ENB_GEN_TIME")
	private Date neGrowenbGenTime;
	
	@Column(name = "NE_GROW_CELL")
	private String neGrowCellStatus;
	
	@Column(name = "NE_GROW_CELL_GEN_TIME")
	private Date neGrowCellGenTime;
	
	@Column(name = "NE_GROW_AUCaCell")
	private String neGrowAuCacellStatus;
	
	@Column(name = "NE_GROW_AUCaCell_GEN_TIME")
	private Date neGrowAuCacellGenTime;
	
	@Column(name = "NE_GROW_AU")
	private String neGrowAuStatus;
	
	@Column(name = "NE_GROW_AU_GEN_TIME")
	private Date neGrowAuGenTime;
	
	@Column(name = "MIG_AU")
	private String migAuStatus;
	
	@Column(name = "MIG_AU_GEN_TIME")
	private Date migAuGenTime;
	
	@Column(name = "MIG_RF")
	private String migRfStatus;
	
	@Column(name = "MIG_RF_GEN_TIME")
	private Date migRfGenTime;
	
	@Column(name = "MIG_COMMISION_SCRIPT")
	private String migCommStatus;
	
	@Column(name = "MIG_COMMISION_GEN_TIME")
	private Date migCommGenTime;
	
	@Column(name = "MIG_ACPF_A1A2")
	private String migAcpfStatus;
	
	@Column(name = "MIG_ACPF_A1A2_GEN_TIME")
	private Date  migAcpfGenTime;
	
	@Column(name = "MIG_CSL")
	private String migCslStatus;
	
	@Column(name = "MIG_CSL_GEN_TIME")
	private Date migCslGenTime;
	
	@Column(name = "MIG_ENDC_X2")
	private String migEndcStatus;
	
	@Column(name = "MIG_ENDC_X2_GEN_TIME")
	private Date migEndcGenTime;
	
	@Column(name = "MIG_PRE_CHECK_RF")
	private String migPreCheckStatus;
	
	@Column(name = "MIG_PRE_CHECK_RF_GEN_TIME")
	private Date migPreCheckGenTime;
	
	@Column(name = "MIG_CUTOVER_RF")
	private String migCutoverStatus;
	
	@Column(name = "MIG_CUTOVER_RF_GEN_TIME")
	private Date migCutoverGenTime;
	
	@Column(name = "MIG_ROLLBACK_RF")
	private String migRollbackStatus;
	
	@Column(name = "MIG_ROLLBACK_RF_GEN_TIME")
	private Date migRollbackGenTime;
	
	@Column(name = "MIG_ANCHOR_CSL")
	private String migAnchorStatus;
	
	@Column(name = "MIG_ANCHOR_CSL_GEN_TIME")
	private Date migAnchorGenTime;
	
	@Column(name = "MIG_NBR")
	private String migNbrStatus;
	
	@Column(name = "MIG_NBR_GEN_TIME")
	private Date migNbrGenTime;
	
	@Column(name = "POST_ACPF_AUDIT")
	private String postAcpfAuditStatus;
	
	@Column(name = "POST_ACPF_AUDIT_GEN_TIME")
	private Date postAcpfAuditGenTime;
	
	@Column(name = "POST_MIG_ATP")
	private String postMigAtpStatus;
	
	@Column(name = "POST_MIG_ATP_GEN_TIME")
	private Date postMigAtpGenTime;
	
	@Column(name = "POST_MIG_AUDIT")
	private String postMigAudiStatus;
	
	@Column(name = "POST_MIG_AUDIT_GEN_TIME")
	private Date postMigAudiGenTime;
	
	@Column(name = "POST_AUPF_AUDIT")
	private String postAupfStatus;
	
	@Column(name = "POST_AUPF_AUDIT_GEN_TIME")
	private Date postAupfGenTime;
	
	@Column(name = "POST_MIG_VDU_AUDIT")
	private String postMigVduStatus;
	
	@Column(name = "POST_MIG_VDU_AUDIT_GEN_TIME")
	private Date postMigVduGenTime;
	
	@Column(name = "POST_MIG_eNB_AUDIT")
	private String postMigEnbAudiStatus;
	
	@Column(name = "POST_MIG_eNB_AUDIT_GEN_TIME")
	private Date ostMigEnbAudiGenTime;
	
	@Column(name = "POST_AU_AUDIT")
	private String postAuAuditStatus;
	
	@Column(name = "POST_AU_AUDIT_GEN_TIME")
	private Date postAuAuditGenTime;
	
	@Column(name = "POST_ENDC_AUDIT")
	private String postEndcAuditStatus;
	
	@Column(name = "POST_ENDC_AUDIT_GEN_TIME")
	private Date postEndcAuditGenTime;
	
	@Column(name = "POST_MIG_FSU_AUDIT")
	private String postMigFsuAuditStatus;
	
	@Column(name = "POST_MIG_FSU_AUDIT_GEN_TIME")
	private Date postMigFsuAuditGenTime;
	
	@Column(name = "SITE_DATA_STATUS")
	private String siteDataStatus;
	
	@Column(name = "REMARKS")
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


	public Date getPreMigEnvGenTime() {
		return preMigEnvGenTime;
	}

	public void setPreMigEnvGenTime(Date preMigEnvGenTime) {
		this.preMigEnvGenTime = preMigEnvGenTime;
	}

	public Date getPreMigGrowGenTime() {
		return preMigGrowGenTime;
	}

	public void setPreMigGrowGenTime(Date preMigGrowGenTime) {
		this.preMigGrowGenTime = preMigGrowGenTime;
	}

	public Date getPreMigCommGenTime() {
		return preMigCommGenTime;
	}

	public void setPreMigCommGenTime(Date preMigCommGenTime) {
		this.preMigCommGenTime = preMigCommGenTime;
	}

	public Date getPreMigEndcGenTime() {
		return preMigEndcGenTime;
	}

	public void setPreMigEndcGenTime(Date preMigEndcGenTime) {
		this.preMigEndcGenTime = preMigEndcGenTime;
	}

	public Date getPreMigRfGenTime() {
		return preMigRfGenTime;
	}

	public void setPreMigRfGenTime(Date preMigRfGenTime) {
		this.preMigRfGenTime = preMigRfGenTime;
	}

	public Date getNeGrowPnpgenTime() {
		return neGrowPnpgenTime;
	}

	public void setNeGrowPnpgenTime(Date neGrowPnpgenTime) {
		this.neGrowPnpgenTime = neGrowPnpgenTime;
	}

	public Date getNeGrowenbGenTime() {
		return neGrowenbGenTime;
	}

	public void setNeGrowenbGenTime(Date neGrowenbGenTime) {
		this.neGrowenbGenTime = neGrowenbGenTime;
	}

	public Date getNeGrowCellGenTime() {
		return neGrowCellGenTime;
	}

	public void setNeGrowCellGenTime(Date neGrowCellGenTime) {
		this.neGrowCellGenTime = neGrowCellGenTime;
	}

	public Date getNeGrowAuCacellGenTime() {
		return neGrowAuCacellGenTime;
	}

	public void setNeGrowAuCacellGenTime(Date neGrowAuCacellGenTime) {
		this.neGrowAuCacellGenTime = neGrowAuCacellGenTime;
	}

	public Date getNeGrowAuGenTime() {
		return neGrowAuGenTime;
	}

	public void setNeGrowAuGenTime(Date neGrowAuGenTime) {
		this.neGrowAuGenTime = neGrowAuGenTime;
	}

	public Date getMigAuGenTime() {
		return migAuGenTime;
	}

	public void setMigAuGenTime(Date migAuGenTime) {
		this.migAuGenTime = migAuGenTime;
	}

	public Date getMigRfGenTime() {
		return migRfGenTime;
	}

	public void setMigRfGenTime(Date migRfGenTime) {
		this.migRfGenTime = migRfGenTime;
	}

	public Date getMigCommGenTime() {
		return migCommGenTime;
	}

	public void setMigCommGenTime(Date migCommGenTime) {
		this.migCommGenTime = migCommGenTime;
	}

	public Date getMigAcpfGenTime() {
		return migAcpfGenTime;
	}

	public void setMigAcpfGenTime(Date migAcpfGenTime) {
		this.migAcpfGenTime = migAcpfGenTime;
	}

	public Date getMigCslGenTime() {
		return migCslGenTime;
	}

	public void setMigCslGenTime(Date migCslGenTime) {
		this.migCslGenTime = migCslGenTime;
	}

	public Date getMigEndcGenTime() {
		return migEndcGenTime;
	}

	public void setMigEndcGenTime(Date migEndcGenTime) {
		this.migEndcGenTime = migEndcGenTime;
	}

	public Date getMigPreCheckGenTime() {
		return migPreCheckGenTime;
	}

	public void setMigPreCheckGenTime(Date migPreCheckGenTime) {
		this.migPreCheckGenTime = migPreCheckGenTime;
	}

	public Date getMigCutoverGenTime() {
		return migCutoverGenTime;
	}

	public void setMigCutoverGenTime(Date migCutoverGenTime) {
		this.migCutoverGenTime = migCutoverGenTime;
	}

	public Date getMigRollbackGenTime() {
		return migRollbackGenTime;
	}

	public void setMigRollbackGenTime(Date migRollbackGenTime) {
		this.migRollbackGenTime = migRollbackGenTime;
	}

	public Date getMigAnchorGenTime() {
		return migAnchorGenTime;
	}

	public void setMigAnchorGenTime(Date migAnchorGenTime) {
		this.migAnchorGenTime = migAnchorGenTime;
	}

	public Date getMigNbrGenTime() {
		return migNbrGenTime;
	}

	public void setMigNbrGenTime(Date migNbrGenTime) {
		this.migNbrGenTime = migNbrGenTime;
	}

	public Date getPostAcpfAuditGenTime() {
		return postAcpfAuditGenTime;
	}

	public void setPostAcpfAuditGenTime(Date postAcpfAuditGenTime) {
		this.postAcpfAuditGenTime = postAcpfAuditGenTime;
	}

	public Date getPostMigAtpGenTime() {
		return postMigAtpGenTime;
	}

	public void setPostMigAtpGenTime(Date postMigAtpGenTime) {
		this.postMigAtpGenTime = postMigAtpGenTime;
	}

	public Date getPostMigAudiGenTime() {
		return postMigAudiGenTime;
	}

	public void setPostMigAudiGenTime(Date postMigAudiGenTime) {
		this.postMigAudiGenTime = postMigAudiGenTime;
	}

	public Date getPostAupfGenTime() {
		return postAupfGenTime;
	}

	public void setPostAupfGenTime(Date postAupfGenTime) {
		this.postAupfGenTime = postAupfGenTime;
	}

	public Date getPostMigVduGenTime() {
		return postMigVduGenTime;
	}

	public void setPostMigVduGenTime(Date postMigVduGenTime) {
		this.postMigVduGenTime = postMigVduGenTime;
	}

	public Date getPostMigEnbAudiGenTime() {
		return ostMigEnbAudiGenTime;
	}

	public void setPostMigEnbAudiGenTime(Date ostMigEnbAudiGenTime) {
		this.ostMigEnbAudiGenTime = ostMigEnbAudiGenTime;
	}

	public Date getPostAuAuditGenTime() {
		return postAuAuditGenTime;
	}

	public void setPostAuAuditGenTime(Date postAuAuditGenTime) {
		this.postAuAuditGenTime = postAuAuditGenTime;
	}

	public Date getPostEndcAuditGenTime() {
		return postEndcAuditGenTime;
	}

	public void setPostEndcAuditGenTime(Date postEndcAuditGenTime) {
		this.postEndcAuditGenTime = postEndcAuditGenTime;
	}

	public Date getPostMigFsuAuditGenTime() {
		return postMigFsuAuditGenTime;
	}

	public void setPostMigFsuAuditGenTime(Date postMigFsuAuditGenTime) {
		this.postMigFsuAuditGenTime = postMigFsuAuditGenTime;
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
	public String getPreMigRfScriptStatus() {
		return preMigRfScriptStatus;
	}

	public void setPreMigRfScriptStatus(String preMigRfScriptStatus) {
		this.preMigRfScriptStatus = preMigRfScriptStatus;
	}

}
