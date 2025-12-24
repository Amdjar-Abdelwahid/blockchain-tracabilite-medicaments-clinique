package com.myorg.tracemed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.tracemed.dto.AuthenticationRequest;
import com.myorg.tracemed.dto.AuthenticationResponse;
import com.myorg.tracemed.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void shouldRegisterAndLoginAndAccessProtectedResource() throws Exception {
                // 1. Try to access protected resource -> 404 Not Found (audit endpoint needs existing colis)
                mockMvc.perform(get("/api/audit/colis-test"))
                                .andExpect(status().isNotFound());

                // 2. Register
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .username("testuser")
                                .password("password123")
                                .email("test@example.com")
                                .role(com.myorg.tracemed.entity.Role.ADMIN)
                                .nomComplet("Test User")
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk());

                // 3. Login
                AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                                .username("testuser")
                                .password("password123")
                                .build();

                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String responseContent = loginResult.getResponse().getContentAsString();
                AuthenticationResponse authResponse = objectMapper.readValue(responseContent,
                                AuthenticationResponse.class);
                String token = authResponse.getToken();

                // 4. Access protected resource with token
                // Should return 404 (Not Found) because "colis-test" doesn't exist,
                // effectively proving that we bypassed the 403 Forbidden check.
                mockMvc.perform(get("/api/audit/colis-test")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isNotFound());
        }
}
