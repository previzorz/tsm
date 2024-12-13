package ru.previzorz.tsm.dto;

import jakarta.validation.constraints.Pattern;

public class TaskFilterDTO {

    private Long authorId;
    private Long executorId;
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Invalid status value")
    private String status;
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Invalid priority value")
    private String priority;

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
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
