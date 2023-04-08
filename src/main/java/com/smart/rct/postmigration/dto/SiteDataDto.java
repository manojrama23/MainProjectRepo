package com.smart.rct.postmigration.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.models.SiteModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.util.CommonUtil;

@Component
public class SiteDataDto {
	final static Logger logger = LoggerFactory.getLogger(SiteDataDto.class);
	public SiteDataModel getSiteDataDetailsModel(SiteDataEntity siteDataEntity) {
		SiteDataModel  siteDataModel = null;
		try {
			siteDataModel= new SiteDataModel();
			siteDataModel.setId(siteDataEntity.getId());
			siteDataModel.setProgramDetailsEntity(siteDataEntity.getProgramDetailsEntity());
			siteDataModel.setFileName(siteDataEntity.getFileName());
			siteDataModel.setFilePath(siteDataEntity.getFilePath());
			siteDataModel.setCiqFileName(siteDataEntity.getCiqFileName());
			siteDataModel.setNeName(siteDataEntity.getNeName());
			siteDataModel.setRemarks(siteDataEntity.getRemarks());
			siteDataModel.setPackedBy(siteDataEntity.getPackedBy());
			siteDataModel.setPackedDate(CommonUtil.dateToString(siteDataEntity.getPackedDate(), Constants.YYYY_MM_DD_HH_MM_SS));
			siteDataModel.setNeId(siteDataEntity.getNeId());
			siteDataModel.setReportType(siteDataEntity.getReportType());
			siteDataModel.setSiteName(siteDataEntity.getSiteName());
			siteDataModel.setSiteReportStatus(siteDataEntity.getSiteReportStatus());
			siteDataModel.setOvSiteReportStatus(siteDataEntity.getOvUpdateStatus());
		} catch (Exception e) {
			logger.error("Excpetion SiteDataDto.getSiteDataDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return siteDataModel;
	}

	public SiteDataEntity getSiteDataDetailsModel(SiteDataModel siteDataModel) {
		SiteDataEntity  siteDataEntity = null;
		try {
			siteDataEntity= new SiteDataEntity();
			siteDataEntity.setId(siteDataModel.getId());
			siteDataEntity.setProgramDetailsEntity(siteDataModel.getProgramDetailsEntity());
			siteDataEntity.setFileName(siteDataModel.getFileName());
			siteDataEntity.setFilePath(siteDataModel.getFilePath());
			siteDataEntity.setCiqFileName(siteDataModel.getCiqFileName());
			siteDataEntity.setNeName(siteDataModel.getNeName());
			siteDataEntity.setRemarks(siteDataModel.getRemarks());
			siteDataEntity.setPackedBy(siteDataModel.getPackedBy());
			siteDataEntity.setPackedDate(new Date());
		} catch (Exception e) {
			logger.error("Excpetion SiteDataDto.getSiteDataDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return siteDataEntity;
	}

	public static SiteModel getSiteDataDetailsModeld(List<CIQDetailsModel> listCIQDetailsModelDay1) {
		List<SiteModel> ModelList = new ArrayList<>();
		String eNodeBName="";
		String eNodeBSW="";
		String fsuSW = "";
		String vDUSW = "";
		String softWareRelease ="";
		String fuzeProjId="";
		SiteModel A5GDSSSummaryModel = new SiteModel(eNodeBName, eNodeBSW, fsuSW, vDUSW, softWareRelease ,fuzeProjId);
		A5GDSSSummaryModel.seteNodeBName(listCIQDetailsModelDay1.get(0).getCiqMap().get("850NRCarriersite").getHeaderValue().trim());
		A5GDSSSummaryModel.seteNodeBSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("eNBVersion").getHeaderValue().trim());
		A5GDSSSummaryModel.setvDUSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("vDU_Version").getHeaderValue().trim());
		A5GDSSSummaryModel.setFsuSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("FSUVersion").getHeaderValue().trim());
		A5GDSSSummaryModel.setFuzeProjId(listCIQDetailsModelDay1.get(0).getCiqMap().get("EquipmentLocation").getHeaderValue().trim());

		ModelList.add(A5GDSSSummaryModel);
		return A5GDSSSummaryModel;
	}
	public static SiteModel getSiteDataDetailsModeFSU(List<CIQDetailsModel> listCIQDetailsModelDay1) {
		List<SiteModel> ModelList = new ArrayList<>();
		String eNodeBName="";
		String eNodeBSW="";
		String fsuSW = "";
		String vDUSW = "";
		String softWareRelease ="";
		String  fuzeProjId="";
		SiteModel A5GDSSSummaryModel = new SiteModel(eNodeBName, eNodeBSW, fsuSW, vDUSW, softWareRelease ,fuzeProjId);
		A5GDSSSummaryModel.seteNodeBName(listCIQDetailsModelDay1.get(0).getCiqMap().get("850_NR_Carrier_site").getHeaderValue().trim());
		A5GDSSSummaryModel.seteNodeBSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("NE_Version").getHeaderValue().trim());
		//A5GDSSSummaryModel.setvDUSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("vDU_Version").getHeaderValue().trim());
		A5GDSSSummaryModel.setFsuSW(listCIQDetailsModelDay1.get(0).getCiqMap().get("NE_Version").getHeaderValue().trim());
		ModelList.add(A5GDSSSummaryModel);
		return A5GDSSSummaryModel;
	}

	public static SiteModel getSiteDataDetailsModelCd(List<CIQDetailsModel> listCIQDetailsModelDay01) {
		List<SiteModel> ModelList = new ArrayList<>();
		String eNodeBName="";
		String eNodeBSW="";
		String fsuSW = "";
		String vDUSW = "";
		String softWareRelease="";
		String fuzeProjId="";
		SiteModel A5GCBANDSummaryModel = new SiteModel(eNodeBName, eNodeBSW, fsuSW, vDUSW, softWareRelease,fuzeProjId);
		A5GCBANDSummaryModel.seteNodeBName(listCIQDetailsModelDay01.get(0).getCiqMap().get("EquipmentLocation").getHeaderValue().trim());
		A5GCBANDSummaryModel.seteNodeBSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNBVersion").getHeaderValue().trim());
		A5GCBANDSummaryModel.setvDUSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNB_Version").getHeaderValue().trim());
		//A5GCBANDSummaryModel.setFsuSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("").getHeaderValue().trim());
		return A5GCBANDSummaryModel;
	}
	public static SiteModel getSiteDataDetailsModel5GMM(List<CIQDetailsModel> listCIQDetailsModelDay01) {
		List<SiteModel> ModelList = new ArrayList<>();
		String eNodeBName="";
		String eNodeBSW="";
		String fsuSW = "";
		String vDUSW = "";
		String softWareRelease ="";
		String fuzeProjId ="";
		SiteModel A5GMMSummaryModel = new SiteModel(eNodeBName, eNodeBSW, fsuSW, vDUSW, softWareRelease,fuzeProjId);
		A5GMMSummaryModel.setsoftWareRelease(listCIQDetailsModelDay01.get(0).getCiqMap().get("ne_version").getHeaderValue().trim());
		A5GMMSummaryModel.setFuzeProjId(listCIQDetailsModelDay01.get(0).getCiqMap().get("FuzeProjId").getHeaderValue().trim());
		//A5GMMSummaryModel.seteNodeBSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNBVersion").getHeaderValue().trim());
		//A5GMMSummaryModel.setvDUSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNB_Version").getHeaderValue().trim());
		//A5GMMSummaryModel.setFsuSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("").getHeaderValue().trim());
		return A5GMMSummaryModel;
	}
	public static SiteModel getSiteDataDetailsModelusm(List<CIQDetailsModel> listOfCiqDetails) {
		List<SiteModel> ModelList = new ArrayList<>();
		String eNodeBName="";
		String eNodeBSW="";
		String fsuSW = "";
		String vDUSW = "";
		String softWareRelease ="";
		String fuzeProjId ="";
		SiteModel A5GMMSummaryModel = new SiteModel(eNodeBName, eNodeBSW, fsuSW, vDUSW, softWareRelease,fuzeProjId);
		A5GMMSummaryModel.setsoftWareRelease(listOfCiqDetails.get(0).getCiqMap().get("ne_version").getHeaderValue().trim());
	 	A5GMMSummaryModel.setFuzeProjId(listOfCiqDetails.get(0).getCiqMap().get("FuzeProjId").getHeaderValue().trim());
		//A5GMMSummaryModel.seteNodeBSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNBVersion").getHeaderValue().trim());
		//A5GMMSummaryModel.setvDUSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("eNB_Version").getHeaderValue().trim());
		//A5GMMSummaryModel.setFsuSW(listCIQDetailsModelDay01.get(0).getCiqMap().get("").getHeaderValue().trim());
		return A5GMMSummaryModel;
	}
	
	
}
