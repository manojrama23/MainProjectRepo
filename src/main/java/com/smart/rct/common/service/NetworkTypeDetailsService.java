package com.smart.rct.common.service;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.exception.RctException;

public interface NetworkTypeDetailsService {

	boolean saveNetworkTypeDetails(NetworkTypeDetailsEntity networkTypeDetailsEntity);

	List<NetworkTypeDetailsEntity> getNwTypeDetails(boolean addAllRecord);

	List<CustomerDetailsEntity> getNwTypeDetailsByCustomerId(int customerId);

	boolean deleteNetworkTypeDetials(int networkTypeId) throws RctException;

	NetworkTypeDetailsEntity getNetworkTypeById(int networkTypeId);

	NetworkTypeDetailsEntity getNetworkTypeByName(String networkType);

}
