package com.imaginationoverboard.bouncergbleaderboard.service;

import com.imaginationoverboard.bouncergbleaderboard.config.LeaderboardConfig;
import com.imaginationoverboard.bouncergbleaderboard.domain.HighscoreList;
import com.imaginationoverboard.bouncergbleaderboard.domain.LeaderboardEntry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service methods for leaderboard
 *
 * @author Alvin Tan (AT)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardConfig config;

    private final LeaderboardRepository repository;

    public HighscoreList getHighscoreList(Optional<Integer> entries) {
        List<LeaderboardEntry> results = repository
                .findByOrderByLevelDescTimeScoreAsc(Limit.of(entries.orElse(5)));
        return new HighscoreList(results);
    }

    @Transactional
    public String insertHighscore(LeaderboardEntry entry) {
        LeaderboardEntry leaderboardEntry = repository.save(entry);

        if (repository.count() > config.getMaxEntries()) {
            log.warn("Deleting oldest record in database...");
            repository.deleteByOrderByCreatedAtAsc(Limit.of(1));
        }

        return leaderboardEntry.getId().toString();
    }
}
