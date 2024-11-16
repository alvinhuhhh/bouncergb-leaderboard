package com.imaginationoverboard.bouncergbleaderboard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("leaderboard")
public class LeaderboardConfig {

    private long maxEntries;
}