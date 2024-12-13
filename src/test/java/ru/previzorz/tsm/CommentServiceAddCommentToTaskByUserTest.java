package ru.previzorz.tsm;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.previzorz.tsm.entity.*;
import ru.previzorz.tsm.mapper.CommentMapper;
import ru.previzorz.tsm.repository.*;
import ru.previzorz.tsm.service.*;
import ru.previzorz.tsm.dto.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceAddCommentToTaskByUserTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void testAddCommentToTaskByUser_Success() {
        Long taskId = 1L;
        String commentContent = "This is a comment";
        Long currentUserId = 2L;
        Long executorId = 2L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        User user = new User();
        user.setId(executorId);
        task.setExecutor(user);

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        Comment comment = new Comment();
        comment.setContent(commentContent);
        when(commentMapper.toEntity(task, commentCreateDTO, currentUser)).thenReturn(comment);

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment addedComment = commentService.addCommentToTaskByUser(taskId, commentCreateDTO, currentUser);

        assertNotNull(addedComment);
        assertEquals(commentContent, addedComment.getContent());

        verify(commentRepository).save(comment);
    }

    @Test
    void testAddCommentToTaskByUser_EmptyCommentContent() {
        Long taskId = 1L;
        String commentContent = "";
        Long currentUserId = 2L;
        Long executorId = 2L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        User user = new User();
        user.setId(executorId);
        task.setExecutor(user);

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        assertThrows(IllegalArgumentException.class, () -> commentService.addCommentToTaskByUser(taskId, commentCreateDTO, currentUser));

        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void testAddCommentToTaskByUser_TaskNotFound() {
        Long taskId = 1L;
        String commentContent = "This is a comment";
        Long currentUserId = 2L;

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.addCommentToTaskByUser(taskId, commentCreateDTO, currentUser));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testAddCommentToTaskByUser_AccessDenied() {
        Long taskId = 1L;
        String commentContent = "This is a comment";
        Long currentUserId = 2L;
        Long executorId = 3L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);
        User user = new User();
        user.setId(executorId);
        task.setExecutor(user);

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> commentService.addCommentToTaskByUser(taskId, commentCreateDTO, currentUser));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}

