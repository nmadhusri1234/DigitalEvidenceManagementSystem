package com.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.Case;

public class CaseDAO implements DAO<Case> {

    private Map<Integer, Case> cases = new HashMap<>();

    @Override
    public void add(Case caseObject) {

        cases.put(caseObject.getCaseId(), caseObject);
    }

    @Override
    public List<Case> getAll() {

        return new ArrayList<>(cases.values());
    }

    @Override
    public Case findBy(int id) {

        return cases.get(id);
    }

    @Override
    public void delete(int id) {

        cases.remove(id);
    }
    public void setCases(List<Case> caseList) {

        cases.clear();

        for (Case caseObject : caseList) {
            cases.put(
                    caseObject.getCaseId(),
                    caseObject
            );
        }
    }
}