package ru.previzorz.tsm.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.previzorz.tsm.config.JwtTokenProvider;
import ru.previzorz.tsm.dto.UserRegistrationDTO;
import ru.previzorz.tsm.dto.UserCredentialsUpdateDTO;
import ru.previzorz.tsm.entity.Role;
import ru.previzorz.tsm.entity.User;
import ru.previzorz.tsm.exception.UserAlreadyExistsException;
import ru.previzorz.tsm.mapper.UserMapper;
import ru.previzorz.tsm.repository.RoleRepository;
import ru.previzorz.tsm.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User registerUser(UserRegistrationDTO userRegistrationDTO) {
        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        userRegistrationDTO.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

        User user = userMapper.toEntity(userRegistrationDTO);
        user.setRoles(new HashSet<>(Set.of(userRole)));

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<String> updateUserCredentials(Long userId, UserCredentialsUpdateDTO userCredentialsUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (userCredentialsUpdateDTO.getEmail() != null && !userCredentialsUpdateDTO.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(userCredentialsUpdateDTO.getEmail())) {
                throw new UserAlreadyExistsException("The email is already in use: " + userCredentialsUpdateDTO.getEmail());
            }

            if (userCredentialsUpdateDTO.getCurrentPassword() == null || userCredentialsUpdateDTO.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("Current password must be provided to change email.");
            }

            if (!passwordEncoder.matches(userCredentialsUpdateDTO.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }

            user.setEmail(userCredentialsUpdateDTO.getEmail());
        }

        if (userCredentialsUpdateDTO.getPassword() != null && !userCredentialsUpdateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userCredentialsUpdateDTO.getPassword()));
        }

        user.setTokenVersion(user.getTokenVersion() + 1);

        userRepository.save(user);

        return ResponseEntity.ok(jwtTokenProvider.generateToken(user));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->new EntityNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User changeAllUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User addUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Set<Role> rolesToAdd = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.getRoles().addAll(rolesToAdd);

        return userRepository.save(user);
    }
}
