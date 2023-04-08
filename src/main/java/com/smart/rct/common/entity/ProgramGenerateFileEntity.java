package com.smart.rct.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROGRAM_GENERATE_FILE_INFO")
public class ProgramGenerateFileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "PROGRAME_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerDetailsEntity programDetailsEntity;
	
	@Column(name = "ENV", nullable = false)
	private String env;
	
	@Column(name = "CSV", nullable = false)
	private String csv;
	
	@Column(name = "COMM_SCRIPT", nullable = false)
	private String commissionScript;
	
	@Column(name = "ENDC", nullable = false)
	private String endc;
	
	public String getEndc() {
		return endc;
	}

	public void setEndc(String endc) {
		this.endc = endc;
	}

	public String getAll() {
		return all;
	}

	public void setAll(String all) {
		this.all = all;
	}

	@Column(name = "ALLTemplates", nullable = false)
	private String all;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CustomerDetailsEntity getProgramDetailsEntity() {
		return programDetailsEntity;
	}

	public void setProgramDetailsEntity(CustomerDetailsEntity programDetailsEntity) {
		this.programDetailsEntity = programDetailsEntity;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getCsv() {
		return csv;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public String getCommissionScript() {
		return commissionScript;
	}

	public void setCommissionScript(String commissionScript) {
		this.commissionScript = commissionScript;
	}
	
}
