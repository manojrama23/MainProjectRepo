package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.models.LsmModel;
import com.smart.rct.common.models.NetworkTypeDetailsModel;

public interface LsmService {
	Map<String, Object> getLsmDetails(LsmModel objLsmModel, int page, int count);

	boolean createLsm(LsmEntity lsmEntity);

	boolean updateLsm(LsmEntity updateUserEntity);

	boolean deleteLsmDetails(int lsmId);

	boolean duplicateLsm(LsmModel objLsmModel);

	List<String> getLsmVersionsByNetworkType(Integer networkTypeId);

	List<NetworkTypeDetailsModel> getNetWorksBasedOnCustomer(int customerId);

	JSONObject createLsmFromInputFile(MultipartFile file, String sessionId);

	List<LsmEntity> getLsmEntityDetails();

	public boolean getLsmDetailsForCreateExcel(LsmModel objLsmModel);
}
