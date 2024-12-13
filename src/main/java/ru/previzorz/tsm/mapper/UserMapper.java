package ru.previzorz.tsm.mapper;

import org.mapstruct.Mapper;
import ru.previzorz.tsm.dto.UserContentDTO;
import ru.previzorz.tsm.dto.UserRegistrationDTO;
import ru.previzorz.tsm.entity.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    UserContentDTO toDto(User user);

    User toEntity(UserRegistrationDTO userRegistrationDTO);
}