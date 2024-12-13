package ru.previzorz.tsm.mapper;

import org.mapstruct.Mapper;
import ru.previzorz.tsm.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    String toDto(Role role);

    Role toEntity(String roleName);
}
