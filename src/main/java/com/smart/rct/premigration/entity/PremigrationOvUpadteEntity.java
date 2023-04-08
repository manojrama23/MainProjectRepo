package com.smart.rct.premigration.entity;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "PREMIGRATION_OV_UPDATE_RUN_TEST_RESULT")
public class PremigrationOvUpadteEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "GENERATE_INFO_AUDIT", referencedColumnName = "ID", nullable = false)
	private GenerateInfoAuditEntity generateAudEntity;
	
	@Column(name = "CURRENT_RESULT")
	private String currentResult;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "FILE_PATH", columnDefinition="LONGTEXT")
	private String filePath;

	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	public GenerateInfoAuditEntity getGenerateAudEntity() {
		return generateAudEntity;
	}

	public void setGenerateAudEntity(GenerateInfoAuditEntity generateAudEntity) {
		this.generateAudEntity = generateAudEntity;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getCurrentResult() {
		return currentResult;
	}

	public void setCurrentResult(String currentResult) {
		this.currentResult = currentResult;
	}

	

}
