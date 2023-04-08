package com.smart.rct.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "AUDIT_TRAIL")
public class AuditTrailEntity {

		private Integer id;
		private String eventName;
		private String eventSubName;
		private String actionPerformed;
		private String userName;
		private String eventDescription;
		private Date actionPerformedDate;
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		@Column(name = "EVENT_NAME")
		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		
		@Column(name = "EVENT_SUBNAME")
		public String getEventSubName() {
			return eventSubName;
		}

		public void setEventSubName(String eventSubName) {
			this.eventSubName = eventSubName;
		}
		
		@Column(name = "ACTION_PERFORMED")
		public String getActionPerformed() {
			return actionPerformed;
		}

		public void setActionPerformed(String actionPerformed) {
			this.actionPerformed = actionPerformed;
		}

		@Column(name = "USER_NAME")
		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		@Column(name = "EVENT_DESCRIPTION",columnDefinition="LONG_TEXT")
		public String getEventDescription() {
			return eventDescription;
		}

		public void setEventDescription(String eventDescription) {
			this.eventDescription = eventDescription;
		}

		@Column(name = "ACTION_PERFORMED_DATE")
		public Date getActionPerformedDate() {
			return actionPerformedDate;
		}

		public void setActionPerformedDate(Date actionPerformedDate) {
			this.actionPerformedDate = actionPerformedDate;
		}

	}