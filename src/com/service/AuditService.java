//package com.service;
//
//import com.audit.AuditLogger;
//import com.dao.AuditDAO;
//import com.model.AuditLog;
//
//public class AuditService {
//
//    private AuditDAO auditDAO;
//
//    public AuditService(AuditDAO auditDAO) {
//        this.auditDAO = auditDAO;
//    }
//
//    public void record(AuditLog log) {
//
//        auditDAO.add(log);
//
//        AuditLogger.log(log);
//    }
//}
package com.service;

import com.dao.AuditDAO;
import com.model.AuditLog;

public class AuditService {

    private AuditDAO auditDAO;

    public AuditService(AuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    public void record(AuditLog log) {

        auditDAO.add(log);

        System.out.println(
                "Audit log recorded successfully."
        );

        System.out.println(log);
    }
}