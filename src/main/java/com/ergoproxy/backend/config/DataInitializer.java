package com.ergoproxy.backend.config;

import com.ergoproxy.backend.model.Role;
import com.ergoproxy.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoles("ADMIN", "Admin for system management");
        createRoles("USER", "Regular user with limited access");
    }

    private void createRoles(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(
                    Role.builder()
                            .name(name)
                            .description(description)
                            .build()
            );
            log.info("Created role with name: " + name);
        }

    }
}
