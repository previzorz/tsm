package ru.previzorz.tsm;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.previzorz.tsm.dto.TaskUpdateDTO;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.TaskPriority;
import ru.previzorz.tsm.entity.TaskStatus;
import ru.previzorz.tsm.exception.InvalidEnumValueException;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.service.TaskService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUpdateTaskTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testUpdateTask() {
        Long taskId = 1L;
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setTitle("Updated Title");
        taskUpdateDTO.setDescription("Updated Description");
        taskUpdateDTO.setStatus("IN_PROGRESS");
        taskUpdateDTO.setPriority("HIGH");

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Old Title");
        task.setDescription("Old Description");
        task.setStatus(TaskStatus.NEW);
        task.setPriority(TaskPriority.LOW);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTask(taskId, taskUpdateDTO);

        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(TaskPriority.HIGH, updatedTask.getPriority());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void testUpdateTaskWhenTaskNotFound() {
        Long taskId = 1L;
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));

        verify(taskRepository).findById(taskId);
    }

    @Test
    void testUpdateTaskWithInvalidStatus() {
        Long taskId = 1L;
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setStatus("INVALID_STATUS");

        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(InvalidEnumValueException.class, () -> taskService.updateTask(taskId, taskUpdateDTO));

        verify(taskRepository).findById(taskId);
    }

    @Test
    void testUpdateTaskWithNullTitle() {
        Long taskId = 1L;
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setTitle(null);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Old Title");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTask(taskId, taskUpdateDTO);

        assertNotNull(updatedTask);
        assertEquals("Old Title", updatedTask.getTitle());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }
}

