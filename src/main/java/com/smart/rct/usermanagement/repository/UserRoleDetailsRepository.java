package com.smart.rct.usermanagement.repository;

import java.util.List;

import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;

public interface UserRoleDetailsRepository {

	public boolean createRole(UserRoleDetailsEntity userRoleEntity);

	public boolean updateRole(UserRoleDetailsEntity userRoleEntity);

	public boolean deleteRole(int roleId);

	public List<UserRoleDetailsEntity> getRoleList();

	public boolean duplicateRole(UserRoleDetailsEntity userRoleEntity);

}
