package com.imaginationoverboard.bouncergbleaderboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imaginationoverboard.bouncergbleaderboard.domain.LeaderboardEntry;
import com.imaginationoverboard.bouncergbleaderboard.service.LeaderboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"classpath:scripts/schema.sql", "classpath:scripts/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private LeaderboardRepository repository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String readFile(String classpath) throws IOException {
        InputStream inputStream = new ClassPathResource(classpath).getInputStream();
        return new String(inputStream.readAllBytes());
    }

    @Test
    @WithMockUser
    void getTopLeaderboard() throws Exception {
        String expected = readFile("expected/leaderboard-top-5.json");

        // Call endpoint under test
        mockMvc.perform(get("/top"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser
    void getTopLeaderboardWithEntriesLimit() throws Exception {
        String expected = readFile("expected/leaderboard-top-7.json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("entries", "7");

        // Call endpoint under test
        mockMvc.perform(get("/top").params(params))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void getTopLeaderboardUnauthorized() throws Exception {
        // Call endpoint under test
        mockMvc.perform(get("/top"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void insertLeaderboardEntry() throws Exception {
        LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
        leaderboardEntry.setPlayerName("John");
        leaderboardEntry.setTimeScore(50.5F);
        String content = objectMapper.writeValueAsString(leaderboardEntry);

        // Call endpoint under test
        MvcResult result = mockMvc.perform(post("/insert").contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        // Assertions
        MockHttpServletResponse response = result.getResponse();
        Assertions.assertNotNull(response.getHeader("Location"));
    }

    @Test
    @WithMockUser
    void insertInvalidLeaderboardEntry() throws Exception {
        // Call endpoint under test
        mockMvc.perform(post("/insert").contentType(MediaType.APPLICATION_JSON_VALUE).content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    void insertLeaderboardUnauthorized() throws Exception {
        LeaderboardEntry leaderboardEntry = new LeaderboardEntry();
        leaderboardEntry.setPlayerName("John");
        leaderboardEntry.setTimeScore(50.5F);
        String content = objectMapper.writeValueAsString(leaderboardEntry);

        // Call endpoint under test
        mockMvc.perform(post("/insert").contentType(MediaType.APPLICATION_JSON_VALUE).content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void insertLeaderboardEntryWithDelete() throws Exception {
        LeaderboardEntry leaderboardEntry1 = new LeaderboardEntry();
        leaderboardEntry1.setPlayerName("John");
        leaderboardEntry1.setTimeScore(50.5F);
        String content1 = objectMapper.writeValueAsString(leaderboardEntry1);

        LeaderboardEntry leaderboardEntry2 = new LeaderboardEntry();
        leaderboardEntry2.setPlayerName("Alice");
        leaderboardEntry2.setTimeScore(51.5F);
        String content2 = objectMapper.writeValueAsString(leaderboardEntry2);

        // Insert 1 record
        mockMvc.perform(post("/insert").contentType(MediaType.APPLICATION_JSON_VALUE).content(content1))
                .andExpect(status().isCreated());
        Assertions.assertEquals(9, repository.count());

        // Insert another record
        mockMvc.perform(post("/insert").contentType(MediaType.APPLICATION_JSON_VALUE).content(content2))
                .andExpect(status().isCreated());
        Assertions.assertEquals(9, repository.count());
        Assertions.assertFalse(repository.findById(UUID.fromString("c3e6dcad-1187-4256-ba20-fe4162e0289b")).isPresent());
    }
}