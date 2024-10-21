package com.tata.bank.service;

import com.tata.bank.dto.*;
import com.tata.bank.entity.*;
import com.tata.bank.repository.RoleRepository;
import com.tata.bank.repository.UserRepository;
import com.tata.bank.shared.GenericSpecification;
import com.tata.bank.shared.PageRequestFromOne;
import com.tata.bank.shared.SpecificationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserResponseDto createUser(UserDto userDto, List<UUID> roleIds, UUID uuidCreatedBy) {
        try {
            List<Role> roles = fetchRoles(roleIds);
            User user = modelMapper.map(userDto, User.class);
            assignRolesToUser(user, roles, uuidCreatedBy);
            user.setCreatedBy(uuidCreatedBy);
            user.setUserStatus(EnumUserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating user: " + e.getMessage(), e);
        }
    }

    public ApiResponseDto<UserResponseDto> updateUser(UserDto userDto, List<UUID> roleIds, UUID uuidUpdatedBy) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            modelMapper.map(userDto, existingUser);
            List<Role> roles = fetchRoles(roleIds);
            updateRoles(existingUser, roles);

            existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
            existingUser.setUpdatedBy(uuidUpdatedBy);
            existingUser.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(existingUser);
            UserResponseDto updatedUserDto = modelMapper.map(savedUser, UserResponseDto.class);

            return new ApiResponseDto<>(true, "User updated successfully.", updatedUserDto);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating user: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching users: " + e.getMessage(), e);
        }
    }

    public Optional<UserResponseDto> getUserById(UUID userId) {
        try {
            return userRepository.findById(userId)
                    .map(user -> modelMapper.map(user, UserResponseDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching user by ID: " + e.getMessage(), e);
        }
    }

    public User loadUserByUsernameAndDeletedByIsNull(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findFirstByUsernameAndDeletedByIsNull(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            return user;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage(), e);
        }
    }

    public Page<UserDto> getFilteredUsers(FilteredRequestDto filteredRequestDto) {
        try {
            Specification<User> userSpec = new GenericSpecification<>(User.class)
                    .getSpecification(filteredRequestDto);
            Sort sort = SpecificationUtil.createSort(filteredRequestDto.getSortOrders());
            Pageable pageable = PageRequestFromOne.of(filteredRequestDto.getPage(), filteredRequestDto.getPageSize(), sort);

            Page<User> users = userRepository.findAll(userSpec, pageable);
            return users.map(user -> modelMapper.map(user, UserDto.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching filtered users: " + e.getMessage(), e);
        }
    }

    public void deleteUser(UUID userId, UUID deletedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            user.setDeletedBy(deletedBy);
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting user: " + e.getMessage(), e);
        }
    }

    private List<Role> fetchRoles(List<UUID> roleIds) {
        List<Role> roles = roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            throw new IllegalArgumentException("No valid roles found.");
        }
        return roles;
    }

    private void assignRolesToUser(User user, List<Role> roles, UUID createdBy) {
        if (user.getUserRoles() == null) {
            user.setUserRoles(new HashSet<>());
        }

        roles.forEach(role -> {
            boolean roleExists = user.getUserRoles().stream()
                    .anyMatch(userRole -> userRole.getRole().equals(role));
            if (!roleExists) {
                UserRole userRole = new UserRole(user, role);
                userRole.setCreatedBy(createdBy);
                userRole.setCreatedAt(LocalDateTime.now());
                user.getUserRoles().add(userRole);
            }
        });
    }

    private void updateRoles(User user, List<Role> newRoles) {
        Set<Role> currentRoles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        Set<UserRole> rolesToRemove = user.getUserRoles().stream()
                .filter(userRole -> !newRoles.contains(userRole.getRole()))
                .collect(Collectors.toSet());

        rolesToRemove.forEach(roleToRemove -> {
            user.getUserRoles().remove(roleToRemove);
            roleToRemove.getRole().getUserRoles().remove(roleToRemove);
        });

        newRoles.stream()
                .filter(role -> !currentRoles.contains(role))
                .forEach(user::addRole);
    }
}
