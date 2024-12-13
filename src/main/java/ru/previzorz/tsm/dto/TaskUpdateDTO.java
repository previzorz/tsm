package ru.previzorz.tsm.dto;

import jakarta.validation.constraints.Pattern;

public class TaskUpdateDTO {
    private String title;
    private String description;
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Invalid status value")
    private String status;
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Invalid priority value")
    private String priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
