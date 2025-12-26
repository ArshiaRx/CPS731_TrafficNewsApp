package com.trafficnewsapp.scheduler.services;

import com.trafficnewsapp.scheduler.dao.SubmissionDAO;
import com.trafficnewsapp.scheduler.models.Submission;

import java.util.List;

/**
 * OfflineSubmissionQueue (C11)
 * Business Logic Layer - Offline sync functionality
 */
public class OfflineSubmissionQueue {
    private SubmissionDAO submissionDAO;
    private boolean isOnline;
    
    public OfflineSubmissionQueue(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
        this.isOnline = true; // Assume online by default
    }
    
    /**
     * Add submission to queue
     * @param incidentData JSON string of incident data
     * @return Created submission
     */
    public Submission addSubmission(String incidentData) {
        Submission submission = new Submission();
        submission.setId(Submission.generateId());
        submission.setIncidentData(incidentData);
        submission.setStatus(isOnline ? "pending" : "pending");
        submission.setTimestamp(java.time.LocalDateTime.now());
        
        boolean saved = submissionDAO.saveSubmission(submission);
        return saved ? submission : null;
    }
    
    /**
     * Get all pending submissions
     * @return List of pending submissions
     */
    public List<Submission> getPendingSubmissions() {
        return submissionDAO.getPendingSubmissions();
    }
    
    /**
     * Process queue (mark as sent)
     * @param submissionId Submission ID to process
     * @return true if successful
     */
    public boolean processSubmission(String submissionId) {
        Submission submission = submissionDAO.getSubmissionById(submissionId);
        if (submission != null) {
            submission.setStatus("sent");
            return submissionDAO.saveSubmission(submission);
        }
        return false;
    }
    
    /**
     * Mark submission as failed
     * @param submissionId Submission ID
     * @return true if successful
     */
    public boolean markAsFailed(String submissionId) {
        Submission submission = submissionDAO.getSubmissionById(submissionId);
        if (submission != null) {
            submission.setStatus("failed");
            return submissionDAO.saveSubmission(submission);
        }
        return false;
    }
    
    public void setOnline(boolean online) {
        this.isOnline = online;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
}













