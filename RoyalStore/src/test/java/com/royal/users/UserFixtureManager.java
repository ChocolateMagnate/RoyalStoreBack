package com.royal.users;

import com.royal.FixtureInitializer;
import com.royal.users.domain.User;
import com.royal.users.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Order(1)
@Component
public class UserFixtureManager extends FixtureInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserFixtureManager(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ArrayList<User> users = getAllFixtureUsers();
        this.userRepository.saveAll(users);
    }

    @PreDestroy
    public void teardown() {
        this.userRepository.deleteAll();
    }

    public User getFixtureUser() throws IOException {
        ArrayList<User> users = getAllFixtureUsers();
        return users.get(0);
    }

    private @NotNull ArrayList<User> getAllFixtureUsers() throws IOException {
        ArrayList<User> users = loadObjectsFromFixture("fixtures/users.yaml", User.class);
        //Since the loadObjectsFromFixture method assigned the raw passwords into the users, we
        //need to use the password encoder to hash the passwords to mimic real production setting.
        for (User user : users) user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return users;
    }
}
