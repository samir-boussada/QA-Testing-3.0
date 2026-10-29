package com.zarastyle.service;

import com.zarastyle.model.Role;
import com.zarastyle.model.StoreBranch;
import com.zarastyle.model.User;
import com.zarastyle.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createStaffIfMissing(String email, String fullName, String rawPassword, Role role, StoreBranch branch) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User(email, fullName, passwordEncoder.encode(rawPassword), role, branch);
            return userRepository.save(user);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}
