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
@Table(name = "SCHEDULING_REPORTS_TEMPLATE")
public class SchedulingReportsTemplateEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "CUSTOMER_NAME_ID", referencedColumnName = "ID", nullable= false)
	private CustomerEntity customerEntity;
	
	@Column(name = "PARAMETER_NAME", nullable = false)
	private String label;
	
	@Column(name = "PARAMETER_VALUE", nullable = false,columnDefinition="LONGTEXT")
	private String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}


