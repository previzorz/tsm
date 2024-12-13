package ru.previzorz.tsm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.previzorz.tsm.dto.CommentContentDTO;
import ru.previzorz.tsm.dto.CommentCreateDTO;
import ru.previzorz.tsm.dto.TaskContentDTO;
import ru.previzorz.tsm.dto.TaskUpdateDTO;
import ru.previzorz.tsm.entity.Comment;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.mapper.CommentMapper;
import ru.previzorz.tsm.mapper.TaskMapper;
import ru.previzorz.tsm.service.CommentService;
import ru.previzorz.tsm.service.TaskService;

@RestController
@RequestMapping("/api/user/tasks")
public class UserTaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public UserTaskController(TaskService taskService, TaskMapper taskMapper, CommentService commentService, CommentMapper commentMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Обновить статус задачи", description = "Позволяет пользователю обновить статус задачи.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи обновлён"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    public ResponseEntity<TaskContentDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskUpdateDTO taskUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Task task = taskService.updateTaskStatusByUser(taskId, taskUpdateDTO, currentUser);
        TaskContentDTO taskContentDto = taskMapper.toDto(task);

        return ResponseEntity.status(HttpStatus.OK).body(taskContentDto);
    }

    @PatchMapping("/{taskId}/comments")
    @Operation(summary = "Добавить комментарий к задаче", description = "Позволяет пользователю добавить комментарий к задаче.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    public ResponseEntity<CommentContentDTO> addCommentToTask(
            @PathVariable Long taskId,
            @RequestBody @Valid CommentCreateDTO commentCreateDTO,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Comment comment = commentService.addCommentToTaskByUser(taskId, commentCreateDTO, currentUser);
        CommentContentDTO commentContentDTO = commentMapper.toDto(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentContentDTO);
    }
}
