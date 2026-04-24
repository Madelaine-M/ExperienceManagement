package model;

import java.time.LocalDateTime;

public class CustomerNote {
    private int id;
    private int customerId;
    private int advisorId;
    private String noteText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerNote() {
    }

    public CustomerNote(int id, int customerId, int advisorId, String noteText, LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.advisorId = advisorId;
        this.noteText = noteText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(int advisorId) {
        this.advisorId = advisorId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
