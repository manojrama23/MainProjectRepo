package com.smart.rct.common.repository;

import java.util.Date;
import java.util.List;

import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.postmigration.entity.PartialSaveSiteReportEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;

public interface SiteDetailsReportRepository {
	
	SiteDataEntity getSiteDetailsById(int siteDataId);
	List<SiteDataEntity> getHistorySiteDetails(String neId);
	SiteDataEntity saveSiteDataEntity(SiteDataEntity siteDataEntity);

	
	

	List<SiteDataEntity> getDonldSiteDetails(int programDetailsEntity, Date fromDate, Date toDate);
	PartialSaveSiteReportEntity getSiteDetailsForSavefile(String neId);
	PartialSaveSiteReportEntity savePartialSiteDataEntity(PartialSaveSiteReportEntity partialSaveSiteReportEntity);
	SiteDataEntity getSiteDataEntity(Integer runTestId);
	boolean updateSiteDataEntity(SiteDataEntity siteDataEntity);
	 boolean updateSiteReportOv(SiteReportOVEntity siteReportOVEntity);
	List<SiteReportOVEntity> getSiteReportOVEntity(Integer runTestId);
	
}