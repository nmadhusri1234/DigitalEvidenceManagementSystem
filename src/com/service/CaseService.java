package com.service;

import com.dao.CaseDAO;
import com.exception.DuplicateCaseException;
import com.exception.CaseNotFoundException;
import com.model.Case;

public class CaseService {

    private CaseDAO caseDAO;

    public CaseService(CaseDAO caseDAO) {
        this.caseDAO = caseDAO;
    }

    public void createCase(Case caseObject)
            throws DuplicateCaseException {

        if (caseDAO.findBy(caseObject.getCaseId()) != null) {

            throw new DuplicateCaseException(
                    "Case ID already exists: "
                    + caseObject.getCaseId()
            );
        }

        caseDAO.add(caseObject);

        System.out.println(
                "Case created successfully."
        );
    }

    public Case getCaseById(int caseId)
            throws CaseNotFoundException {

        Case caseObject = caseDAO.findBy(caseId);

        if (caseObject == null) {

            throw new CaseNotFoundException(
                    "Case not found with ID: "
                    + caseId
            );
        }

        return caseObject;
    }

    public void deleteCase(int caseId)
            throws CaseNotFoundException {

        Case caseObject = caseDAO.findBy(caseId);

        if (caseObject == null) {

            throw new CaseNotFoundException(
                    "Cannot delete. Case not found with ID: "
                    + caseId
            );
        }

        caseDAO.delete(caseId);

        System.out.println(
                "Case deleted successfully."
        );
    }
}