package com.smart.rct.common.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LoginTypeEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ServerTypeEntity;
import com.smart.rct.common.models.LoginTypeModel;
import com.smart.rct.common.models.NeTypeModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.ServerTypeModel;

public interface NetworkConfigRepository {
	public Map<String, Object> getNetworkConfigDetails(NetworkConfigModel networkConfigModel, int page, int count,
			List<CustomerDetailsEntity> programNamesList);
	
	public Map<String, Object> getNetworkConfigDetailsPage(NetworkConfigModel networkConfigModel, List<CustomerDetailsEntity> programNamesList);

	public List<NeTypeEntity> getNeTypeList(NeTypeModel neTypeModel);

	public List<LoginTypeEntity> getLoginTypeList(LoginTypeModel loginTypeModel);

	public List<ServerTypeEntity> getServerTypeList(ServerTypeModel serverTypeModel);

	public boolean duplicateNetworkConfig(NetworkConfigModel networkConfigModel);

	public boolean createNetworkConfig(NetworkConfigEntity networkConfigEntity);

	public boolean deleteNetworkConfigDetails(Integer networkConfigId);

	public boolean deleteNetworkConfigServerDetails(int networkConfigDetailId);

	public List<NetworkConfigEntity> getNetworkConfigDetailsForExPort(NetworkConfigModel networkConfigModel);

	public List<NetworkConfigEntity> getNetworkConfigDetails(NetworkConfigModel networkConfigModel);

	public NetworkConfigDetailsEntity getNetworkConfigServerDetailsById(int networkConfigDetailId);

	public List<NetworkConfigEntity> getNetworkConfigList(int programId);
}
