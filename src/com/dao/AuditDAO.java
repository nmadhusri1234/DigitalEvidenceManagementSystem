package com.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.AuditLog;

public class AuditDAO implements DAO<AuditLog> {

    private Map<Integer, AuditLog> auditLogs = new HashMap<>();

    @Override
    public void add(AuditLog log) {

        auditLogs.put(
                log.getLogId(),
                log
        );
    }

    @Override
    public List<AuditLog> getAll() {

        return new ArrayList<>(auditLogs.values());
    }

    @Override
    public AuditLog findBy(int id) {

        return auditLogs.get(id);
    }

    @Override
    public void delete(int id) {

        auditLogs.remove(id);
    }
}