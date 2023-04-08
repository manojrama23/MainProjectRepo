package com.smart.rct.common.service;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.common.models.SiteCompletionModel;
import com.smart.rct.postmigration.entity.PartialSaveSiteReportEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;

public interface SiteDetailsReportService {
	
	boolean createExcelSiteReportDetails(SiteCompletionModel siteCompletionModel,String fileNamePath);
	boolean createExcelSiteReportDetailsForDSS(SiteCompletionModel siteCompletionModel,String fileNamePath);
	boolean createExcelSiteReportUsmLiveDetails(SiteCompletionModel siteCompletionUsmLiveModel,String fileNamePath);
	SiteDataEntity saveSiteDetails(SiteCompletionModel siteCompletionUsmLiveModel, JSONObject siteDetails,
			String excelpath, String fileName) ;
	PartialSaveSiteReportEntity savePartialSiteDetails(SiteCompletionModel siteCompletionUsmLiveModel, JSONObject siteDetails, String neidfiveG) ;
	JSONObject getSiteDetailsById(int siteDataId);
	 List<SiteDataModel> getHistorySiteDetails(String neId);
	//List<SiteDataModel> getDonldSiteDetails(int sourceProgramId);
	
	//List<SiteDataModel> getDonldSiteDetails();
	List<SiteDataModel> getDonldSiteDetails(int sourceProgramId, Date FromDate, Date ToDate);
	boolean createExcelSiteReportDetailsForCBANAD(SiteCompletionModel siteCompletionModel, String fileNamePath);
	boolean createExcelSiteReportDetailsForFSU(SiteCompletionModel siteCompletionModel, String fileNamePath);
	JSONObject getSiteDetailsForSavefile(String neId);
	JSONObject SiteReportUploadeToOV(JSONObject siteDetails, SiteDataEntity statusSiteDataEntity);
	boolean updateSiteDataEntity(SiteDataEntity siteDataEntity);
	List<SiteReportOVEntity> getSiteReportOVEntity(Integer runTestId);
	
}