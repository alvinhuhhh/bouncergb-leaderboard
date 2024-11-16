package com.imaginationoverboard.bouncergbleaderboard.service;

import com.imaginationoverboard.bouncergbleaderboard.domain.LeaderboardEntry;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, UUID> {

    List<LeaderboardEntry> findByOrderByLevelDescTimeScoreAsc(Limit limit);

    LeaderboardEntry save(LeaderboardEntry leaderboardEntry);
}
