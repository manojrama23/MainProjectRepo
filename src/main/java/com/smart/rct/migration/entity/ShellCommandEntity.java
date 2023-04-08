package com.smart.rct.migration.entity;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SHELL_COMMAND_INFO")
public class ShellCommandEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CMD_NAME")
	private String cmdName;

	@Column(name = "COLUMN_NAME")
	private String columnName;

	@Column(name = "ALIGNMENT")
	private String alignment;

	@Column(name = "COLUMN_ORDER")
	private String columnOrder;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

}
