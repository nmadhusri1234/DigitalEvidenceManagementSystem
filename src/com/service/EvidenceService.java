package com.service;

import java.io.IOException;
import java.util.List;

import com.dao.EvidenceDAO;
import com.exception.DuplicateEvidenceException;
import com.exception.EvidenceNotFoundException;
import com.model.Evidence;
import com.security.HashUtil;

public class EvidenceService {

    private EvidenceDAO evidenceDAO;

    public EvidenceService(EvidenceDAO evidenceDAO) {
        this.evidenceDAO = evidenceDAO;
    }

    public void uploadEvidence(Evidence evidence)
            throws DuplicateEvidenceException {

        if (evidenceDAO.findBy(evidence.getEvidenceId()) != null) {
            throw new DuplicateEvidenceException(
                    "Evidence ID already exists: "
                    + evidence.getEvidenceId()
            );
        }

        try {
            String hash =
                    HashUtil.generateSHA256(
                            evidence.getFilePath()
                    );

            evidence.setHash(hash);

            System.out.println();
            System.out.println(
                    "SHA-256 Hash Generated:"
            );
            System.out.println(hash);

        } catch (IOException e) {

            evidence.setHash(
                    "HASH_NOT_GENERATED"
            );

            System.out.println();
            System.out.println(
                    "Warning: Unable to generate "
                    + "SHA-256 hash."
            );

            System.out.println(
                    "Evidence will still be stored."
            );
        }

        evidenceDAO.add(evidence);

        System.out.println(
                "Evidence uploaded successfully."
        );
    }

    public List<Evidence> getAllEvidence() {
        return evidenceDAO.getAll();
    }

    public Evidence getEvidenceById(int evidenceId)
            throws EvidenceNotFoundException {

        Evidence evidence =
                evidenceDAO.findBy(evidenceId);

        if (evidence == null) {
            throw new EvidenceNotFoundException(
                    "Evidence not found with ID: "
                    + evidenceId
            );
        }

        return evidence;
    }

    public void deleteEvidence(int evidenceId)
            throws EvidenceNotFoundException {

        Evidence evidence =
                evidenceDAO.findBy(evidenceId);

        if (evidence == null) {
            throw new EvidenceNotFoundException(
                    "Cannot delete. Evidence not found with ID: "
                    + evidenceId
            );
        }

        evidenceDAO.delete(evidenceId);

        System.out.println(
                "Evidence deleted successfully."
        );
    }

    public boolean verifyEvidence(int evidenceId)
            throws EvidenceNotFoundException {

        Evidence evidence =
                getEvidenceById(evidenceId);

        try {
            String currentHash =
                    HashUtil.generateSHA256(
                            evidence.getFilePath()
                    );

            return currentHash.equals(
                    evidence.getHash()
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable to read evidence file."
            );

            return false;
        }
    }
}
