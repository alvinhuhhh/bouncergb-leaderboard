package com.imaginationoverboard.bouncergbleaderboard.controller;

import com.imaginationoverboard.bouncergbleaderboard.domain.LeaderboardEntry;
import com.imaginationoverboard.bouncergbleaderboard.service.LeaderboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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

    private final LeaderboardRepository repository;

    @GetMapping("/top")
    public ResponseEntity<List<LeaderboardEntry>> getTop(
            @RequestParam
            Optional<Integer> entries
    ) {
        try {
            log.info("[top] Request received with entries limit: " + entries);

            List<LeaderboardEntry> results = repository
                    .findByOrderByLevelDescTimeScoreAsc(Limit.of(entries.orElse(5)));

            log.info("[top] Found number of entries: " + results.size());

            return ResponseEntity.status(HttpStatus.OK).body(results);
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

            LeaderboardEntry savedLeaderboardEntry = repository.save(leaderboardEntry);
            URI uuid = new URI(savedLeaderboardEntry.getId().toString());

            log.info(String.format("[insert] Inserted entry with UUID: [%s]", savedLeaderboardEntry.getId()));

            return ResponseEntity.created(uuid).build();
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
