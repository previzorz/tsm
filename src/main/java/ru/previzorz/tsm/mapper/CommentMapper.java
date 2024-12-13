package ru.previzorz.tsm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.previzorz.tsm.dto.CommentContentDTO;
import ru.previzorz.tsm.dto.CommentCreateDTO;
import ru.previzorz.tsm.entity.Comment;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "task", source = "task")
    @Mapping(target = "author", source = "currentUser")
    @Mapping(target = "content", source = "commentCreateDTO.content")
    Comment toEntity(Task task, CommentCreateDTO commentCreateDTO, User currentUser);

    @Mapping(target = "authorId", source = "comment.author.id")
    CommentContentDTO toDto(Comment comment);
}
