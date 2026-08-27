package com.campusgig.backend;

import com.campusgig.backend.dto.AuthResponse;
import com.campusgig.backend.dto.LoginRequest;
import com.campusgig.backend.dto.RegisterRequest;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.UserRepository;
import com.campusgig.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("test" + System.currentTimeMillis() + "@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("0711111111");
    }

    @Test
    void testRegister() {
        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response.getToken());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("STUDENT", response.getRole());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void testRegisterDuplicateEmail() {
        authService.register(registerRequest);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegisterDuplicatePhone() {
        authService.register(registerRequest);

        RegisterRequest duplicatePhone = new RegisterRequest();
        duplicatePhone.setFirstName("Another");
        duplicatePhone.setLastName("User");
        duplicatePhone.setEmail("different@test.com");
        duplicatePhone.setPassword("password123");
        duplicatePhone.setPhone("0711111111");

        assertThrows(RuntimeException.class, () -> authService.register(duplicatePhone));
    }

    @Test
    void testLogin() {
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword("password123");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals(registerRequest.getEmail(), response.getEmail());
    }

    @Test
    void testLoginWrongPassword() {
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword("wrongpassword");

        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    @Test
    void testPasswordHashed() {
        authService.register(registerRequest);

        User user = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();
        assertNotEquals("password123", user.getPassword());
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }
}