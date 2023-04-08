package com.smart.rct.postmigration.repository;

import java.util.Map;

import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;

public interface SiteDataRepository {

	public boolean saveSiteDataAudit(SiteDataEntity siteDataEntity);

	public boolean deleteSiteDataDetails(SiteDataModel siteDataModel);

	public Map<String, Object> getSiteDataDetails(SiteDataModel siteDataModel, int page, int count);

	public SiteDataEntity getSiteDataDetailsById(int articleId);

}
