package ru.previzorz.tsm;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import ru.previzorz.tsm.dto.TaskFilterDTO;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.exception.InvalidEnumValueException;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.repository.UserRepository;
import ru.previzorz.tsm.service.TaskService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TaskServiceFindTasksWithFilterTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testFindTasksWithFilterWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);

        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(2L);
        List<Task> taskList = List.of(task1, task2);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        Page<Task> result = taskService.findTasksWithFilter(new TaskFilterDTO(), pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());

        verify(taskRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindTasksWithFilterWithAuthorFilter() {
        Long authorId = 1L;
        TaskFilterDTO filterDTO = new TaskFilterDTO();
        filterDTO.setAuthorId(authorId);

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(authorId)).thenReturn(true);

        Task task = new Task();
        task.setId(1L);
        task.setAuthor(new User());
        List<Task> taskList = List.of(task);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        Page<Task> result = taskService.findTasksWithFilter(filterDTO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());

        verify(taskRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindTasksWithFilterWithExecutorFilter() {
        Long executorId = 2L;
        TaskFilterDTO filterDTO = new TaskFilterDTO();
        filterDTO.setExecutorId(executorId);

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(executorId)).thenReturn(true);

        Task task = new Task();
        task.setId(1L);
        task.setExecutor(new User());
        List<Task> taskList = List.of(task);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());

        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);

        Page<Task> result = taskService.findTasksWithFilter(filterDTO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());

        verify(taskRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindTasksWithFilterWithInvalidStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilterDTO filterDTO = new TaskFilterDTO();
        filterDTO.setStatus("INVALID_STATUS");

        assertThrows(InvalidEnumValueException.class, () -> taskService.findTasksWithFilter(filterDTO, pageable));

        verify(taskRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindTasksWithFilterWithInvalidPriority() {
        Pageable pageable = PageRequest.of(0, 10);
        TaskFilterDTO filterDTO = new TaskFilterDTO();
        filterDTO.setPriority("INVALID_PRIORITY");

        assertThrows(InvalidEnumValueException.class, () -> taskService.findTasksWithFilter(filterDTO, pageable));

        verify(taskRepository, never()).findAll(any(Specification.class), eq(pageable));
    }
}