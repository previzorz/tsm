package ru.previzorz.tsm;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.repository.UserRepository;
import ru.previzorz.tsm.service.TaskService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceAssignExecutorTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testAssignExecutorWhenTaskAndExecutorExist() {
        Long taskId = 1L;
        Long executorId = 2L;

        Task task = new Task();
        task.setId(taskId);

        User executor = new User();
        executor.setId(executorId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(executorId)).thenReturn(Optional.of(executor));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.assignExecutor(taskId, executorId);

        assertNotNull(updatedTask);
        assertEquals(executor, updatedTask.getExecutor());

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(executorId);
        verify(taskRepository).save(task);
    }

    @Test
    void testAssignExecutorWhenTaskNotFound() {
        Long taskId = 1L;
        Long executorId = 2L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.assignExecutor(taskId, executorId));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testAssignExecutorWhenExecutorNotFound() {
        Long taskId = 1L;
        Long executorId = 2L;

        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(executorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.assignExecutor(taskId, executorId));

        verify(taskRepository, never()).save(any(Task.class));
    }
}

