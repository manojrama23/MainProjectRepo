package com.smart.rct.premigration.repository;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvReport;
import com.smart.rct.common.entity.OvScheduledEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.FetchOVResponseModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.premigration.serviceImpl.EnodebDetails;

public interface FetchProcessRepository {
	
	OvScheduledEntity getScheduledRecord(String neId, String trackerId);
	OvScheduledEntity saveScheduledDetails(OvScheduledEntity ovScheduledEntity); 
	CustomerDetailsEntity getProgrammeDetails(String programName);
	List<ProgramTemplateEntity> getScheduledDaysDetails(ProgramTemplateModel programTemplateModel);
	 ProgramTemplateEntity getFetchTimeProgaramTemplate(ProgramTemplateModel programTemplateModel);
	 List<NetworkConfigEntity> getNetworkConfigDetailsForOv(FetchOVResponseModel objFetchOVResponseModel);
	 List<EnodebDetails> getEnbDetails(String ciqfileName, String dbcollectionFileName);
	OvReport saveOvReportsDetails(OvReport ovReports);
	OvReport getOvReportsDetails(String trackerId, String enbId);
	
	

}
