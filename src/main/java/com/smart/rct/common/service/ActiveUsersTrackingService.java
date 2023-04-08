package com.smart.rct.common.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;

public interface ActiveUsersTrackingService {

	boolean savedetail(ActiveUsersTracking activeUserTracking);

	
}
