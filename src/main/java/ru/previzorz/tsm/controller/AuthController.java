package ru.previzorz.tsm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.previzorz.tsm.config.JwtTokenProvider;
import ru.previzorz.tsm.dto.LoginDTO;
import ru.previzorz.tsm.dto.UserContentDTO;
import ru.previzorz.tsm.dto.UserRegistrationDTO;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.mapper.UserMapper;
import ru.previzorz.tsm.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Operation(summary = "Регистрация нового пользователя", description = "Создаёт нового пользователя в системе и возвращает его данные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserContentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для регистрации")
    })
    @PostMapping("/register")
    public ResponseEntity<UserContentDTO> register(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        User user = userService.registerUser(userRegistrationDTO);
        UserContentDTO userContentDTO = userMapper.toDto(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userContentDTO);
    }

    @Operation(summary = "Логин пользователя", description = "Аутентифицирует пользователя и генерирует JWT токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно сгенерирован",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Неверные данные для входа")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        return ResponseEntity.ok(jwtTokenProvider.generateToken((User) authentication.getPrincipal()));
    }
}
