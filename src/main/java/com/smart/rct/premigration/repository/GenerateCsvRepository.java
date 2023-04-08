package com.smart.rct.premigration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeTypeModel;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.EnbPreGrowAuditEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.Ip;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.models.CsvInfoAuditModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;

public interface GenerateCsvRepository {

	public List<CIQDetailsModel> findAll(String fileName);

	public boolean saveCsvAudit(GenerateInfoAuditEntity objInfo);

	public Map<String, Object> getCsvAuditDetails(CsvInfoAuditModel csvModel,int page, int count);

	public LsmEntity getLsmById(int iLsmId);

	public List<CsvInfoAuditModel> getCsvFilesList(int customerId);

	public List<UseCaseBuilderEntity> getUseCaseDetails(Integer customerId);

	public boolean savepreGrowAudit(EnbPreGrowAuditEntity objInfo);

	public List<EnbPreGrowAuditModel> getPreGrowListAudit(int customerId);

	public Map<String, Object> generateFilesListSearch(CsvInfoAuditModel objCsvInfoAuditModel, int customerId, int page,
			int count);

	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count);

	public List<CiqUploadAuditTrailDetEntity> getciqList(EnbPreGrowAuditModel enbModel,CiqUploadAuditTrailDetModel ciqModel);

	public List<GenerateInfoAuditEntity> getNeNameList(EnbPreGrowAuditModel enbModel,CsvInfoAuditModel csvModel);

	public List<NeVersionEntity> getSmVersionList(EnbPreGrowAuditModel enbModel,NeVersionModel neVersionModel);

	public List<NeTypeEntity> getSmNameList(NeTypeModel neTypeModel);

	public List<UseCaseBuilderEntity> getUseCaseList(UseCaseBuilderModel useCaseBuilderModel);

	public Ip getip(String marketid);

	public List<NeMappingEntity> getVersion(String enbid);
}
