package ru.previzorz.tsm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.previzorz.tsm.dto.TaskContentDTO;
import ru.previzorz.tsm.dto.TaskCreateDTO;
import ru.previzorz.tsm.entity.Comment;
import ru.previzorz.tsm.entity.Task;
import ru.previzorz.tsm.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "authorId", source = "task.author.id")
    @Mapping(target = "executorId", source = "task.executor.id")
    @Mapping(target = "comments", source = "task.comments")
    TaskContentDTO toDto(Task task);

    @Mapping(target = "author", source = "currentUser")
    Task toEntity(TaskCreateDTO taskCreateDto, User currentUser);

    default List<String> map(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(Comment::getContent)
                .collect(Collectors.toList());
    }
}
