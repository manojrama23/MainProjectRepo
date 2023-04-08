package com.smart.rct.usermanagement.service;

import java.util.List;

import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;

public interface UserRoleDetailsService {

	public boolean createRole(UserRoleDetailsEntity userRoleEntity);

	public boolean updateRole(UserRoleDetailsEntity userRoleEntity);

	public boolean deleteRole(Integer roleId);

	public List<UserRoleDetailsEntity> getRoleList();

	public boolean duplicateRole(UserRoleDetailsEntity userRoleEntity);

}
