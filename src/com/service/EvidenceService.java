package com.service;

import java.io.IOException;
import java.util.List;

import com.dao.EvidenceDAO;
import com.exception.DuplicateEvidenceException;
import com.exception.EvidenceNotFoundException;
import com.file.EvidenceFileManager;
import com.model.Evidence;
import com.security.HashUtil;

public class EvidenceService {

    private EvidenceDAO evidenceDAO;

    public EvidenceService(EvidenceDAO evidenceDAO) {
        this.evidenceDAO = evidenceDAO;
    }

    // =========================================================
    // UPLOAD EVIDENCE
    // =========================================================

    public void uploadEvidence(Evidence evidence)
            throws DuplicateEvidenceException {

        // Check duplicate Evidence ID
        if (evidenceDAO.findBy(
                evidence.getEvidenceId()) != null) {

            throw new DuplicateEvidenceException(
                    "Evidence ID already exists: "
                    + evidence.getEvidenceId()
            );
        }

        String originalPath =
                evidence.getFilePath();

        // Remove quotes if user enters:
        // "C:\folder\file.txt"
        if (originalPath != null
                && originalPath.length() >= 2
                && originalPath.startsWith("\"")
                && originalPath.endsWith("\"")) {

            originalPath =
                    originalPath.substring(
                            1,
                            originalPath.length() - 1
                    );
        }

        try {

            // =================================================
            // STEP 1: COPY FILE INTO DEMS
            // =================================================

            String storedFilePath =
                    EvidenceFileManager.copyEvidenceFile(
                            originalPath,
                            evidence.getCaseDetails().getCaseId()
                    );

            // Store the DEMS-managed path
            evidence.setFilePath(
                    storedFilePath
            );

            System.out.println();
            System.out.println(
                    "Evidence file copied successfully."
            );

            System.out.println(
                    "Stored at: "
                    + storedFilePath
            );

            // =================================================
            // STEP 2: GENERATE SHA-256 HASH
            // =================================================

            String hash =
                    HashUtil.generateSHA256(
                            storedFilePath
                    );

            evidence.setHash(hash);

            System.out.println();
            System.out.println(
                    "SHA-256 Hash Generated:"
            );

            System.out.println(hash);

            // =================================================
            // STEP 3: STORE EVIDENCE OBJECT
            // =================================================

            evidenceDAO.add(evidence);

            System.out.println();
            System.out.println(
                    "Evidence uploaded successfully."
            );

        } catch (IOException e) {

            System.out.println();
            System.out.println(
                    "Unable to process evidence file."
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );

            System.out.println(
                    "Evidence was not stored."
            );
        }
    }

    // =========================================================
    // GET EVIDENCE BY ID
    // =========================================================

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

    // =========================================================
    // GET ALL EVIDENCE
    // =========================================================

    public List<Evidence> getAllEvidence() {

        return evidenceDAO.getAll();
    }

    // =========================================================
    // DELETE EVIDENCE
    // =========================================================

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

        // Delete actual evidence file
        String filePath =
                evidence.getFilePath();

        boolean fileDeleted =
                EvidenceFileManager.deleteEvidenceFile(
                        filePath
                );

        if (fileDeleted) {

            System.out.println(
                    "Physical evidence file deleted."
            );

        } else {

            System.out.println(
                    "Warning: Physical evidence file "
                    + "could not be deleted."
            );
        }

        // Delete Evidence object
        evidenceDAO.delete(evidenceId);

        System.out.println(
                "Evidence record deleted successfully."
        );
    }

    // =========================================================
    // VERIFY EVIDENCE
    // =========================================================

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