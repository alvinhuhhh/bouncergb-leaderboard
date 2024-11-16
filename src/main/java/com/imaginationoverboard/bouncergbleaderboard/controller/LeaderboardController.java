package com.imaginationoverboard.bouncergbleaderboard.controller;

import com.imaginationoverboard.bouncergbleaderboard.domain.HighscoreList;
import com.imaginationoverboard.bouncergbleaderboard.domain.LeaderboardEntry;
import com.imaginationoverboard.bouncergbleaderboard.service.LeaderboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

/**
 * REST API Controller exposing methods to get leaderboard data
 *
 * @author Alvin Tan (AT)
 */
@Slf4j
@CrossOrigin
@RestController
@AllArgsConstructor
public class LeaderboardController {

    private final LeaderboardService service;

    @GetMapping("/top")
    public ResponseEntity<HighscoreList> getTop(
            @RequestParam
            Optional<Integer> entries
    ) {
        try {
            log.info("[top] Request received with entries limit: " + entries);

            HighscoreList highscoreList = service.getHighscoreList(entries);

            log.info("[top] Found number of entries: " + highscoreList.getHighscoreList().size());

            return ResponseEntity.status(HttpStatus.OK).body(highscoreList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertEntry(
            @RequestBody
            final LeaderboardEntry leaderboardEntry
    ) {
        try {
            log.info("[insert] Request received");

            String id = service.insertHighscore(leaderboardEntry);

            log.info(String.format("[insert] Inserted entry with UUID: [%s]", id));

            return ResponseEntity.created(new URI(id)).build();
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
