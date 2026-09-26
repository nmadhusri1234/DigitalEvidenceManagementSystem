package com.model;

import java.time.LocalDate;
import java.io.Serializable;

public class Case  implements Serializable{

    private int caseId;
    private String caseTitle;
    private String description;
    private User createdBy;
    private LocalDate createdDate;
    private CaseStatus status;

    public Case(int caseId,
            String caseTitle,
            String description,
            User createdBy,
            LocalDate createdDate,
            CaseStatus status) {

    this.caseId = caseId;
    this.caseTitle = caseTitle;
    this.description = description;
    this.createdBy = createdBy;
    this.createdDate = createdDate;
    this.status = status;
}
    
    public int getCaseId() {
        return caseId;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public String getDescription() {
        return description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public CaseStatus getStatus() {
        return status;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {

        return "Case{" +
                "caseId=" + caseId +
                ", caseTitle='" + caseTitle + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy.getUserName() +
                ", createdDate=" + createdDate +
                ", status=" + status +
                '}';
    }
    
    
}