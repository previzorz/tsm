package ru.previzorz.tsm;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.previzorz.tsm.dto.TaskUpdateDTO;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.TaskStatus;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.service.TaskService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUpdateTaskStatusByUserTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testUpdateTaskStatusByUser_Success() {
        Long taskId = 1L;
        String newStatus = "IN_PROGRESS";
        Long executorId = 2L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        User user = new User();
        user.setId(executorId);
        task.setExecutor(user);

        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setStatus(newStatus);

        User currentUser = new User();
        currentUser.setId(executorId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatusByUser(taskId, taskUpdateDTO, currentUser);

        assertNotNull(updatedTask);
        assertEquals(newStatus, updatedTask.getStatus().toString());

        verify(taskRepository).save(task);
    }

    @Test
    void testUpdateTaskStatusByUser_AccessDenied() {
        Long taskId = 1L;
        String newStatus = "IN_PROGRESS";
        Long executorId = 2L;
        Long currentUserId = 3L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        User executor = new User();
        executor.setId(executorId);
        task.setExecutor(executor);

        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setStatus(newStatus);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> taskService.updateTaskStatusByUser(taskId, taskUpdateDTO, currentUser));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatusByUser_TaskNotFound() {
        Long taskId = 1L;
        String newStatus = "IN_PROGRESS";

        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setStatus(newStatus);

        User currentUser = new User();
        currentUser.setId(2L);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTaskStatusByUser(taskId, taskUpdateDTO, currentUser));

        verify(taskRepository, never()).save(any(Task.class));
    }
}
