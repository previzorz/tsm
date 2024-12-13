package ru.previzorz.tsm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.previzorz.tsm.dto.TaskCreateDTO;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.exception.InvalidEnumValueException;
import ru.previzorz.tsm.mapper.TaskMapper;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.service.TaskService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceCreateTaskTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testCreateTask() {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setStatus("NEW");
        taskCreateDTO.setPriority("HIGH");

        User currentUser = new User();
        currentUser.setId(1L);

        Task task = new Task();
        task.setId(1L);

        when(taskMapper.toEntity(taskCreateDTO, currentUser)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(taskCreateDTO, currentUser);

        assertNotNull(createdTask);
        assertEquals(1L, createdTask.getId());

        verify(taskMapper).toEntity(taskCreateDTO, currentUser);
        verify(taskRepository).save(task);
    }

    @Test
    void testCreateTaskWithInvalidStatus() {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setStatus("INVALID_STATUS");
        taskCreateDTO.setPriority("HIGH");

        User currentUser = new User();
        currentUser.setId(1L);

        assertThrows(InvalidEnumValueException.class, () -> taskService.createTask(taskCreateDTO, currentUser));
    }

    @Test
    void testCreateTaskWithInvalidPriority() {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setStatus("NEW");
        taskCreateDTO.setPriority("DDD");

        User currentUser = new User();
        currentUser.setId(1L);

        assertThrows(InvalidEnumValueException.class, () -> taskService.createTask(taskCreateDTO, currentUser));
    }
}
