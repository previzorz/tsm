package ru.previzorz.tsm.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateDTO {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
