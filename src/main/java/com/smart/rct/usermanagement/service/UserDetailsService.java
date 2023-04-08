package com.smart.rct.usermanagement.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.premigration.models.DashBoardModel;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;

public interface UserDetailsService {

	public boolean createUser(UserDetailsEntity userEntity);

	public Map<String, Object> getUserList(User user, UserDetailsModel userDetailsModel, Integer customerId, int page, int count);

	public boolean deleteUser(int userName);

	public boolean updateUser(UserDetailsEntity userEntity);

	public boolean duplicateUser(UserDetailsModel userUserDetailsModel);

	public DashBoardModel getDashBoardCountDetails();

	public StringBuilder getCpuUsage();
	
	public Map<String, Object> getMapDetails(SchedulingSRModel schedulingSRModel, List<CustomerEntity> customerEntities);

	public Object getPageCount(int page, int count);

	public UserDetailsEntity getUserById(int articleId);

	List<String> getUserNameList(User user, UserDetailsModel userDetailsModel, Integer customerId);

	

}
