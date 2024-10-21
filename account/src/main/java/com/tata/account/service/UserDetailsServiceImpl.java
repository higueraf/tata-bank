package com.tata.account.service;

import com.tata.account.entity.User;
import com.tata.account.entity.EnumUserStatus;
import com.tata.account.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsernameAndDeletedByIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        if (!passwordEncoder.matches("SecurePassword123", user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getUserStatus() == EnumUserStatus.ACTIVE,
                true,
                true,
                true,
                getAuthorities(user)
        );
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(userRole ->
                        new SimpleGrantedAuthority(
                                "ROLE_" + userRole.getRole().getName().toUpperCase()))
                .distinct()
                .collect(Collectors.toList());
    }
}
