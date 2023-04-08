package com.smart.rct.usermanagement.entity;

import java.util.HashMap;
import java.util.Map;

import com.smart.rct.usermanagement.models.User;

public class UserSessionPool {

	private static UserSessionPool instance = null;

	// .--{user session Id,User}
	private Map<String, User> sessionMap = new HashMap<String, User>();

	private UserSessionPool() {
	}

	public static UserSessionPool getInstance() {
		if (instance == null) {
			synchronized (UserSessionPool.class) {
				instance = new UserSessionPool();
			}
		}
		return instance;
	}

	public void addUser(User user) {
		sessionMap.put(user.getTokenKey(), user);
	}

	public void removeUser(User user) {
		sessionMap.remove(user.getTokenKey());
	}

	public void removeUser(String sessionToken) {
		sessionMap.remove(sessionToken);
	}

	public boolean isUserInSession(User user) {
		return sessionMap.containsKey(user.getTokenKey());
	}

	public User getSessionUser(String key) {
		return sessionMap.get(key);
	}
}
