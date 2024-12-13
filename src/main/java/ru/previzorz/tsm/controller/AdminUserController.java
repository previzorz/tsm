package ru.previzorz.tsm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.previzorz.tsm.dto.UserContentDTO;
import ru.previzorz.tsm.dto.UserCredentialsUpdateDTO;
import ru.previzorz.tsm.entity.Role;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.mapper.UserMapper;
import ru.previzorz.tsm.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public AdminUserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserContentDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserContentDTO userContentDTO = userMapper.toDto(user);

        return ResponseEntity.ok(userContentDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<UserContentDTO> getUserByEmail(@RequestParam @Email @NotNull String email) {
        User user = userService.getUserByEmail(email);
        UserContentDTO userContentDTO = userMapper.toDto(user);

        return ResponseEntity.ok(userContentDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserContentDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserContentDTO> userContentDTO = users.stream()
                .map(user -> {
                    UserContentDTO dto = new UserContentDTO();
                    dto.setEmail(user.getEmail());
                    dto.setRoleName(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.joining(", ")));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userContentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseEntity<String>> updateUserCredentials(
            @PathVariable Long id,
            @RequestBody @Valid UserCredentialsUpdateDTO userCredentialsUpdateDTO) {

        return ResponseEntity.ok(userService.updateUserCredentials(id, userCredentialsUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserContentDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody @Valid Set<String> roleNames) {
        User updatedUser = userService.changeAllUserRoles(id, roleNames);
        UserContentDTO userContentDTO = new UserContentDTO(
                updatedUser.getEmail(),
                updatedUser.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()).toString()
        );
        return ResponseEntity.ok(userContentDTO);
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserContentDTO> addRolesToUser(
            @PathVariable Long id,
            @RequestBody @Valid Set<String> roleNames) {
        User updatedUser = userService.addUserRoles(id, roleNames);
        UserContentDTO userContentDTO = new UserContentDTO(
                updatedUser.getEmail(),
                updatedUser.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()).toString()
        );
        return ResponseEntity.ok(userContentDTO);
    }
}
