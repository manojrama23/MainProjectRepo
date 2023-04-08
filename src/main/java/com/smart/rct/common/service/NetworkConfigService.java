package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.NetworkConfigModel;

public interface NetworkConfigService {
	public boolean duplicateNetworkConfig(NetworkConfigModel networkConfigModel);

	public boolean createNetworkConfig(NetworkConfigEntity networkConfigEntity);

	public Map<String, Object> getNetworkConfigDetails(NetworkConfigModel networkConfigModel, int page, int count,
			List<CustomerDetailsEntity> programNamesList);
	
	public Map<String, Object> getNetworkConfigDetailsByPage(NetworkConfigModel networkConfigModel, List<CustomerDetailsEntity> programNamesList);

	public List<NetworkConfigEntity> getNetworkConfigDetails(NetworkConfigModel networkConfigModel);
	
	public List<NetworkConfigEntity> getNetworkConfigList(int programId);

	public boolean deleteNetworkConfigDetails(Integer networkConfigId);

	public JSONObject importNetworkConfigDetails(MultipartFile file, List<String> programNamesList, String sessionId);

	public boolean deleteNetworkConfigServerDetails(int parseInt);

	public boolean getNetWorkDetailsForCreateExcel(NetworkConfigModel networkConfigModel, List<String> programNamesList,
			boolean addToZip);

	public NetworkConfigDetailsEntity getNetworkConfigServerDetailsById(int networkConfigDetailId);
}
