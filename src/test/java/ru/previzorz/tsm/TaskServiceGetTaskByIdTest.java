package ru.previzorz.tsm;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.service.TaskService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceGetTaskByIdTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTaskByIdSuccess() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Sample Task");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Sample Task", result.getTitle());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testGetTaskByIdEntityNotFoundException() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(taskId));

        assertEquals("Task not found with id " + taskId, thrown.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }
}
