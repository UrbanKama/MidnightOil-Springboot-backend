package com.devrassicpark.midnightoil.Config;

import com.devrassicpark.midnightoil.models.Role;
import com.devrassicpark.midnightoil.models.User;
import com.devrassicpark.midnightoil.repositories.UserRepository;
import com.devrassicpark.midnightoil.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    @Override
    public void run(String... args) throws Exception {

        if(userRepository.count() == 0){

            User admin = User
                    .builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userService.save(admin);
            log.debug("Admin user created - {}", admin);
        }
    }
}
