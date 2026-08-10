package com.acleda.training.studentmanagement.features.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@NullMarked
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        String normalizedUsername = username
                .trim()
                .toLowerCase(Locale.ROOT);
        AppUser appUser = appUserRepository
                .findByUsernameIgnoreCase(normalizedUsername)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Invalid username or password"
                        )
                );
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities("ROLE_" + appUser.getRole().name())
                .disabled(!appUser.isEnabled())
                .build();
    }
}