package ru.previzorz.tsm.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.previzorz.tsm.dto.TaskCreateDTO;
import ru.previzorz.tsm.dto.TaskFilterDTO;
import ru.previzorz.tsm.dto.TaskUpdateDTO;
import ru.previzorz.tsm.entity.*;
import ru.previzorz.tsm.exception.InvalidEnumValueException;
import ru.previzorz.tsm.mapper.TaskMapper;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.repository.UserRepository;
import ru.previzorz.tsm.specification.TaskSpecification;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Task createTask(TaskCreateDTO taskCreateDTO, User currentUser) {
        validateTaskStatus(taskCreateDTO.getStatus());
        validateTaskPriority(taskCreateDTO.getPriority());

        Task task = taskMapper.toEntity(taskCreateDTO, currentUser);

        return taskRepository.save(task);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Task updateTask(Long taskId, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        String title = taskUpdateDTO.getTitle();
        String description = taskUpdateDTO.getDescription();
        String status = taskUpdateDTO.getStatus();
        String priority = taskUpdateDTO.getPriority();

        if (title != null && !title.isBlank()) {
            task.setTitle(title);
        }

        if (description != null && !description.isBlank()) {
            task.setDescription(description);
        }

        if (status != null) {
            validateTaskStatus(status);

            task.setStatus(TaskStatus.valueOf(status));
        }

        if (priority != null) {
            validateTaskPriority(priority);

            task.setPriority(TaskPriority.valueOf(priority));
        }

        return taskRepository.save(task);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task not found with id " + taskId);
        }

        taskRepository.deleteById(taskId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Task assignExecutor(Long taskId, Long executorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        User executor = userRepository.findById(executorId)
                .orElseThrow(() -> new EntityNotFoundException("Executor not found with id " + executorId));

        task.setExecutor(executor);

        return taskRepository.save(task);
    }

    public Page<Task> findTasksWithFilter(TaskFilterDTO taskFilterDTO, Pageable pageable) {
        Long authorId = taskFilterDTO.getAuthorId();
        Long executorId = taskFilterDTO.getExecutorId();
        String status = taskFilterDTO.getStatus();
        String priority = taskFilterDTO.getPriority();

        Specification<Task> spec = Specification.where((Specification<Task>) (root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if (authorId != null) {
            validateUserExists(authorId, "Author");

            spec = spec.and(TaskSpecification.hasAuthor(authorId));
        }
        if (executorId != null) {
            validateUserExists(executorId, "Executor");

            spec = spec.and(TaskSpecification.hasExecutor(executorId));
        }
        if (status != null) {
            validateTaskStatus(status);

            spec = spec.and(TaskSpecification.hasStatus(status));
        }
        if (priority != null) {
            validateTaskPriority(priority);

            spec = spec.and(TaskSpecification.hasPriority(priority));
        }

        return taskRepository.findAll(spec, pageable);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Task updateTaskStatusByUser(Long taskId, TaskUpdateDTO taskUpdateDTO, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (!task.getExecutor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update the status of this task.");
        }

        task.setStatus(TaskStatus.valueOf(taskUpdateDTO.getStatus()));

        return taskRepository.save(task);
    }

    public void validateUserExists(Long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userType + " with id " + userId + " not found.");
        }
    }

    public void validateTaskStatus(String status) {
        try {
            TaskStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new InvalidEnumValueException("Invalid task status: " + status);
        }
    }

    public void validateTaskPriority(String priority) {
        try {
            TaskPriority.valueOf(priority);
        } catch (IllegalArgumentException ex) {
            throw new InvalidEnumValueException("Invalid task priority: " + priority);
        }
    }
}
