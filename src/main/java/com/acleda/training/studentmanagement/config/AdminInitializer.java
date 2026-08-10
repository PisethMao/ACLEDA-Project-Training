package com.acleda.training.studentmanagement.config;

import com.acleda.training.studentmanagement.features.auth.AppUser;
import com.acleda.training.studentmanagement.features.auth.AppUserRepository;
import com.acleda.training.studentmanagement.features.auth.UserRole;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        String username = "admin";
        if (!appUserRepository.existsByUsernameIgnoreCase(username)) {
            AppUser admin = new AppUser();
            admin.setUsername(username);
            admin.setPassword(
                    passwordEncoder.encode("Admin@2026")
            );
            admin.setRole(UserRole.ADMIN);
            admin.setEnabled(true);
            appUserRepository.save(admin);
        }
    }
}
