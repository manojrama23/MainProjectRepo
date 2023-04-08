package com.smart.rct.common.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.models.LsmModel;
import com.smart.rct.common.models.NetworkTypeDetailsModel;

public interface LsmRepository {

	Map<String, Object> getLsmDetails(LsmModel objLsmModel, int page, int count);

	boolean createLsm(LsmEntity lsmEntity);

	boolean updateLsm(LsmEntity updateUserEntity);

	boolean deleteLsmDetails(int lsmId);

	boolean duplicateLsm(LsmModel objLsmModel);

	List<String> getLsmVersionsByNetworkType(Integer networkTypeId);

	List<NetworkTypeDetailsModel> getNetWorksBasedOnCustomer(int customerId);

	LsmEntity getLsmEntity(String lsmVersion, String lsmName, Integer id);

	List<LsmEntity> getLsmEntityDetails();

	List<LsmModel> getLsmDetailsForCreateExcel(LsmModel objLsmModel);

}
