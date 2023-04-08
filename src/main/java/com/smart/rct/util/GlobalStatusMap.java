package com.smart.rct.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.smart.rct.duosocket.SocketSession;
import com.smart.rct.premigration.models.WorkFlowManagementPremigration;
import com.smart.rct.usermanagement.models.User;

public class GlobalStatusMap {

	public static ConcurrentHashMap<String, WorkFlowManagementPremigration> WFM_PRE_MIG_USECASES = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, User> loginUsersDetails = new ConcurrentHashMap<String, User>();
	public static ConcurrentHashMap<String, String> inProgressCiqStatusMap = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, User> schedulingLoginUsersDetails = new ConcurrentHashMap<String, User>();
	public static ConcurrentHashMap<String, SocketSession> socketSessionUser = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Boolean> socketSessionInCreation = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> userNeQueue = new ConcurrentHashMap<>();
}
