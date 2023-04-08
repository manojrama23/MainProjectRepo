package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VZW_4G_CIQ_MAIN")
public class RFDBEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;
	
	@Column(name = "Market", nullable = false)
	private String Market;
	@Column(name = "USM")
	private String USM;
	
	@Column(name = "Version")
	private String VERSION;
	public String getVERSION() {
		return VERSION;
	}
	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}
	@Column(name = "`eNB Name`")
	private String eNB_Name;
	@Column(name = "`Samsung eNB ID`")
	private String Samsung_eNB_ID;
	@Column(name = "`Cell ID`")
	private String Cell_ID;
	@Column(name = "TAC")
	private String TAC;
	
	@Column(name = "PCI")
	private String PCI;
	@Column(name = "RACH")
	private String RACH;
	@Column(name = "`Band Name`")
	private String BandName;
	@Column(name = "`Bandwidth (MHz)`")
	private String Bandwidth;
	@Column(name = "`EARFCN DL`")
	private String EARFCN_DL;
	@Column(name = "`EARFCN UL`")
	private String EARFCN_UL;
	@Column(name = "`Output Power (dBm)`")
	private String Output_Power_dBm;
	/*@Column(name = "`CPRI Port Assignment`")
	private String CPRI_Port_Assignment;*/
	@Column(name = "`Tx Diversity`")
	private String Tx_Diversity;
	@Column(name = "`Rx Diversity`")
	private String Rx_Diveristy;
	@Column(name = "`Electrical Tilt`")
	private String Electrical_Tilt;
	@Column(name = "`RRH Type(after)`")
	private String RRH_Type;
	@Column(name = "`Card Count per eNB`")
	private String Card_Count_per_eNB;
	@Column(name = "Deployment")
	private String Deployment;
	@Column(name = "`RRH_CODE(after)`")
	private String RRH_Code;
	@Column(name = "`Market CLLI Code`")
	private String Market_CLLI_Code;
	@Column(name = "aliasName")
	private String aliasName;
	@Column(name = "antennaPathDelayDL")
	private String antennaPathDelayDL;
	@Column(name = "antennaPathDelayUL")
	private String antennaPathDelayUL;
	@Column(name = "`antennaPathDelayDL (m)`")
	private String antennaPathDelayDLm;
	@Column(name = "`antennaPathDelayUL (m)`")
	private String antennaPathDelayULm;
	@Column(name = "`DAS OUTPUT POWER`")
	private String DAS_OUTPUT_POWER;
	@Column(name = "DAS")
	private String das;
	@Column(name = "`NBIoT TAC`")//
	private String NBIoT_TAC;
	@Column(name = "`PRACH CONFIG INDEX`")
	private String PreambleFormat_prachIndex;
	@Column(name = "pa")
	private String pa;
	@Column(name = "pb")
	private String pb;
	@Column(name = "`ZCZC`")//
	private String prachCS;
	@Column(name = "SDL")
	private String SDL;
	@Column(name = "`CBRS user-id`")
	private String CBRS_user_id;
	@Column(name = "`CBRS Fcc-Id`")
	private String CBRS_FCC_ID;
	@Column(name = "`max eirp threshold`")//
	private String max_eirp_threshold;
	@Column(name = "`Preferred Earfcn`")
	private String Preferred_Earfcn;
	@Column(name = "`CBSD Category`")
	private String CBSD_Category;
	@Column(name = "`RU Port ID`")
	private String RU_port;
	@Column(name = "`DSP Index`")//
	private String DSP_ID;
	@Column(name = "`DSP Cell Index`")//
	private String DSP_CELL_INDEX;
	@Column(name = "`CBRS/LAA Antenna Gain(dBi)`")
	private String ANTENNA_GAIN_DBI;
	@Column(name = "`X Pole Antenna`")
	private String X_Pole_Antenna;
	@Column(name = "USM_IP")
	private String USM_IP;
	@Column(name = "NBIoT")
	private String nbIOT;
	@Column(name = "`Release Version`")
	private String release_version;
	@Column(name = "`NE Version`")
	private String ne_version;
	@Column(name = "`administrative-state`")
	private String adminiState;
	
	public String getAdminiState() {
		return adminiState;
	}
	public void setAdminiState(String adminiState) {
		this.adminiState = adminiState;
	}
	
	public String getRelease_version() {
		return release_version;
	}
	public void setRelease_version(String release_version) {
		this.release_version = release_version;
	}
	public String getNe_version() {
		return ne_version;
	}
	public void setNe_version(String ne_version) {
		this.ne_version = ne_version;
	}
	@Column(name = "eMTC")
	private String eMTC;
	
	@Column(name = "DSS")
	private String dSS;
	
	@Column(name = "`DSS Scenario`")
	private String dSSScenario;
	
	public String getMcType() {
		return mcType;
	}
	public void setMcType(String mcType) {
		this.mcType = mcType;
	}
	@Column(name = "`MULTI CARRIER TYPE`")
	private String mcType;
	
	@Column(name = "`UL COMP`")
	private String ulComp;
	
	public String getUlComp() {
		return ulComp;
	}
	public void setUlComp(String ulComp) {
		this.ulComp = ulComp;
	}
	public String getPrachConfigIndex() {
		return prachConfigIndex;
	}
	public void setPrachConfigIndex(String prachConfigIndex) {
		this.prachConfigIndex = prachConfigIndex;
	}
	@Column(name = "`LCC Card`")
	private String lCCCard;
	
	@Column(name = "`PRACH CONFIG INDEX (new)`")
	private String prachConfigIndex;
	
	@Column(name = "`CPRI Port ID`")
	private String CRPIPortID;
	
	@Column(name = "`Network`")
	private String Network;
	
	public String getNetwork() {
		return Network;
	}
	public void setNetwork(String network) {
		Network = network;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMarket() {
		return Market;
	}
	public void setMarket(String market) {
		Market = market;
	}
	public String getUSM() {
		return USM;
	}
	public void setUSM(String uSM) {
		USM = uSM;
	}
	
	public String geteNB_Name() {
		return eNB_Name;
	}
	public void seteNB_Name(String eNB_Name) {
		this.eNB_Name = eNB_Name;
	}
	public String getSamsung_eNB_ID() {
		return Samsung_eNB_ID;
	}
	public void setSamsung_eNB_ID(String samsung_eNB_ID) {
		Samsung_eNB_ID = samsung_eNB_ID;
	}
	public String getCell_ID() {
		return Cell_ID;
	}
	public void setCell_ID(String cell_ID) {
		Cell_ID = cell_ID;
	}
	public String getTAC() {
		return TAC;
	}
	public void setTAC(String tAC) {
		TAC = tAC;
	}
	public String getPCI() {
		return PCI;
	}
	public void setPCI(String pCI) {
		PCI = pCI;
	}
	public String getRACH() {
		return RACH;
	}
	public void setRACH(String rACH) {
		RACH = rACH;
	}
	public String getBandName() {
		return BandName;
	}
	public void setBandName(String bandName) {
		BandName = bandName;
	}
	public String getBandwidth() {
		return Bandwidth;
	}
	public void setBandwidth(String bandwidth) {
		Bandwidth = bandwidth;
	}
	public String getEARFCN_DL() {
		return EARFCN_DL;
	}
	public void setEARFCN_DL(String eARFCN_DL) {
		EARFCN_DL = eARFCN_DL;
	}
	public String getEARFCN_UL() {
		return EARFCN_UL;
	}
	public void setEARFCN_UL(String eARFCN_UL) {
		EARFCN_UL = eARFCN_UL;
	}
	public String getOutput_Power_dBm() {
		return Output_Power_dBm;
	}
	public void setOutput_Power_dBm(String output_Power_dBm) {
		Output_Power_dBm = output_Power_dBm;
	}
	/*public String getCPRI_Port_Assignment() {
		return CPRI_Port_Assignment;
	}
	public void setCPRI_Port_Assignment(String cPRI_Port_Assignment) {
		CPRI_Port_Assignment = cPRI_Port_Assignment;
	}*/
	public String getTx_Diversity() {
		return Tx_Diversity;
	}
	public void setTx_Diversity(String tx_Diversity) {
		Tx_Diversity = tx_Diversity;
	}
	public String getNbIOT() {
		return nbIOT;
	}
	public void setNbIOT(String nbIOT) {
		this.nbIOT = nbIOT;
	}
	public String geteMTC() {
		return eMTC;
	}
	public void seteMTC(String eMTC) {
		this.eMTC = eMTC;
	}
	public String getdSS() {
		return dSS;
	}
	public void setdSS(String dSS) {
		this.dSS = dSS;
	}
	public String getdSSScenario() {
		return dSSScenario;
	}
	public void setdSSScenario(String dSSScenario) {
		this.dSSScenario = dSSScenario;
	}
	public String getlCCCard() {
		return lCCCard;
	}
	public void setlCCCard(String lCCCard) {
		this.lCCCard = lCCCard;
	}
	public String getCRPIPortID() {
		return CRPIPortID;
	}
	public void setCRPIPortID(String cRPIPortID) {
		CRPIPortID = cRPIPortID;
	}
	public String getRx_Diveristy() {
		return Rx_Diveristy;
	}
	public void setRx_Diveristy(String rx_Diveristy) {
		Rx_Diveristy = rx_Diveristy;
	}
	public String getElectrical_Tilt() {
		return Electrical_Tilt;
	}
	public void setElectrical_Tilt(String electrical_Tilt) {
		Electrical_Tilt = electrical_Tilt;
	}
	public String getRRH_Type() {
		return RRH_Type;
	}
	public void setRRH_Type(String rRH_Type) {
		RRH_Type = rRH_Type;
	}
	public String getCard_Count_per_eNB() {
		return Card_Count_per_eNB;
	}
	public void setCard_Count_per_eNB(String card_Count_per_eNB) {
		Card_Count_per_eNB = card_Count_per_eNB;
	}
	public String getDeployment() {
		return Deployment;
	}
	public void setDeployment(String deployment) {
		Deployment = deployment;
	}
	public String getRRH_Code() {
		return RRH_Code;
	}
	public void setRRH_Code(String rRH_Code) {
		RRH_Code = rRH_Code;
	}
	public String getMarket_CLLI_Code() {
		return Market_CLLI_Code;
	}
	public void setMarket_CLLI_Code(String market_CLLI_Code) {
		Market_CLLI_Code = market_CLLI_Code;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getAntennaPathDelayDL() {
		return antennaPathDelayDL;
	}
	public void setAntennaPathDelayDL(String antennaPathDelayDL) {
		this.antennaPathDelayDL = antennaPathDelayDL;
	}
	public String getAntennaPathDelayUL() {
		return antennaPathDelayUL;
	}
	public void setAntennaPathDelayUL(String antennaPathDelayUL) {
		this.antennaPathDelayUL = antennaPathDelayUL;
	}
	public String getAntennaPathDelayDLm() {
		return antennaPathDelayDLm;
	}
	public void setAntennaPathDelayDLm(String antennaPathDelayDLm) {
		this.antennaPathDelayDLm = antennaPathDelayDLm;
	}
	public String getAntennaPathDelayULm() {
		return antennaPathDelayULm;
	}
	public void setAntennaPathDelayULm(String antennaPathDelayULm) {
		this.antennaPathDelayULm = antennaPathDelayULm;
	}
	public String getDAS_OUTPUT_POWER() {
		return DAS_OUTPUT_POWER;
	}
	public void setDAS_OUTPUT_POWER(String dAS_OUTPUT_POWER) {
		DAS_OUTPUT_POWER = dAS_OUTPUT_POWER;
	}
	public String getDas() {
		return das;
	}
	public void setDas(String das) {
		this.das = das;
	}
	public String getNBIoT_TAC() {
		return NBIoT_TAC;
	}
	public void setNBIoT_TAC(String nBIoT_TAC) {
		NBIoT_TAC = nBIoT_TAC;
	}
	public String getPreambleFormat_prachIndex() {
		return PreambleFormat_prachIndex;
	}
	public void setPreambleFormat_prachIndex(String preambleFormat_prachIndex) {
		PreambleFormat_prachIndex = preambleFormat_prachIndex;
	}
	public String getPa() {
		return pa;
	}
	public void setPa(String pa) {
		this.pa = pa;
	}
	public String getPb() {
		return pb;
	}
	public void setPb(String pb) {
		this.pb = pb;
	}
	public String getPrachCS() {
		return prachCS;
	}
	public void setPrachCS(String prachCS) {
		this.prachCS = prachCS;
	}
	public String getSDL() {
		return SDL;
	}
	public void setSDL(String sDL) {
		SDL = sDL;
	}
	public String getCBRS_user_id() {
		return CBRS_user_id;
	}
	public void setCBRS_user_id(String cBRS_user_id) {
		CBRS_user_id = cBRS_user_id;
	}
	public String getCBRS_FCC_ID() {
		return CBRS_FCC_ID;
	}
	public void setCBRS_FCC_ID(String cBRS_FCC_ID) {
		CBRS_FCC_ID = cBRS_FCC_ID;
	}
	public String getMax_eirp_threshold() {
		return max_eirp_threshold;
	}
	public void setMax_eirp_threshold(String max_eirp_threshold) {
		this.max_eirp_threshold = max_eirp_threshold;
	}
	public String getPreferred_Earfcn() {
		return Preferred_Earfcn;
	}
	public void setPreferred_Earfcn(String preferred_Earfcn) {
		Preferred_Earfcn = preferred_Earfcn;
	}
	public String getCBSD_Category() {
		return CBSD_Category;
	}
	public void setCBSD_Category(String cBSD_Category) {
		CBSD_Category = cBSD_Category;
	}
	public String getRU_port() {
		return RU_port;
	}
	public void setRU_port(String rU_port) {
		RU_port = rU_port;
	}
	public String getDSP_ID() {
		return DSP_ID;
	}
	public void setDSP_ID(String dSP_ID) {
		DSP_ID = dSP_ID;
	}
	public String getDSP_CELL_INDEX() {
		return DSP_CELL_INDEX;
	}
	public void setDSP_CELL_INDEX(String dSP_CELL_INDEX) {
		DSP_CELL_INDEX = dSP_CELL_INDEX;
	}
	public String getANTENNA_GAIN_DBI() {
		return ANTENNA_GAIN_DBI;
	}
	public void setANTENNA_GAIN_DBI(String aNTENNA_GAIN_DBI) {
		ANTENNA_GAIN_DBI = aNTENNA_GAIN_DBI;
	}
	public String getX_Pole_Antenna() {
		return X_Pole_Antenna;
	}
	public void setX_Pole_Antenna(String x_Pole_Antenna) {
		X_Pole_Antenna = x_Pole_Antenna;
	}
	public String getUSM_IP() {
		return USM_IP;
	}
	public void setUSM_IP(String uSM_IP) {
		USM_IP = uSM_IP;
	}
	

}
