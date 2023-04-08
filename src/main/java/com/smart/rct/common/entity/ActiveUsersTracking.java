package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "active_users_tracking")
public class ActiveUsersTracking {

		private Integer id;
		private Integer activeUsers;
		private Integer activeSessions;
		private String timestamp;
		
		@Column(name = "active_sessions")
		public Integer getActiveSessions() {
			return activeSessions;
		}

		public void setActiveSessions(Integer activeSessions) {
			this.activeSessions = activeSessions;
		}

		@Column(name = "timestamp")
		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		
	
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		@Column(name = "active_users")
		public Integer getActiveUsers() {
			return activeUsers;
		}

		public void setActiveUsers(Integer activeUsers) {
			this.activeUsers = activeUsers;
		}
		
		

	}