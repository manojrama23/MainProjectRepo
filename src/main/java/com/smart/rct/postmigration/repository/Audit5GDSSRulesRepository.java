package com.smart.rct.postmigration.repository;

import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;

public interface Audit5GDSSRulesRepository {

	Audit5GDSSRulesEntity getAudit5GDSSRulesEntityById(int auditRuleId);

}
