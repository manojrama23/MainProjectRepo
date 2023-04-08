package com.smart.rct.usermanagement.repository;

import java.util.Map;

import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;

public interface UserDetailsRepository {

	public boolean createUser(UserDetailsEntity userEntity);

	public boolean deleteUser(int userId);

	public Map<String, Object> getUserList(User user, UserDetailsModel userDetailsModel, Integer customerId, int page, int count);

	public boolean updateUser(UserDetailsEntity userEntity);

	public boolean duplicateUser(UserDetailsModel userUserDetailsModel);

	public Object getPageCount(int page, int count);

	public UserDetailsEntity getUserById(int articleId);

	public UserDetailsEntity getUserByRole(String role);
	
	Map<String, Object> getUserNameList(User user, UserDetailsModel userDetailsModel, Integer customerId);

}
