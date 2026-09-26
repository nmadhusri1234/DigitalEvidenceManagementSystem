package com.model;

import java.time.LocalDateTime;
import java.io.Serializable;

public class Evidence implements Serializable{


    private int evidenceId;
    private String evidenceName;
    private EvidenceType evidenceType;
    private String filePath;
    private User uploadedBy;
    private LocalDateTime uploadDate;
    private Case caseDetails;
    private String hash;
    
    
    public Evidence(int evidenceId,
            String evidenceName,
            EvidenceType evidenceType,
            String filePath,
            User uploadedBy,
            LocalDateTime uploadDate,
            Case caseDetails,
            String hash) {

this.evidenceId = evidenceId;
this.evidenceName = evidenceName;
this.evidenceType = evidenceType;
this.filePath = filePath;
this.uploadedBy = uploadedBy;
this.uploadDate = uploadDate;
this.caseDetails = caseDetails;
this.hash = hash;
}
    
    public int getEvidenceId() {
        return evidenceId;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public String getFilePath() {
        return filePath;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public Case getCaseDetails() {
        return caseDetails;
    }

    public String getHash() {
        return hash;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    
    @Override
    public String toString() {

        return "Evidence{" +
                "evidenceId=" + evidenceId +
                ", evidenceName='" + evidenceName + '\'' +
                ", evidenceType=" + evidenceType +
                ", filePath='" + filePath + '\'' +
                ", uploadedBy=" + uploadedBy.getUserName() +
                ", uploadDate=" + uploadDate +
                ", caseId=" + caseDetails.getCaseId() +
                ", hash='" + hash + '\'' +
                '}';
    }
    
	
}
