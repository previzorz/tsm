package ru.previzorz.tsm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.previzorz.tsm.dto.*;
import ru.previzorz.tsm.entity.*;
import ru.previzorz.tsm.mapper.CommentMapper;
import ru.previzorz.tsm.mapper.TaskMapper;
import ru.previzorz.tsm.service.CommentService;
import ru.previzorz.tsm.service.TaskService;

@RestController
@RequestMapping("/api/admin/tasks")
public class AdminTaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public AdminTaskController(TaskService taskService, TaskMapper taskMapper, CommentService commentService, CommentMapper commentMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID", description = "Возвращает полную информацию о задаче по её идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskContentDTO> getTaskById(@PathVariable @NotNull Long id) {
        Task task = taskService.getTaskById(id);

        return taskContentDtoToResponseEntity(task, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Создать новую задачу", description = "Создаёт новую задачу для текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    public ResponseEntity<TaskContentDTO> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Task task = taskService.createTask(taskCreateDTO, currentUser);

        return taskContentDtoToResponseEntity(task, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить задачу", description = "Обновляет информацию о задаче по её идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    public ResponseEntity<TaskContentDTO> updateTask(@PathVariable @NotNull Long id, @RequestBody @Valid TaskUpdateDTO taskUpdateDTO) {
        Task task = taskService.updateTask(id, taskUpdateDTO);

        return taskContentDtoToResponseEntity(task, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable @NotNull Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/executor/{executorId}")
    @Operation(summary = "Назначить исполнителя", description = "Назначает исполнителя на задачу по её идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель назначен"),
            @ApiResponse(responseCode = "404", description = "Задача или исполнитель не найдены")
    })
    public ResponseEntity<TaskContentDTO> assignExecutor(
            @PathVariable @NotNull Long taskId,
            @PathVariable @NotNull Long executorId) {
        Task task = taskService.assignExecutor(taskId, executorId);

        return taskContentDtoToResponseEntity(task, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Поиск задач с фильтрами", description = "Возвращает задачи с возможностью фильтрации и пагинации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры фильтрации")
    })
    public Page<TaskContentDTO> findTasksWithFilter(
            @Valid TaskFilterDTO taskFilterDTO,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        Page<Task> tasks = taskService.findTasksWithFilter(
                taskFilterDTO,
                PageRequest.of(page, size));

        return ResponseEntity.ok(tasks.map(taskMapper::toDto)).getBody();
    }

    @PatchMapping("/{taskId}/comments")
    @Operation(summary = "Добавить комментарий к задаче", description = "Добавляет новый комментарий к задаче.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий добавлен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    public ResponseEntity<CommentContentDTO> addCommentToTask(
            @PathVariable @NotNull Long taskId,
            @RequestBody @Valid CommentCreateDTO commentCreateDTO, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Comment comment = commentService.addCommentToTaskByAdmin(taskId, commentCreateDTO, currentUser);
        CommentContentDTO commentContentDTO = commentMapper.toDto(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentContentDTO);
    }

    private ResponseEntity<TaskContentDTO> taskContentDtoToResponseEntity(Task task, HttpStatus status) {
        TaskContentDTO taskContentDto = taskMapper.toDto(task);
        return ResponseEntity.status(status).body(taskContentDto);
    }
}
