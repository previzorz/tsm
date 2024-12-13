package ru.previzorz.tsm.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.previzorz.tsm.dto.CommentCreateDTO;
import ru.previzorz.tsm.entity.Comment;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.mapper.CommentMapper;
import ru.previzorz.tsm.repository.CommentRepository;
import ru.previzorz.tsm.repository.TaskRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Comment addCommentToTaskByAdmin(Long taskId, CommentCreateDTO commentCreateDTO, User currentUser) {
        if (commentCreateDTO.getContent() == null || commentCreateDTO.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content cannot be empty.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        Comment comment = commentMapper.toEntity(task, commentCreateDTO, currentUser);

        return commentRepository.save(comment);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Comment addCommentToTaskByUser(Long taskId, CommentCreateDTO commentCreateDTO, User currentUser) {
        if (commentCreateDTO.getContent() == null || commentCreateDTO.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content cannot be empty.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        if (!task.getExecutor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to comment on this task.");
        }

        Comment comment = commentMapper.toEntity(task, commentCreateDTO, currentUser);

        return commentRepository.save(comment);
    }
}
