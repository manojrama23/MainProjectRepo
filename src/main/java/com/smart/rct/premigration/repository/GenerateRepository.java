package com.smart.rct.premigration.repository;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;

public interface GenerateRepository {

	public List<CIQDetailsModel> findAll(String fileName);

	public JSONObject saveCsvAudit(GenerateInfoAuditEntity objInfo);

	public Map<String, Object> getCsvAuditDetails(GenerateInfoAuditModel csvModel, int page, int count);

	public LsmEntity getLsmById(int iLsmId);

	public List<GenerateInfoAuditModel> getCsvFilesList(int customerId);

	public List<UseCaseBuilderEntity> getUseCaseDetails(Integer customerId);

	public boolean savepreGrowAudit(EnbPreGrowAuditEntity objInfo);

	public List<EnbPreGrowAuditModel> getPreGrowListAudit(int customerId);

	public Map<String, Object> generateFilesListSearch(GenerateInfoAuditModel objCsvInfoAuditModel, int customerId, int page,
			int count);

	public boolean deleteGeneratedFileDetails(Integer id);

	
	public String[] getMmeIpDetails(String market);

	public String[] getBucketDetails(String ciqBucketName);

	public String[] getRrhAluDetails(String oldModel);

	public String[] getVlsmMmeIpDetails(String market);

	public boolean updateGeneratedFileDetails(GenerateInfoAuditEntity generateInfoAuditEntity);

	public GenerateInfoAuditEntity getGenerateInfoAuditById(int articleId);
	
	
}
