package com.smart.rct.common.repository;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;

public interface NetworkTypeDetailsRepository {

	public boolean saveNetworkTypeDetails(NetworkTypeDetailsEntity networkTypeDetailsEntity);

	public List<NetworkTypeDetailsEntity> getNwTypeDetails(boolean addAllRecord);

	public boolean deleteNetworkTypeDetials(int nwTypeId);

	public NetworkTypeDetailsEntity getNetworkTypeById(int networkTypeId);

	NetworkTypeDetailsEntity getNetworkTypeByName(String networkType);

	public List<CustomerDetailsEntity> getNwTypeDetailsByCustomerId(int customerId);

}
