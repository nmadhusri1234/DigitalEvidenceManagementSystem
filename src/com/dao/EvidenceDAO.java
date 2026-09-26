package com.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.Evidence;

public class EvidenceDAO implements DAO<Evidence> {

    private Map<Integer, Evidence> evidenceMap = new HashMap<>();

    @Override
    public void add(Evidence evidence) {

        evidenceMap.put(
                evidence.getEvidenceId(),
                evidence
        );
    }

    @Override
    public List<Evidence> getAll() {

        return new ArrayList<>(evidenceMap.values());
    }

    @Override
    public Evidence findBy(int id) {

        return evidenceMap.get(id);
    }

    @Override
    public void delete(int id) {

        evidenceMap.remove(id);
    }
    
    public void setEvidence(
            List<Evidence> evidenceList) {

        evidenceMap.clear();

        for (Evidence evidence : evidenceList) {

            evidenceMap.put(
                    evidence.getEvidenceId(),
                    evidence
            );
        }
    }
}