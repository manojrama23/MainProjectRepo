package com.smart.rct.migration.repository;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;

public interface RunTestResultRepository {
	
	public boolean createRunTestResult(RunTestResultEntity runTestResultEntity);

	public List<RunTestResultEntity> getRunTestResultList(Integer runTestId);
	
	public List<RunTestResultEntity> getRunTestResultEntityList(int runTestId,int scriptId);
	
	public String getScriptOutput(Integer runTestId,Integer useCaseId,Integer scriptId);

	public boolean updateValueToPrevious(RunTestResultEntity runTestResultEntity);
	
	public List<RunTestResultEntity> getPreviousRunTestResult(Integer id);
	
	public CustomerDetailsEntity getCustomerDetailsEntity(int programId);

	public List<OvTestResultEntity> getOVRunTestResultList(Integer runTestId);

	public List<PremigrationOvUpadteEntity> getOVRunTestResultListpre(Integer runTestId);
	
}