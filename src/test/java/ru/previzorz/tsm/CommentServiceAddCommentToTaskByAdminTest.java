package ru.previzorz.tsm;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.previzorz.tsm.dto.CommentCreateDTO;
import ru.previzorz.tsm.entity.Comment;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.TaskStatus;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.mapper.CommentMapper;
import ru.previzorz.tsm.repository.CommentRepository;
import ru.previzorz.tsm.repository.TaskRepository;
import ru.previzorz.tsm.service.CommentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceAddCommentToTaskByAdminTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void testAddCommentToTaskByAdmin_Success() {
        Long taskId = 1L;
        String commentContent = "This is a comment";
        Long currentUserId = 2L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        Comment comment = new Comment();
        comment.setContent(commentContent);
        when(commentMapper.toEntity(task, commentCreateDTO, currentUser)).thenReturn(comment);

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment addedComment = commentService.addCommentToTaskByAdmin(taskId, commentCreateDTO, currentUser);

        assertNotNull(addedComment);
        assertEquals(commentContent, addedComment.getContent());

        verify(commentRepository).save(comment);
    }

    @Test
    void testAddCommentToTaskByAdmin_EmptyCommentContent() {
        Long taskId = 1L;
        String commentContent = "";
        Long currentUserId = 2L;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.NEW);

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        assertThrows(IllegalArgumentException.class, () -> commentService.addCommentToTaskByAdmin(taskId, commentCreateDTO, currentUser));

        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void testAddCommentToTaskByAdmin_TaskNotFound() {
        Long taskId = 1L;
        String commentContent = "This is a comment";
        Long currentUserId = 2L;

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setContent(commentContent);

        User currentUser = new User();
        currentUser.setId(currentUserId);

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.addCommentToTaskByAdmin(taskId, commentCreateDTO, currentUser));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
